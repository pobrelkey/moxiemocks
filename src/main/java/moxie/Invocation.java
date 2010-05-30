/*
 * Copyright (c) 2010 Moxie contributors
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

class Invocation {
    private final Interception interception;
    private final Method method;
    private final Object[] arguments;
    private final InstantiationStackTrace instantiationStackTrace;
    private ExpectationImpl expectationSatisfied;
    private CheckImpl checkSatisfied;
    private Object valueReturned;
    private Object exceptionThrown;

    Invocation(Interception interception, Method method, Object[] arguments) {
        this.interception = interception;
        this.method = method;
        this.arguments = arguments;
        instantiationStackTrace = new InstantiationStackTrace("method " + method.getName() + " called here");
    }

    public Object[] getArguments() {
        return arguments;
    }

    public Interception getInterception() {
        return interception;
    }

    public Method getMethod() {
        return method;
    }

    public InstantiationStackTrace getInstantiationStackTrace() {
        return instantiationStackTrace;
    }

    public CheckImpl getCheckSatisfied() {
        return checkSatisfied;
    }

    public void setCheckSatisfied(CheckImpl checkSatisfied) {
        this.checkSatisfied = checkSatisfied;
    }

    public ExpectationImpl getExpectationSatisfied() {
        return expectationSatisfied;
    }

    public void setExpectationSatisfied(ExpectationImpl expectationSatisfied) {
        this.expectationSatisfied = expectationSatisfied;
    }

    public Object getExceptionThrown() {
        return exceptionThrown;
    }

    public void setExceptionThrown(Object exceptionThrown) {
        this.exceptionThrown = exceptionThrown;
    }

    public Object getValueReturned() {
        return valueReturned;
    }

    public void setValueReturned(Object valueReturned) {
        this.valueReturned = valueReturned;
    }

}