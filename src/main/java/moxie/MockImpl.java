/*
 * Copyright (c) 2010 Moxie contributors
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

import java.lang.reflect.Method;
import java.util.List;

class MockImpl<T> extends Interception<T> {

    MockImpl(Class<T> clazz, String name, MoxieFlags flags, List<Invocation> invocations, Class[] constructorArgTypes, Object[] constructorArgs) {
        super(clazz, name, flags, new InstantiationStackTrace("mock object \"" + name + "\" was instantiated here"), constructorArgTypes, constructorArgs);
    }

    protected MethodBehavior defaultBehavior(Method method, final Object[] args, final SuperInvoker superInvoker) {
        if (superInvoker != null && Boolean.TRUE.equals(flags.isPartial())) {
            return new MethodBehavior() {
                public Object invoke() throws Throwable {
                    return superInvoker.invokeSuper(args);
                }
            };
        } else if (TO_STRING.matches(method)) {
            return new MethodBehavior() {
                public Object invoke() throws Throwable {
                    return "[mock object \"" + name + "\"]";
                }
            };
        } else if (EQUALS.matches(method)) {
            return new MethodBehavior() {
                public Object invoke() throws Throwable {
                    return args[0] == proxy;
                }
            };
        } else if (HASH_CODE.matches(method)) {
            return new MethodBehavior() {
                public Object invoke() throws Throwable {
                    return System.identityHashCode(proxy);
                }
            };
        } else {
            final Object defaultValue = MoxieUtils.defaultValue(method.getReturnType());
            return new MethodBehavior() {
                public Object invoke() {
                    return defaultValue;
                }
            };
        }
    }

}
