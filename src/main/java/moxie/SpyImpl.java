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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

class SpyImpl<T> extends ObjectInterception<T> {
    private final T realObject;

    @SuppressWarnings("unchecked")
    SpyImpl(T realObject, String name, MoxieFlags flags, List<Invocation> invocations) {
        super((Class<T>) realObject.getClass(), name, flags, instantiationStackTrace(name, flags), null, null);
        this.realObject = realObject;
    }

    private static InstantiationStackTrace instantiationStackTrace(String name, MoxieFlags flags) {
        return MoxieUtils.unbox(flags.isTracing(), false) ? new InstantiationStackTrace("spy object \"" + name + "\" was instantiated here") : null;
    }

    protected MethodBehavior defaultBehavior(InvocableAdapter invocable, final Object[] args, SuperInvoker superInvoker) {
        final Method method = ((MethodAdapter) invocable).getMethod();
        return new IdempotentMethodBehavior() {
            @Override
            protected void doInvoke() {
                try {
                    method.setAccessible(true);
                    result = method.invoke(realObject, args);
                } catch (IllegalAccessException e) {
                    thrown = new MoxieUnexpectedError("error calling target of spy object", e);
                } catch (InvocationTargetException e) {
                    thrown = e.getTargetException();
                } catch (Throwable t) {
                    thrown = t;
                }
            }
        };
    }

}
