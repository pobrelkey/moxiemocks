/*
 * Copyright (c) 2010-2012 Moxie contributors
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
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.NewInvocationControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;

class CGLIBProxyFactory<T> extends ProxyFactory<T> {

    static private boolean haveObjenesis = false;
    static private boolean havePowermock = false;
    private static ObjenesisStd objenesis;
    static {
        try {
            objenesis = new ObjenesisStd(true);
            haveObjenesis = true;
        } catch (NoClassDefFoundError e) {
            // oh well, no objenesis then.
        }
        try {
            new MemberModifier();
            havePowermock = true;
        } catch (NoClassDefFoundError e) {
            // oh well, no powermock then.
        }
    }

    private static Map<Object, MethodIntercept> proxyIntercepts = Collections.synchronizedMap(new WeakIdentityMap<Object, MethodIntercept>());

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
        if (!haveObjenesis || constructorArgTypes != null || constructorArgs != null) {
            try {
                Constructor constructor;
                if (constructorArgTypes == null && constructorArgs != null && constructorArgs.length > 0) {
                    constructor = MoxieUtils.guessConstructor(enhancedClass, constructorArgTypes, constructorArgs).getConstructor();
                } else {
                    constructor = enhancedClass.getConstructor(constructorArgTypes);
                }
                constructor.setAccessible(true);
                result = (T) constructor.newInstance(constructorArgs);
            } catch (InstantiationException e) {
                throw new MoxieUnexpectedError(e);
            } catch (IllegalAccessException e) {
                throw new MoxieUnexpectedError(e);
            } catch (InvocationTargetException e) {
                throw new MoxieUnexpectedError(e.getTargetException());
            } catch (NoSuchMethodException e) {
                if (!haveObjenesis && (constructorArgTypes == null || constructorArgTypes.length == 0) && (constructorArgs == null || constructorArgs.length == 0)) {
                    throw new IllegalArgumentException("To mock concrete types that don't have no-arg constructors, either pass constructor arguments or add Objenesis to the classpath");
                }
                throw new MoxieUnexpectedError(e);
            } catch (MoxieUtils.NoMethodFoundException e) {
                if (!haveObjenesis && (constructorArgTypes == null || constructorArgTypes.length == 0) && (constructorArgs == null || constructorArgs.length == 0)) {
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
                return methodIntercept.intercept(proxy, new MethodAdapter(method), args, new MethodIntercept.SuperInvoker() {
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

        proxyIntercepts.put(result, methodIntercept);
        return result;
    }

    private static final InvocationHandler POWERMOCK_INVOCATION_HANDLER = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Modifier.isStatic(method.getModifiers())) {
                proxy = method.getDeclaringClass();
            }
            MethodIntercept methodIntercept = proxyIntercepts.get(proxy);
            if (methodIntercept == null) {
                throw new MoxieZombieMethodInvocationError("cannot partially mock a static or final method");
            }
            return methodIntercept.intercept(proxy, new MethodAdapter(method), args, ZOMBIE_METHOD_SUPER_INVOKER);
        }
    };

    private static final MethodIntercept.SuperInvoker ZOMBIE_METHOD_SUPER_INVOKER = new ZombieSuperInvoker("cannot partially mock a static or final method");
    private static final MethodIntercept.SuperInvoker ZOMBIE_CONSTRUCTOR_SUPER_INVOKER = new ZombieSuperInvoker("cannot partially mock a constructor");

    private static class ZombieSuperInvoker implements MethodIntercept.SuperInvoker {
        private final String errorMessage;

        public ZombieSuperInvoker(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Object invokeSuper(Object[] args) throws Throwable {
            throw new MoxieZombieMethodInvocationError(errorMessage);
        }
    }

    private static final NewInvocationControl POWERMOCK_CONSTRUCTOR_HANDLER = new NewInvocationControl() {
        public Object invoke(Class type, Object[] args, Class[] sig) throws Exception {
            try {
                return proxyIntercepts.get(type).intercept(type, new ConstructorAdapter(type.getDeclaredConstructor(sig)), args, ZOMBIE_CONSTRUCTOR_SUPER_INVOKER);
            } catch (Exception e) {
                throw e;
            } catch (Error e) {
                throw e;
            } catch (Throwable e) {
                throw new MoxieUnexpectedError(e);
            }
        }

        public Object expectSubstitutionLogic(Object... arguments) throws Exception {
            throw new UnsupportedOperationException("EasyMock/Mockito operations on mocks are not supported by the Moxie invocation handler");
        }

        public Object replay(Object... mocks) {
            throw new UnsupportedOperationException("EasyMock/Mockito operations on mocks are not supported by the Moxie invocation handler");
        }

        public Object verify(Object... mocks) {
            throw new UnsupportedOperationException("EasyMock/Mockito operations on mocks are not supported by the Moxie invocation handler");
        }

        public Object reset(Object... mocks) {
            throw new UnsupportedOperationException("EasyMock/Mockito operations on mocks are not supported by the Moxie invocation handler");
        }
    };

    static void registerClassInterception(ClassInterception interception) {
        proxyIntercepts.put(interception.getInterceptedClass(), interception);
    }

    static void zombify(Constructor constructor) {
        if (!havePowermock) {
            throw new UnsupportedOperationException("add powermock-api-support to the classpath to enable mocking of constructors");
        }
        MockRepository.putNewInstanceControl(constructor.getDeclaringClass(), POWERMOCK_CONSTRUCTOR_HANDLER);
    }

    static void zombify(Method method) {
        if (!havePowermock) {
            throw new UnsupportedOperationException("add powermock-api-support to the classpath to enable mocking of static/final methods");
        }
        org.powermock.api.support.membermodification.MemberModifier.replace(method).with(POWERMOCK_INVOCATION_HANDLER);
    }

}
