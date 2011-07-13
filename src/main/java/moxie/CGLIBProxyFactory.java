/*
 * Copyright (c) 2010-2011 Moxie contributors
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

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class CGLIBProxyFactory<T> extends ProxyFactory<T> {

    static private boolean haveObjenesis = false;
    private static ObjenesisStd objenesis;
    static {
        try {
            objenesis = new ObjenesisStd(true);
            haveObjenesis = true;
        } catch (NoClassDefFoundError e) {
            // oh well, no objenesis then.
        }
    }

    private static class TrivialSubclassOfObjectToWorkAroundCGLIBBug {
        public TrivialSubclassOfObjectToWorkAroundCGLIBBug() {}
    }

    private final Class enhancedClass;
    private ObjectInstantiator objenesisInstantiator;

    @SuppressWarnings("unchecked")
    CGLIBProxyFactory(Class<T> clazz, Class[] ancillaryTypes) {
        if (!haveObjenesis && clazz.getDeclaringClass() != null && !Modifier.isStatic(clazz.getModifiers())) {
            throw new IllegalArgumentException("Cannot mock a non-static inner class (" + clazz.getName() + ") - add Objenesis to the classpath to get around this");
        }
        if (clazz == Object.class) {
            clazz = (Class<T>) TrivialSubclassOfObjectToWorkAroundCGLIBBug.class;
        }
        Enhancer e = new Enhancer();
        e.setClassLoader(clazz.getClassLoader());
        e.setSuperclass(clazz);
        e.setInterfaces(ancillaryTypes);
        e.setUseFactory(true);
        e.setUseCache(true);
        e.setCallbackType(MethodInterceptor.class);
        enhancedClass = e.createClass();
        if (haveObjenesis) {
            objenesisInstantiator = objenesis.getInstantiatorOf(enhancedClass);
        }
    }

    @SuppressWarnings("unchecked")
    T createProxy(final MethodIntercept methodIntercept, Class[] constructorArgTypes, Object[] constructorArgs) {
        T result;
        if (!haveObjenesis || constructorArgTypes != null) {
            try {
                result = (T) enhancedClass.getConstructor(constructorArgTypes).newInstance(constructorArgs);
            } catch (InstantiationException e) {
                throw new MoxieUnexpectedError(e);
            } catch (IllegalAccessException e) {
                throw new MoxieUnexpectedError(e);
            } catch (InvocationTargetException e) {
                throw new MoxieUnexpectedError(e.getTargetException());
            } catch (NoSuchMethodException e) {
                if (!haveObjenesis && (constructorArgTypes == null || constructorArgTypes.length == 0)) {
                    throw new IllegalArgumentException("To mock concrete types that don't have no-arg constructors, either pass constructor arguments or add Objenesis to the classpath");
                }
                throw new MoxieUnexpectedError(e);
            }
        } else {
            // no specific constructor requested and have Objenesis, so use that instead.
            result = (T) objenesisInstantiator.newInstance();
        }

        MethodInterceptor methodInterceptor = new MethodInterceptor() {
            public Object intercept(final Object proxy, Method method, Object[] args, final MethodProxy superProxy) throws Throwable {
                return methodIntercept.intercept(proxy, method, args, new MethodIntercept.SuperInvoker() {
                    public Object invokeSuper(Object[] superArgs) throws Throwable {
                        return superProxy.invokeSuper(proxy, superArgs);
                    }
                });
            }
        };
        Factory factory = (Factory) result;
        int callbackCount = factory.getCallbacks().length;
        for(int i = 0; i < callbackCount; i++) {
            factory.setCallback(i, methodInterceptor);
        }

        return result;
    }
}
