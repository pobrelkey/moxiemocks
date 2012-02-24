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

class ClassCheckImpl<C, T> extends CheckImpl<ClassCheckImpl<C, T>, ClassInterception> implements ClassCheck<C, ClassCheckImpl<C, T>> {
    ClassCheckImpl(ClassInterception interception, List<Invocation> invocations) {
        super(interception, invocations);
    }

    public C onNew(Object... params) {
        return onNew(null, params);
    }

    public C whenNew(Object... params) {
        return onNew(params);
    }

    public C getNew(Object... params) {
        return onNew(params);
    }

    public C gotNew(Object... params) {
        return onNew(params);
    }

    @SuppressWarnings("unchecked")
    public C onNew(Class[] paramSignature, Object... params) {
        Class interceptedClass = interception.getInterceptedClass();
        ConstructorAdapter constructor = MoxieUtils.guessConstructor(interceptedClass, null, params);
        return (C) handleInvocation(constructor, params);
    }

    public C whenNew(Class[] paramSignature, Object... params) {
        return onNew(paramSignature,  params);
    }

    public C getNew(Class[] paramSignature, Object... params) {
        return onNew(paramSignature,  params);
    }

    public C gotNew(Class[] paramSignature, Object... params) {
        return onNew(paramSignature,  params);
    }
}