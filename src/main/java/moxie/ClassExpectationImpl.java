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

class ClassExpectationImpl<T> extends ExpectationImpl<ClassExpectationImpl<T>, ClassInterception> implements ClassExpectation<T> {
    protected ClassExpectationImpl(ClassInterception interception) {
        super(interception);
    }

    public T onNew(Object... params) {
        return onNew(null, params);
    }

    public T whenNew(Object... params) {
        return onNew(params);
    }

    public T willNew(Object... params) {
        return onNew(params);
    }

    @SuppressWarnings("unchecked")
    public T onNew(Class[] paramSignature, Object... params) {
        Class interceptedClass = interception.getInterceptedClass();
        ConstructorAdapter constructor = MoxieUtils.guessConstructor(interceptedClass, null, params);
        constructor.zombify();
        return (T) handleInvocation(constructor, params);
    }

    public T whenNew(Class[] paramSignature, Object... params) {
        return onNew(paramSignature, params);
    }

    public T willNew(Class[] paramSignature, Object... params) {
        return onNew(paramSignature, params);
    }

    @Override
    protected boolean isStatic() {
        return true;
    }

}
