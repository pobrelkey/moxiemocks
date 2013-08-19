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

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.Method;

class JavassistProxyFactory<T> extends ConcreteTypeProxyFactory<T> {

    JavassistProxyFactory(Class<T> originalClass, Class[] ancillaryTypes) {
        super(originalClass, ancillaryTypes);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<? extends T> createEnhancedClass(Class<T> clazz, Class[] ancillaryTypes) {
        ProxyFactory f = new ProxyFactory();
        f.setSuperclass(clazz);
        if (ancillaryTypes != null) {
            f.setInterfaces(ancillaryTypes);
        }
        return f.createClass();
    }

    @Override
    protected void decorateInstance(T result, final MethodIntercept methodIntercept) {
        MethodHandler mi = new MethodHandler() {
            public Object invoke(final Object proxy, final Method thisMethod, final Method proceed, Object[] args) throws Throwable {
                return methodIntercept.intercept(proxy, new MethodAdapter(thisMethod), args, new MethodIntercept.SuperInvoker() {
                    public Object invokeSuper(Object[] args) throws Throwable {
                        if (proceed != null) {
                            return proceed.invoke(proxy, args);
                        } else {
                            throw new MoxieZombieMethodInvocationError("Method not implemented in superclass: " + thisMethod.getName());
                        }
                    }
                });
            }
        };
        // weird code to play nice with pre-3.16 version of Javassist...
        if (result instanceof ProxyObject) {
            ((ProxyObject) result).setHandler(mi);
        } else {
            ((Proxy) result).setHandler(mi);
        }
    }

}
