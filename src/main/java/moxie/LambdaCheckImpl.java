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

class LambdaCheckImpl extends CheckImpl<LambdaCheckImpl, Interception> {
    private Interception interception;
    private final MoxieControlImpl moxie;

    public LambdaCheckImpl(MoxieControlImpl moxie, List<Invocation> invocations) {
        super(invocations);
        this.moxie = moxie;
    }

    public void on(Runnable lambda) {
        new MagicLambdaHelper(moxie) {
            @Override
            protected MethodIntercept getLambdaInterceptForClass(ClassInterception classInterception) {
                interception = classInterception;
                final ClassCheckImpl check = new ClassCheckImpl(classInterception, invocations);
                return new MethodIntercept() {
                    public Object intercept(Object proxy, InvocableAdapter invocable, Object[] args, SuperInvoker superInvoker) throws Throwable {
                        return check.handleInvocation(invocable, args);
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
        }.doInvoke(lambda, MagicLambdaHelper.RUNNABLE_METHOD);
    }

    public void when(Runnable lambda) {
        on(lambda);
    }

    public void get(Runnable lambda) {
        on(lambda);
    }

    public void got(Runnable lambda) {
        on(lambda);
    }

    @Override
    protected Interception getInterception() {
        return interception;
    }
}
