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

abstract class NonLambdaExpectationImpl<E extends NonLambdaExpectationImpl<E, I>, I extends Interception> extends ExpectationImpl<E,I> {

    private final I interception;

    @SuppressWarnings("unchecked")
    protected NonLambdaExpectationImpl(I interception) {
        super();
        this.interception = interception;
        cardinality.atLeastOnce();
    }

    abstract protected boolean isStatic();

    public Object on(String methodName, Object... params) {
        return on(methodName, null, params);
    }

    public Object when(String methodName, Object... params) {
        return on(methodName, params);
    }

    public Object will(String methodName, Object... params) {
        return on(methodName, params);
    }

    public Object on(String methodName, Class[] paramSignature, Object... params) {
        checkMethodAndCardinality();
        return handleInvocation(MoxieUtils.guessMethod(this.getInterception().getInterceptedClass(), methodName, isStatic(), paramSignature, params), params);
    }

    public Object when(String methodName, Class[] paramSignature, Object... params) {
        return on(methodName, paramSignature, params);
    }

    public Object will(String methodName, Class[] paramSignature, Object... params) {
        return on(methodName, paramSignature, params);
    }

    @Override
    protected I getInterception() {
        return interception;
    }

}
