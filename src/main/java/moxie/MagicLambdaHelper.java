/*
 * Copyright (c) 2013 Moxie contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package moxie;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

abstract class MagicLambdaHelper {

    static Method RUNNABLE_METHOD;
    static Method SUPPLIER_METHOD;

    static {
        try {
            RUNNABLE_METHOD = Runnable.class.getMethod("run");
            SUPPLIER_METHOD = Supplier.class.getMethod("get");
        } catch (NoSuchMethodException e) {
            throw new MoxieUnexpectedError(e);
        }
    }

    private final MoxieControlImpl moxie;
    private int invocationCount = 0;

    protected MagicLambdaHelper(MoxieControlImpl moxie) {
        this.moxie = moxie;
    }

    @SuppressWarnings("unchecked")
    void doInvoke(Object lambdaObject, Method lambdaMethod) {
        Class lambdaClass = lambdaObject.getClass();
        try {
            // Decompile the lambda method and find the last method invocation instruction -
            // this will (or had better!) call the method to be mocked.
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(lambdaClass.getName());
            CtClass ctReturnType = classPool.get(lambdaMethod.getReturnType().getName());
            Class<?>[] parameterTypes = lambdaMethod.getParameterTypes();
            CtClass[] ctParamTypes = new CtClass[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                ctParamTypes[i] = classPool.get(parameterTypes[i].getName());
            }
            CtMethod lambdaCtMethod = ctClass.getMethod(lambdaMethod.getName(), Descriptor.ofMethod(ctReturnType, ctParamTypes));
            MethodInfo methodInfo = lambdaCtMethod.getMethodInfo();
            CodeIterator iterator = methodInfo.getCodeAttribute().iterator();
            int lastInvokeIndex = -1, lastInvokeOpcode = -1;
            while (iterator.hasNext()) {
                int index = iterator.next();
                int op = iterator.byteAt(index);
                switch (op) {
                    case Opcode.INVOKEDYNAMIC:
                    case Opcode.INVOKEINTERFACE:
                    case Opcode.INVOKESPECIAL:
                    case Opcode.INVOKESTATIC:
                    case Opcode.INVOKEVIRTUAL:
                        lastInvokeIndex = index;
                        lastInvokeOpcode = op;
                }
            }
            if (lastInvokeIndex == -1) {
                throw new MoxieSyntaxError("Cannot detect a method invocation in magic lambda");
            }

            // Figure out what's being called (method/constructor), and act accordingly.
            int descriptorRef = (iterator.byteAt(lastInvokeIndex + 1) << 8) | iterator.byteAt(lastInvokeIndex + 2);
            ConstPool constPool = ctClass.getClassFile().getConstPool();
            switch (lastInvokeOpcode) {
                case Opcode.INVOKESPECIAL:
                    // Perhaps calling a constructor?
                    String magicMethodName = constPool.getMethodrefName(descriptorRef);
                    if ("<init>".equals(magicMethodName)) {
                        ConstructorAdapter constructorAdapter = guessConstructor(classPool, constPool, descriptorRef);
                        registerThreadLocalClassIntercept(constructorAdapter.getDeclaringClass());
                        constructorAdapter.zombify();
                        try {
                            lambdaMethod.invoke(lambdaObject);
                        } finally {
                            moxie.getInterceptionFromProxy(constructorAdapter.getDeclaringClass()).clearThreadLocalHandler();
                        }
                        break;
                    }
                    // FALL THROUGH

                case Opcode.INVOKEINTERFACE:
                case Opcode.INVOKEVIRTUAL:
                    // Calling an instance method...
                    {
                        MethodAdapter instanceMethod = guessMethod(classPool, constPool, descriptorRef, false, lastInvokeOpcode == Opcode.INVOKEINTERFACE);
                        @SuppressWarnings("unchecked")
                        List possibleProxies = moxie.getProxiesForClass(instanceMethod.getDeclaringClass());
                        MethodIntercept lambdaInterceptForObject = incrementingMethodIntercept(getLambdaInterceptForObject());
                        for (Object proxy : possibleProxies) {
                            moxie.getInterceptionFromProxy(proxy).registerThreadLocalHandler(lambdaInterceptForObject);
                        }
                        instanceMethod.zombify();
                        try {
                            lambdaMethod.invoke(lambdaObject);
                        } finally {
                            for (Object proxy : possibleProxies) {
                                moxie.getInterceptionFromProxy(proxy).clearThreadLocalHandler();
                            }
                        }
                        break;
                    }

                case Opcode.INVOKESTATIC:
                    // Calling a static method...
                    {
                        MethodAdapter staticMethod = guessMethod(classPool, constPool, descriptorRef, true, false);
                        registerThreadLocalClassIntercept(staticMethod.getDeclaringClass());
                        staticMethod.zombify();
                        try {
                            lambdaMethod.invoke(lambdaObject);
                        } finally {
                            moxie.getInterceptionFromClass(staticMethod.getDeclaringClass()).clearThreadLocalHandler();
                        }
                        break;
                    }

                case Opcode.INVOKEDYNAMIC:
                    // HACK: since I have no idea what the JVM is about to do, push a thread-local intercept onto every last class/proxy.
                    //   Note that this means your lambda can't call finals/statics/constructors unless they've been previously zombified.
                    //   This is of course a wild stab in the dark at proper behavior - if you ever actually use this code, please e-mail me.
                    {
                        MethodIntercept lambdaInterceptForObject = incrementingMethodIntercept(getLambdaInterceptForObject());
                        Set<Object> allProxies = moxie.getAllProxies();
                        for (Object proxy : allProxies) {
                            if (proxy instanceof Class) {
                                registerThreadLocalClassIntercept((Class) proxy);
                            } else {
                                moxie.getInterceptionFromProxy(proxy).registerThreadLocalHandler(lambdaInterceptForObject);
                            }
                        }
                        try {
                            lambdaMethod.invoke(lambdaObject);
                        } finally {
                            for (Object proxy : allProxies) {
                                moxie.getInterceptionFromProxy(proxy).clearThreadLocalHandler();
                            }
                        }
                        break;
                    }

                default:
                    throw new MoxieSyntaxError("Cannot detect a method invocation in magic lambda");
            }

            // Verify that the magic lambda indeed called a mock setup method.
            if (invocationCount == 0) {
                throw new MoxieSyntaxError("Cannot detect a method invocation in magic lambda");
            } else if (invocationCount > 1) {
                // TODO: stacktraces if MoxieOptions.TRACE?
                throw new MoxieSyntaxError("Too many method invocations (" + invocationCount + " in magic lambda");
            }

        } catch (NotFoundException e) {
            throw new MoxieUnexpectedError(e);
        } catch (BadBytecode e) {
            throw new MoxieUnexpectedError(e);
        } catch (ClassNotFoundException e) {
            throw new MoxieUnexpectedError(e);
        } catch (IllegalAccessException e) {
            throw new MoxieUnexpectedError(e);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof Error) {
                throw (Error) targetException;
            } else if (targetException instanceof RuntimeException) {
                throw (RuntimeException) targetException;
            } else {
                throw new MoxieUnexpectedError(targetException);
            }
        }

    }

    private void registerThreadLocalClassIntercept(Class clazz) {
        @SuppressWarnings("unchecked")
        ClassInterception interceptionFromClass = moxie.getInterceptionFromClass(clazz);
        MethodIntercept classIntercept = getLambdaInterceptForClass(interceptionFromClass);
        interceptionFromClass.registerThreadLocalHandler(incrementingMethodIntercept(classIntercept));
    }

    protected MethodIntercept incrementingMethodIntercept(final MethodIntercept intercept) {
        return new MethodIntercept() {
            public Object intercept(Object proxy, InvocableAdapter invocable, Object[] args, SuperInvoker superInvoker) throws Throwable {
                invocationCount++;
                return intercept.intercept(proxy, invocable, args, superInvoker);
            }
        };
    }

    protected abstract MethodIntercept getLambdaInterceptForClass(ClassInterception classInterception);

    protected abstract MethodIntercept getLambdaInterceptForObject();

    private static ConstructorAdapter guessConstructor(ClassPool classPool, ConstPool constPool, int descriptorRef) throws ClassNotFoundException, NotFoundException {
        String magicMethodClassName = constPool.getMethodrefClassName(descriptorRef);
        String magicMethodDescriptor = constPool.getMethodrefType(descriptorRef);
        Class magicMethodClass = Class.forName(magicMethodClassName);
        CtClass[] magicMethodParamCtTypes = Descriptor.getParameterTypes(magicMethodDescriptor, classPool);
        Class[] magicMethodParamTypes = new Class[magicMethodParamCtTypes.length];
        for (int i = 0; i < magicMethodParamCtTypes.length; i++) {
            magicMethodParamTypes[i] = getJvmClassForCtClass(magicMethodParamCtTypes[i]);
        }

        return MoxieUtils.guessConstructor(magicMethodClass, magicMethodParamTypes, null);
    }

    private static MethodAdapter guessMethod(ClassPool classPool, ConstPool constPool, int descriptorRef, boolean isStatic, boolean isInterface) throws ClassNotFoundException, NotFoundException {
        String magicMethodClassName, magicMethodName, magicMethodDescriptor;
        if (isInterface) {
            magicMethodClassName = constPool.getInterfaceMethodrefClassName(descriptorRef);
            magicMethodName = constPool.getInterfaceMethodrefName(descriptorRef);
            magicMethodDescriptor = constPool.getInterfaceMethodrefType(descriptorRef);
        } else {
            magicMethodClassName = constPool.getMethodrefClassName(descriptorRef);
            magicMethodName = constPool.getMethodrefName(descriptorRef);
            magicMethodDescriptor = constPool.getMethodrefType(descriptorRef);
        }
        Class magicMethodClass = Class.forName(magicMethodClassName);
        CtClass[] magicMethodParamCtTypes = Descriptor.getParameterTypes(magicMethodDescriptor, classPool);
        Class[] magicMethodParamTypes = new Class[magicMethodParamCtTypes.length];
        for (int i = 0; i < magicMethodParamCtTypes.length; i++) {
            magicMethodParamTypes[i] = getJvmClassForCtClass(magicMethodParamCtTypes[i]);
        }

        return MoxieUtils.guessMethod(magicMethodClass, magicMethodName, isStatic, magicMethodParamTypes, null);
    }

    private static Class<?> getJvmClassForCtClass(CtClass ctClass) throws ClassNotFoundException {
        if (ctClass == CtClass.booleanType) {
            return Boolean.TYPE;
        } else if (ctClass == CtClass.byteType) {
            return Byte.TYPE;
        } else if (ctClass == CtClass.charType) {
            return Character.TYPE;
        } else if (ctClass == CtClass.doubleType) {
            return Double.TYPE;
        } else if (ctClass == CtClass.floatType) {
            return Float.TYPE;
        } else if (ctClass == CtClass.intType) {
            return Integer.TYPE;
        } else if (ctClass == CtClass.longType) {
            return Long.TYPE;
        } else if (ctClass == CtClass.shortType) {
            return Short.TYPE;
        } else if (ctClass == CtClass.voidType) {
            return Void.TYPE;
        } else {
            return Class.forName(ctClass.getName());
        }
    }
}
