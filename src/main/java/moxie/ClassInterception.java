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

import java.lang.reflect.Method;

class ClassInterception<T> extends Interception {

    protected ClassInterception(Class<T> clazz, String name, MoxieFlags flags, InstantiationStackTrace instantiationStackTrace) {
        super(clazz, name, flags, instantiationStackTrace);
        CGLIBProxyFactory.registerClassInterception(this);
    }

    ClassExpectationImpl expect() {
        return new ClassExpectationImpl(this);
    }

    ClassCheckImpl check() {
        return new ClassCheckImpl(this, invocations);
    }

    @Override
    protected MethodBehavior defaultBehavior(final Method method, Object[] args, SuperInvoker superInvoker) {
        return new MethodBehavior() {
            public Object invoke() throws Throwable {
                if (flags.isAutoStubbing()) {
                    return MoxieUtils.defaultValue(method.getReturnType());
                }
                // TODO: clearer error message
                throw new MoxieZombieMethodInvocationError("Behavior not defined for class method " + method.getName());
            }
        };
    }
}
