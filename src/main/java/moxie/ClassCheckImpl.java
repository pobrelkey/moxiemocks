/*
 * Copyright (c) 2010-2013 Moxie contributors
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

class ClassCheckImpl<C> extends NonLambdaCheckImpl<ClassCheckImpl<C>, ClassInterception> implements ClassCheck<C> {
    ClassCheckImpl(ClassInterception interception, List<Invocation> invocations) {
        super(interception, invocations);
    }

    public void onNew(Object... params) {
        onNew(null, params);
    }

    public void whenNew(Object... params) {
        onNew(params);
    }

    public void getNew(Object... params) {
        onNew(params);
    }

    public void gotNew(Object... params) {
        onNew(params);
    }

    public void onNew(Class[] paramSignature, Object... params) {
        Class interceptedClass = getInterception().getInterceptedClass();
        ConstructorAdapter constructor = MoxieUtils.guessConstructor(interceptedClass, paramSignature, params);
        handleInvocation(constructor, params);
    }

    public void whenNew(Class[] paramSignature, Object... params) {
        onNew(paramSignature,  params);
    }

    public void getNew(Class[] paramSignature, Object... params) {
        onNew(paramSignature,  params);
    }

    public void gotNew(Class[] paramSignature, Object... params) {
        onNew(paramSignature,  params);
    }

    protected boolean isStatic() {
        return true;
    }
}