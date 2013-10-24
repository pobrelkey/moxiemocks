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

abstract class NonLambdaCheckImpl<C extends NonLambdaCheckImpl<C,I>, I extends Interception> extends CheckImpl<C,I,Object> {

    private final I interception;


    NonLambdaCheckImpl(I interception, List<Invocation> invocations) {
        super(invocations);
        this.interception = interception;
    }

    public void on(String methodName, Object... params) {
        handleInvocation(MoxieUtils.guessMethod(this.getInterception().getInterceptedClass(), methodName, isStatic(), null, params), params);
    }

    public void when(String methodName, Object... params) {
        on(methodName, params);
    }

    public void get(String methodName, Object... params) {
        on(methodName, params);
    }

    public void got(String methodName, Object... params) {
        on(methodName, params);
    }

    public void on(String methodName, Class[] paramSignature, Object... params) {
        handleInvocation(MoxieUtils.guessMethod(this.getInterception().getInterceptedClass(), methodName, isStatic(), paramSignature, params), params);
    }

    protected abstract boolean isStatic();

    public void when(String methodName, Class[] paramSignature, Object... params) {
        on(methodName, paramSignature, params);
    }

    public void get(String methodName, Class[] paramSignature, Object... params) {
       on(methodName, paramSignature, params);
    }

    public void got(String methodName, Class[] paramSignature, Object... params) {
        on(methodName, paramSignature, params);
    }


    @Override
    protected I getInterception() {
        return interception;
    }
}