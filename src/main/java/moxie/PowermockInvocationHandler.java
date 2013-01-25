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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

class PowermockInvocationHandler implements InvocationHandler {
    private static final MethodIntercept.SuperInvoker ZOMBIE_METHOD_SUPER_INVOKER = new ZombieSuperInvoker("cannot partially mock a static or final method");
    private final Map<Object, MethodIntercept> proxyIntercepts;

    PowermockInvocationHandler(Map<Object, MethodIntercept> proxyIntercepts) {
        this.proxyIntercepts = proxyIntercepts;
    }

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
}
