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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


abstract class ProxyFactory<T> {


    static private boolean haveCglib = false;
    static {
        try {
            new Enhancer();
            haveCglib = true;
        } catch (NoClassDefFoundError e) {
            // oh well, no cglib then.
        }
    }

    private final static Map<TypeKey, ProxyFactory> proxyFactories = new HashMap<TypeKey, ProxyFactory>();

    @SuppressWarnings("unchecked")
    public synchronized static <T> ProxyFactory<T> create(Class<T> clazz, Class... ancillaryTypes) {
        TypeKey tk = new TypeKey(clazz, ancillaryTypes);

        if (proxyFactories.containsKey(tk)) {
            return proxyFactories.get(tk);
        }

        ProxyFactory<T> factory;
        if (clazz == null || clazz.isInterface()) {
            factory = new JDKProxyFactory<T>(clazz, ancillaryTypes);
        } else if (!haveCglib) {
            throw new IllegalArgumentException("You must have CGLIB on the classpath to mock concrete classes");
        } else {
            factory = new CGLIBProxyFactory<T>(clazz, ancillaryTypes);
        }
        proxyFactories.put(tk, factory);
        return factory;
    }

    abstract T createProxy(final MethodIntercept methodInterceptor, Class[] constructorArgTypes, Object[] constructorArgs);

    private static class TypeKey {

        private Set<Class> interfaces;
        private Class clazz = null;

        public TypeKey(Class clazz, Class[] ancillaryTypes) {
            interfaces = new HashSet<Class>(Arrays.asList(ancillaryTypes));
            if (clazz.isInterface()) {
                interfaces.add(clazz);
            } else {
                this.clazz = clazz;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeKey typeKey = (TypeKey) o;

            if (clazz != null ? !clazz.equals(typeKey.clazz) : typeKey.clazz != null) return false;
            if (interfaces != null ? !interfaces.equals(typeKey.interfaces) : typeKey.interfaces != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = interfaces != null ? interfaces.hashCode() : 0;
            result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
            return result;
        }
    }

}
