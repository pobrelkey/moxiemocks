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

import java.util.Collections;
import java.util.Map;

class ProxyIntercepts {
    private static final ProxyIntercepts INSTANCE = new ProxyIntercepts();

    private final Map<Class, MethodIntercept> classIntercepts = Collections.synchronizedMap(new WeakIdentityMap<Class, MethodIntercept>());
    private final Map<Object, MethodIntercept> proxyIntercepts = Collections.synchronizedMap(new WeakIdentityMap<Object, MethodIntercept>());

    private final Map<Class, ThreadLocal<MethodIntercept>> classThreadLocalIntercepts = Collections.synchronizedMap(new WeakIdentityMap<Class, ThreadLocal<MethodIntercept>>());
    private final Map<Object, ThreadLocal<MethodIntercept>> proxyThreadLocalIntercepts = Collections.synchronizedMap(new WeakIdentityMap<Object, ThreadLocal<MethodIntercept>>());

    private ProxyIntercepts() {}

    void registerClassIntercept(Class clazz, MethodIntercept intercept) {
        classIntercepts.put(clazz, intercept);
    }

    void registerThreadLocalClassIntercept(Class clazz, MethodIntercept intercept) {
        register(clazz, intercept, classThreadLocalIntercepts);
    }

    void clearThreadLocalClassIntercept(Class clazz) {
        ThreadLocal<MethodIntercept> threadLocal = classThreadLocalIntercepts.get(clazz);
        if (threadLocal != null) {
            threadLocal.remove();
        }
    }

    void registerIntercept(Object proxy, MethodIntercept intercept) {
        proxyIntercepts.put(proxy, intercept);
    }

    void registerThreadLocalIntercept(Object proxy, MethodIntercept intercept) {
        register(proxy, intercept, proxyThreadLocalIntercepts);
    }

    void clearThreadLocalIntercept(Object proxy) {
        ThreadLocal<MethodIntercept> threadLocal = proxyThreadLocalIntercepts.get(proxy);
        if (threadLocal != null) {
            threadLocal.remove();
        }
    }

    static ProxyIntercepts getInstance() {
        return INSTANCE;
    }

    MethodIntercept getClassIntercept(Class<?> clazz) {
        return getThreadLocalOrGeneral(clazz, classThreadLocalIntercepts, classIntercepts);
    }

    MethodIntercept getIntercept(Object proxy) {
        return getThreadLocalOrGeneral(proxy, proxyThreadLocalIntercepts, proxyIntercepts);
    }

    private <T> void register(T key, MethodIntercept interception, Map<T, ThreadLocal<MethodIntercept>> threadLocalIntercepts) {
        ThreadLocal<MethodIntercept> threadLocal = threadLocalIntercepts.get(key);
        if (threadLocal == null) {
            threadLocal = new ThreadLocal<MethodIntercept>();
            threadLocal.set(interception);
            threadLocalIntercepts.put(key, threadLocal);
        } else {
            threadLocal.set(interception);
        }
    }

    private <T> MethodIntercept getThreadLocalOrGeneral(T key, Map<T, ThreadLocal<MethodIntercept>> threadLocalIntercepts, Map<T, MethodIntercept> intercepts) {
        MethodIntercept methodIntercept = null;
        ThreadLocal<MethodIntercept> methodInterceptThreadLocal = threadLocalIntercepts.get(key);
        if (methodInterceptThreadLocal != null) {
            methodIntercept = methodInterceptThreadLocal.get();
        }
        if (methodIntercept == null) {
            methodIntercept = intercepts.get(key);
        }
        return methodIntercept;
    }
}
