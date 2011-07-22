/*
 * Copyright (c) 2010-2011 Moxie contributors
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

import org.hamcrest.Matcher;

import java.lang.reflect.Method;
import java.util.List;

abstract class CheckImpl<C extends CheckImpl<C,I>, I extends Interception> {

    protected final I interception;
    private final List<Invocation> invocations;
    private boolean negated = false;
    private boolean unexpectedly = false;
    private CardinalityImpl cardinality = new CardinalityImpl<CardinalityImpl>().once();
    private boolean defaultCardinality = true;
    private List<GroupImpl> groups;
    protected Matcher resultMatcher;
    protected Matcher throwableMatcher;


    CheckImpl(I interception, List<Invocation> invocations) {
        this.interception = interception;
        this.invocations = invocations;
    }

    @SuppressWarnings("unchecked")
    public C didNot() {
        if (negated) {
            throw new IllegalStateException("no double negatives!");
        }
        negated = true;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C unexpectedly() {
        if (unexpectedly) {
            throw new IllegalStateException("you already called unexpectedly()!");
        }
        unexpectedly = true;
        return (C) this;
    }

    protected void handleInvocation(Method method, Object[] params) {
        List<Matcher> argMatchers = MatcherSyntax.methodCall(method, params);
        Matcher argsMatcher = MoxieMatchers.isArrayMatcher(argMatchers);

        int cursor = 0;
        if (groups != null) {
            for (GroupImpl group : groups) {
                if (group.getCheckCursor() > cursor) {
                    cursor = group.getCheckCursor();
                }
            }
        }

        Integer lastMatch = null;

        for (; cursor < invocations.size(); cursor++) {
            final Invocation invocation = invocations.get(cursor);
            if (interception.equals(invocation.getInterception()) && method.equals(invocation.getMethod()) && argsMatcher.matches(invocation.getArguments())) {
                if (unexpectedly && invocation.getExpectationSatisfied() != null) {
                    continue;
                }
                if (throwableMatcher != null && !throwableMatcher.matches(invocation.getExceptionThrown())) {
                    continue;
                }
                if (resultMatcher != null && !resultMatcher.matches(invocation.getValueReturned())) {
                    continue;
                }

                cardinality.incrementCount();
                lastMatch = cursor;
                if (!negated) {
                    cardinality.whenCardinalitySatisfied(new Runnable() {
                        public void run() {
                            invocation.setCheckSatisfied(CheckImpl.this);
                        }
                    });
                }
            }
        }

        if (cardinality.isSatisfied() && negated) {
            throwFailedCheckError("check matched one or more method invocations", method, argMatchers);
        } else if (!cardinality.isSatisfied() && !negated) {
            throwFailedCheckError("check failed to match the correct number of method invocations", method, argMatchers);
        }

        if (lastMatch != null && groups != null && !negated) {
            for (GroupImpl group : groups) {
                group.setCheckCursor(lastMatch);
            }
        }
    }

    public void on(String methodName, Object... params) {
        handleInvocation(MoxieUtils.guessMethod(this.interception.getInterceptedClass(), methodName, false, null, params), params);
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
        handleInvocation(MoxieUtils.guessMethod(this.interception.getInterceptedClass(), methodName, false, paramSignature, params), params);
    }

    public void when(String methodName, Class[] paramSignature, Object... params) {
        on(methodName, paramSignature, params);
    }

    public void get(String methodName, Class[] paramSignature, Object... params) {
        on(methodName, paramSignature, params);
    }

    public void got(String methodName, Class[] paramSignature, Object... params) {
        on(methodName, paramSignature, params);
    }

    @SuppressWarnings("unchecked")
    public C inGroup(Group... groups) {
        if (this.groups != null) {
            throw new IllegalStateException("group(s) already specified for this check");
        }
        for (Group group : groups) {
            if (!((GroupImpl) group).isStrictlyOrdered()) {
                throw new IllegalArgumentException("must perform checks using ORDERED groups");
            }
        }
        this.groups = MoxieUtils.listFromArray(groups);
        return (C) this;
    }

    private CardinalityImpl<C> newCardinality() {
        if (!defaultCardinality) {
            throw new IllegalStateException("already specified number of times");
        }
        defaultCardinality = false;
        @SuppressWarnings("unchecked")
        CardinalityImpl<C> result = new CardinalityImpl<C>((C) this);
        cardinality = result;
        return result;
    }


    public C never() {
        return newCardinality().never();
    }

    public C once() {
        return newCardinality().once();
    }

    public C atLeastOnce() {
        return newCardinality().atLeastOnce();
    }

    public C atMostOnce() {
        return newCardinality().atMostOnce();
    }

    public C anyTimes() {
        return newCardinality().anyTimes();
    }

    public C times(int times) {
        return newCardinality().times(times);
    }

    public C times(int minTimes, int maxTimes) {
        return newCardinality().times(minTimes, maxTimes);
    }

    public C atLeast(int times) {
        return newCardinality().atLeast(times);
    }

    public C atMost(int times) {
        return newCardinality().atMost(times);
    }

    private void throwFailedCheckError(String message, Method checkedMethod, List<Matcher> argMatchers) {
        throw new MoxieFailedCheckError(message, checkedMethod, argMatchers, cardinality, throwableMatcher, resultMatcher, invocations);
    }
}