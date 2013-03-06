/*
 * Copyright (c) 2010-2013 Moxie contributors
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

import java.lang.reflect.Method;

class CGLIBProxyFactory<T> extends ConcreteTypeProxyFactory<T> {

    private static class TrivialSubclassOfObjectToWorkAroundCGLIBBug {
        public TrivialSubclassOfObjectToWorkAroundCGLIBBug() {}
    }

    CGLIBProxyFactory(Class<T> clazz, Class[] ancillaryTypes) {
        super(clazz, ancillaryTypes);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<? extends T> createEnhancedClass(Class<T> clazz, Class[] ancillaryTypes) {
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
        return e.createClass();
    }

    @Override
    protected void decorateInstance(T result, final MethodIntercept methodIntercept) {
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
    }

}
