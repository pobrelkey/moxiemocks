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

import java.util.List;

class MockImpl<T> extends ObjectInterception<T> {

    MockImpl(Class<T> clazz, String name, MoxieFlags flags, List<Invocation> invocations, Class[] constructorArgTypes, Object[] constructorArgs) {
        super(clazz, name, flags, instantiationStackTrace(name, flags), constructorArgTypes, constructorArgs);
    }

    private static InstantiationStackTrace instantiationStackTrace(String name, MoxieFlags flags) {
        return MoxieUtils.unbox(flags.isTracing(), false) ? new InstantiationStackTrace("mock object \"" + name + "\" was instantiated here") : null;
    }

    protected MethodBehavior defaultBehavior(InvocableAdapter invocable, final Object[] args, final SuperInvoker superInvoker) {
        if (superInvoker != null && Boolean.TRUE.equals(flags.isPartial())) {
            return new IdempotentMethodBehavior() {
                public void doInvoke() {
                    try {
                        result = superInvoker.invokeSuper(args);
                    } catch (Throwable t) {
                        thrown = t;
                    }
                }
            };
        } else if (TO_STRING.matches(invocable)) {
            return new ReturnValueMethodBehavior("[mock object \"" + name + "\"]");
        } else if (EQUALS.matches(invocable)) {
            return new ReturnValueMethodBehavior(args[0] == proxy);
        } else if (HASH_CODE.matches(invocable)) {
            return new ReturnValueMethodBehavior(System.identityHashCode(proxy));
        } else {
            return new ReturnValueMethodBehavior(MoxieUtils.defaultValue(invocable.getReturnType()));
        }
    }

    private static class ReturnValueMethodBehavior implements MethodBehavior {
        private final Object toBeReturned;

        public ReturnValueMethodBehavior(Object toBeReturned) {
            this.toBeReturned = toBeReturned;
        }

        public Object invoke() {
            return toBeReturned;
        }
    }
}
