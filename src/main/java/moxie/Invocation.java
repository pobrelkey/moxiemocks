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

import moxie.hamcrest.SimpleDescription;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

class Invocation implements SelfDescribing {
    private final Interception interception;
    private final InvocableAdapter invocable;
    private final Object[] arguments;
    private final InstantiationStackTrace instantiationStackTrace;
    private ExpectationImpl expectationSatisfied;
    private CheckImpl checkSatisfied;
    private Object valueReturned;
    private Object exceptionThrown;

    Invocation(Interception interception, InvocableAdapter invocable, Object[] arguments) {
        this.interception = interception;
        this.invocable = invocable;
        this.arguments = arguments;
        instantiationStackTrace = MoxieUtils.unbox(interception.flags.isTracing(), false) ? new InstantiationStackTrace("method " + invocable.getName() + " called here") : null;
    }

    Object[] getArguments() {
        return arguments;
    }

    Interception getInterception() {
        return interception;
    }

    InvocableAdapter getInvocable() {
        return invocable;
    }

    InstantiationStackTrace getInstantiationStackTrace() {
        return instantiationStackTrace;
    }

    CheckImpl getCheckSatisfied() {
        return checkSatisfied;
    }

    void setCheckSatisfied(CheckImpl checkSatisfied) {
        this.checkSatisfied = checkSatisfied;
    }

    ExpectationImpl getExpectationSatisfied() {
        return expectationSatisfied;
    }

    void setExpectationSatisfied(ExpectationImpl expectationSatisfied) {
        this.expectationSatisfied = expectationSatisfied;
    }

    Object getExceptionThrown() {
        return exceptionThrown;
    }

    void setExceptionThrown(Object exceptionThrown) {
        this.exceptionThrown = exceptionThrown;
    }

    Object getValueReturned() {
        return valueReturned;
    }

    void setValueReturned(Object valueReturned) {
        this.valueReturned = valueReturned;
    }

    public void describeTo(Description description) {
        description.appendText(interception.getName());
        description.appendText(".");
        description.appendText(invocable.getName());
        if (arguments != null) {
            description.appendValueList("(", ", ", ")", arguments);
        } else {
            description.appendText("()");
        }
        if (exceptionThrown != null) {
            description.appendText(", threw " + exceptionThrown);
        } else if (valueReturned != null) {
            description.appendText(", returned " + valueReturned);
        } else if (!invocable.getReturnType().equals(Void.TYPE)) {
            description.appendText(", returned null");
        }
    }

    @Override
    public String toString() {
        return SimpleDescription.toString(this);
    }
}