/*
 * Copyright (c) 2011-2013 Moxie contributors
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

class JDKProxyFactory<T> extends ProxyFactory<T> {
    private Constructor<T> constructor;

    @SuppressWarnings("unchecked")
    public JDKProxyFactory(Class<T> clazz, Class[] ancillaryTypes) {
        ArrayList<Class> interfaces = new ArrayList(Arrays.asList(ancillaryTypes));
        if (clazz != null) {
            interfaces.add(0, clazz);
        }
        try {
            constructor = (Constructor<T>) Proxy.getProxyClass(interfaces.get(0).getClassLoader(), interfaces.toArray(new Class[interfaces.size()])).getConstructor(InvocationHandler.class);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new MoxieUnexpectedError(e);
        }
    }

    @Override
    T createProxy(final MethodIntercept methodIntercept, Class[] constructorArgTypes, Object[] constructorArgs) {
        try {
            return constructor.newInstance(new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return methodIntercept.intercept(proxy, new MethodAdapter(method), args, new ZombieSuperInvoker("This is an interface mock - there are no superclass methods to invoke"));
                }
            });
        } catch (InstantiationException e) {
            throw new MoxieUnexpectedError(e);
        } catch (IllegalAccessException e) {
            throw new MoxieUnexpectedError(e);
        } catch (InvocationTargetException e) {
            throw new MoxieUnexpectedError(e);
        }
    }
}
