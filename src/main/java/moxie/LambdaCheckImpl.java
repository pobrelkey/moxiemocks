/*
 * Copyright (c) 2013 Moxie contributors
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

class LambdaCheckImpl<R> extends CheckImpl<LambdaCheckImpl<R>, Interception, R> implements LambdaCheck<R> {
    private Interception interception;
    private final MoxieControlImpl moxie;

    public LambdaCheckImpl(MoxieControlImpl moxie, List<Invocation> invocations) {
        super(invocations);
        this.moxie = moxie;
    }

    @SuppressWarnings("unchecked")
    public LambdaCheckImpl<Void> onVoid(ThrowingRunnable lambda) {
        getMagicLambdaHelper().doInvoke(lambda, MagicLambdaHelper.RUNNABLE_METHOD);
        return (LambdaCheckImpl<Void>) this;
    }

    public LambdaCheckImpl<Void> whenVoid(ThrowingRunnable lambda) {
        return onVoid(lambda);
    }

    public LambdaCheckImpl<Void> getVoid(ThrowingRunnable lambda) {
        return onVoid(lambda);
    }

    public LambdaCheckImpl<Void> gotVoid(ThrowingRunnable lambda) {
        return onVoid(lambda);
    }

    public LambdaCheckImpl<Void> thatVoid(ThrowingRunnable lambda) {
        return onVoid(lambda);
    }

    @SuppressWarnings("unchecked")
    public <RR extends R> LambdaCheckImpl<RR> on(ThrowingSupplier<RR> lambda) {
        getMagicLambdaHelper().doInvoke(lambda, MagicLambdaHelper.SUPPLIER_METHOD);
        return (LambdaCheckImpl<RR>) this;
    }

    public <RR extends R> LambdaCheckImpl<RR> when(ThrowingSupplier<RR> lambda) {
        return on(lambda);
    }

    public <RR extends R> LambdaCheckImpl<RR> get(ThrowingSupplier<RR> lambda) {
        return on(lambda);
    }

    public <RR extends R> LambdaCheckImpl<RR> got(ThrowingSupplier<RR> lambda) {
        return on(lambda);
    }

    public <RR extends R> LambdaCheckImpl<RR> that(ThrowingSupplier<RR> lambda) {
        return on(lambda);
    }

    private MagicLambdaHelper getMagicLambdaHelper() {
        return new MagicLambdaHelper(moxie) {
            @Override
            protected MethodIntercept getLambdaInterceptForClass(final ClassInterception classInterception) {
                return new MethodIntercept() {
                    public Object intercept(Object proxy, InvocableAdapter invocable, Object[] args, SuperInvoker superInvoker) throws Throwable {
                        interception = classInterception;
                        return handleInvocation(invocable, args);
                    }
                };
            }

            @Override
            protected MethodIntercept getLambdaInterceptForObject() {
                return new MethodIntercept() {
                    public Object intercept(Object proxy, InvocableAdapter invocable, Object[] args, SuperInvoker superInvoker) throws Throwable {
                        interception = moxie.getInterceptionFromProxy(proxy);
                        return handleInvocation(invocable, args);
                    }
                };
            }
        };
    }

    @Override
    protected Interception getInterception() {
        return interception;
    }
}
