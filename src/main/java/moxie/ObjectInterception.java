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

abstract class ObjectInterception<T> extends Interception {
    private final Class[] constructorArgTypes;
    private final Object[] constructorArgs;
    protected T proxy;
    private ProxyFactory<T> proxyFactory;

    protected ObjectInterception(Class<T> clazz, String name, MoxieFlags flags, InstantiationStackTrace instantiationStackTrace, Class[] constructorArgTypes, Object[] constructorArgs) {
        super(clazz, name, flags, instantiationStackTrace);
        this.constructorArgTypes = constructorArgTypes;
        this.constructorArgs = constructorArgs;
    }

    T getProxy() {
        if (proxy == null) {
            proxy = getProxyFactory().createProxy(this, constructorArgTypes, constructorArgs);
        }
        return proxy;
    }

    @SuppressWarnings("unchecked")
    ProxyFactory<T> getProxyFactory() {
        if (proxyFactory == null) {
            proxyFactory = ProxyFactory.create(clazz);
        }
        return proxyFactory;
    }

    Class[] getConstructorArgTypes() {
        return constructorArgTypes;
    }

    Object[] getConstructorArgs() {
        return constructorArgs;
    }

    ObjectExpectationImpl<T> expect() {
        return new ObjectExpectationImpl<T>(this);
    }

    ObjectCheckImpl<T> check() {
        return new ObjectCheckImpl<T>(this, invocations);
    }

}
