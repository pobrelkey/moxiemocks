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

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

class JavassistProxyFactory<T> extends ConcreteTypeProxyFactory<T> {

    private static final Random random = new Random();
    private static final String INVOCATION_HANDLER_FIELD_NAME = "$MoxieInvocationHandler$";
    private Field methodInterceptField;
    private final Class<T> originalClass;

    JavassistProxyFactory(Class<T> originalClass, Class[] ancillaryTypes) {
        super(originalClass, ancillaryTypes);
        this.originalClass = originalClass;
    }

    @Override
    protected Class<? extends T> createEnhancedClass(Class<T> clazz, Class[] ancillaryTypes) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass original = null;
            CtClass proxyClass;
            List<CtClass> allImplementedClasses = new ArrayList<CtClass>();

            String proxyClassName = "$MoxieProxy$" + Long.toHexString(random.nextLong());
            if (clazz != null) {
                original = classPool.get(clazz.getName());
                proxyClass = classPool.makeClass(clazz.getName() + proxyClassName, original);
                allImplementedClasses.add(original);
            } else {
                proxyClass = classPool.makeClass(proxyClassName);
            }

            for (Class ancillaryType : ancillaryTypes) {
                CtClass ancillaryCtClass = classPool.get(ancillaryType.getName());
                proxyClass.addInterface(ancillaryCtClass);
                allImplementedClasses.add(ancillaryCtClass);
            }

            CtClass invocationHandlerClass = classPool.get(InvocationHandler.class.getName());
            CtField invocationHandlerField = new CtField(invocationHandlerClass, INVOCATION_HANDLER_FIELD_NAME, proxyClass);
            invocationHandlerField.setModifiers(AccessFlag.PUBLIC);
            proxyClass.addField(invocationHandlerField);

            Set<CtClass> classesToCheck = new HashSet<CtClass>();
            for (CtClass implementedClass : allImplementedClasses) {
                for (; implementedClass != null && !classesToCheck.contains(implementedClass); implementedClass = implementedClass.getSuperclass()) {
                    classesToCheck.add(implementedClass);
                }
            }
            for (CtClass ctClass : classesToCheck) {
                for (CtMethod originalMethod : ctClass.getMethods()) {
                    MethodInfo methodInfo = originalMethod.getMethodInfo();
                    if (!methodInfo.isMethod() || (methodInfo.getAccessFlags() & (AccessFlag.FINAL | AccessFlag.PRIVATE | AccessFlag.STATIC)) != 0) {
                        continue;
                    }
                    CtMethod proxyMethod = new CtMethod(originalMethod.getReturnType(), originalMethod.getName(), originalMethod.getParameterTypes(), proxyClass);
                    proxyMethod.setModifiers(originalMethod.getModifiers());
                    proxyClass.addMethod(proxyMethod);

                    StringBuilder methodBody = new StringBuilder();
                    methodBody.append('{');
                    if (originalMethod.getReturnType() != CtClass.voidType) {
                        methodBody.append("return ");
                    }
                    methodBody.append("$0.");
                    methodBody.append(INVOCATION_HANDLER_FIELD_NAME);
                    methodBody.append(".invoke($0, $0.getClass().getMethod(\"");
                    methodBody.append(proxyMethod.getName());
                    methodBody.append("\", $sig), $args); }");
                    proxyMethod.setBody(methodBody.toString());
                }
            }

            @SuppressWarnings("unchecked")
            Class<? extends T> result = proxyClass.toClass();
            try {
                methodInterceptField = result.getField(INVOCATION_HANDLER_FIELD_NAME);
                methodInterceptField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new MoxieUnexpectedError(e);
            }
            return result;
        } catch (NotFoundException e) {
            throw new MoxieUnexpectedError(e);
        } catch (CannotCompileException e) {
            throw new MoxieUnexpectedError(e);
        }
    }

    @Override
    protected void decorateInstance(T result, final MethodIntercept methodIntercept) {
        try {
            methodInterceptField.set(result, new MethodInterceptAdapter(originalClass, methodIntercept));
        } catch (IllegalAccessException e) {
            throw new MoxieUnexpectedError(e);
        }
    }

}
