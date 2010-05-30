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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

class SpyImpl<T> extends Interception<T> {
    private final T realObject;

    SpyImpl(T realObject, String name, MoxieFlags flags, List<Invocation> invocations) {
        super((Class<T>) realObject.getClass(), name, flags, invocations, new InstantiationStackTrace("spy object \"" + name + "\" was instantiated here"));
        this.realObject = realObject;
    }

    protected MethodBehavior defaultBehavior(final Method method, final Object[] args) {
        // return an idempotent method invoker
        return new MethodBehavior() {
            private Object result;
            private Throwable thrown;
            private boolean called = false;

            public Object invoke() throws Throwable {
                if (!called) {
                    try {
                        result = method.invoke(realObject, args);
                    } catch (IllegalAccessException e) {
                        thrown = new MoxieError("error calling target of spy object", e);
                    } catch (InvocationTargetException e) {
                        thrown = e.getTargetException();
                    } catch (Throwable t) {
                        thrown = t;
                    }
                    called = true;
                }
                if (thrown != null) {
                    throw thrown;
                }
                return result;
            }
        };
    }
}
