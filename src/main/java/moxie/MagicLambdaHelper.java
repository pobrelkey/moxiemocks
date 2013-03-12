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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

abstract class MagicLambdaHelper {

    static Method RUNNABLE_METHOD;

    static {
        try {
            MagicLambdaHelper.RUNNABLE_METHOD = Runnable.class.getMethod("run");
        } catch (NoSuchMethodException e) {
            throw new MoxieUnexpectedError(e);
        }
    }

    private final MoxieControlImpl moxie;
    private final ProxyIntercepts proxyIntercepts;

    protected MagicLambdaHelper(MoxieControlImpl moxie) {
        this.moxie = moxie;
        proxyIntercepts = ProxyIntercepts.getInstance();
    }

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
                        constructorAdapter.zombify();
                        MethodIntercept expectationIntercept = getLambdaInterceptForClass(moxie.getInterceptionFromClass(constructorAdapter.getDeclaringClass()));
                        proxyIntercepts.registerThreadLocalClassIntercept(constructorAdapter.getDeclaringClass(), expectationIntercept);
                        lambdaMethod.invoke(lambdaObject);
                        proxyIntercepts.clearThreadLocalClassIntercept(constructorAdapter.getDeclaringClass());
                        return;
                    }
                    // FALL THROUGH

                case Opcode.INVOKEINTERFACE:
                case Opcode.INVOKEVIRTUAL:
                    // Calling an instance method...
                    MethodAdapter instanceMethod = guessMethod(classPool, constPool, descriptorRef, false, lastInvokeOpcode == Opcode.INVOKEINTERFACE);
                    instanceMethod.zombify();

                    @SuppressWarnings("unchecked")
                    List list = moxie.proxiesForClass(instanceMethod.getDeclaringClass());
                    MethodIntercept lambdaInterceptForObject = getLambdaInterceptForObject();
                    for (Object proxy : list) {
                        proxyIntercepts.registerThreadLocalIntercept(proxy, lambdaInterceptForObject);
                    }
                    lambdaMethod.invoke(lambdaObject);
                    for (Object proxy : list) {
                        proxyIntercepts.clearThreadLocalIntercept(proxy);
                    }
                    return;

                case Opcode.INVOKESTATIC:
                    // Calling a static method...
                    MethodAdapter staticMethod = guessMethod(classPool, constPool, descriptorRef, true, false);
                    staticMethod.zombify();
                    MethodIntercept expectationIntercept = getLambdaInterceptForClass(moxie.getInterceptionFromClass(staticMethod.getDeclaringClass()));
                    proxyIntercepts.registerThreadLocalClassIntercept(staticMethod.getDeclaringClass(), expectationIntercept);
                    lambdaMethod.invoke(lambdaObject);
                    proxyIntercepts.clearThreadLocalClassIntercept(staticMethod.getDeclaringClass());
                    return;

                case Opcode.INVOKEDYNAMIC:
                    // TODO: makes my brain hurt
                    throw new MoxieSyntaxError("Magic lambdas cannot use dynamic invocation");

                default:
                    throw new MoxieSyntaxError("Cannot detect a method invocation in magic lambda");
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

    protected abstract MethodIntercept getLambdaInterceptForClass(ClassInterception classInterception);

    protected abstract MethodIntercept getLambdaInterceptForObject();

    private static ConstructorAdapter guessConstructor(ClassPool classPool, ConstPool constPool, int descriptorRef) throws ClassNotFoundException, NotFoundException {
        String magicMethodClassName = constPool.getMethodrefClassName(descriptorRef);
        String magicMethodDescriptor = constPool.getMethodrefType(descriptorRef);
        Class magicMethodClass = Class.forName(magicMethodClassName);
        CtClass[] magicMethodParamCtTypes = Descriptor.getParameterTypes(magicMethodDescriptor, classPool);
        Class[] magicMethodParamTypes = new Class[magicMethodParamCtTypes.length];
        for (int i = 0; i < magicMethodParamCtTypes.length; i++) {
            magicMethodParamTypes[i] = Class.forName(magicMethodParamCtTypes[i].getName());
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
            magicMethodParamTypes[i] = Class.forName(magicMethodParamCtTypes[i].getName());
        }

        return MoxieUtils.guessMethod(magicMethodClass, magicMethodName, isStatic, magicMethodParamTypes, null);
    }
}
