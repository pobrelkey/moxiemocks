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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

class ObjectExpectationImpl<T> extends NonLambdaExpectationImpl<ObjectExpectationImpl<T>, ObjectInterception<T>> implements ObjectExpectation<T> {

    protected ObjectExpectationImpl(ObjectInterception<T> interception) {
        super(interception);
    }

    public T on() {
        checkMethodAndCardinality();
        return getInterception().getProxyFactory().createProxy(new MethodIntercept() {
            public Object intercept(Object proxy, InvocableAdapter invocable, Object[] params, SuperInvoker superInvoker) throws Throwable {
                return handleInvocation(invocable, params);
            }
        }, getInterception().getConstructorArgTypes(), getInterception().getConstructorArgs());
    }

    public T will() {
        return on();
    }

    public T when() {
        return on();
    }

    public ObjectExpectationImpl<T> willReturnVerified(Object result) {
        Matcher matcher = MatcherSyntax.singleMatcherExpression(null, result);
        if (!(getInterception() instanceof SpyImpl)) {
            throw new MoxieSyntaxError("this method is only for expectations on spy objects");
        }
        returnValueMatcher = matcher;
        return this;
    }

    public ObjectExpectationImpl<T> andVerifyReturn(Object result) {
        return willReturnVerified(result);
    }

    public ObjectExpectationImpl<T> willThrowVerified(Throwable throwable) {
        Matcher matcher = MatcherSyntax.singleMatcherExpression(Throwable.class, throwable);
        if (!(getInterception() instanceof SpyImpl)) {
            throw new MoxieSyntaxError("this method is only for expectations on spy objects");
        }
        exceptionMatcher = matcher;
        return this;
    }

    public ObjectExpectationImpl<T> andVerifyThrow(Throwable throwable) {
        return willThrowVerified(throwable);
    }

    public ObjectExpectationImpl<T> andCallOriginal() {
        return willCallOriginal();
    }

    public ObjectExpectationImpl<T> willCallOriginal() {
        return doWillHandleWith(new OriginalHandler());
    }

    @Override
    protected boolean isStatic() {
        return false;
    }

    static private class OriginalHandler implements MethodIntercept, SelfDescribing {
        public Object intercept(Object mockObject, InvocableAdapter invocable, Object[] parameters, SuperInvoker superInvoker) throws Throwable {
            return superInvoker.invokeSuper(parameters);
        }

        public void describeTo(Description description) {
            description.appendText("call original method implementation");
        }
    }
}
