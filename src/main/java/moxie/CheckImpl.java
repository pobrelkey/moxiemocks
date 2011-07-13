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

class CheckImpl<T> implements Check<T> {

    private final Interception<T> interception;
    private final List<Invocation> invocations;
    private boolean negated = false;
    private boolean unexpectedly = false;
    private Matcher resultMatcher;
    private Matcher throwableMatcher;
    private CardinalityImpl cardinality = new CardinalityImpl<CardinalityImpl>().once();
    private boolean defaultCardinality = true;
    private List<GroupImpl> groups;

    CheckImpl(Interception<T> interception, List<Invocation> invocations) {
        this.interception = interception;
        this.invocations = invocations;
    }

    public Check<T> didNot() {
        if (negated) {
            throw new IllegalStateException("no double negatives!");
        }
        negated = true;
        return this;
    }

    public Check<T> unexpectedly() {
        if (unexpectedly) {
            throw new IllegalStateException("you already called unexpectedly()!");
        }
        unexpectedly = true;
        return this;
    }

    public Check<T> throwException(Throwable throwable) {
        Matcher matcher = MatcherSyntax.singleMatcherExpression(Throwable.class, throwable);
        if (this.throwableMatcher != null) {
            throw new IllegalStateException("already specified a Throwable for this check");
        }
        this.throwableMatcher = matcher;
        return this;
    }

    public Check<T> threw(Throwable throwable) {
        return throwException(throwable);
    }

    public Check<T> returnValue(Object returnValue) {
        Matcher matcher = MatcherSyntax.singleMatcherExpression(null, returnValue);
        if (this.resultMatcher != null) {
            throw new IllegalStateException("cannot specify a return value twice");
        }
        this.resultMatcher = matcher;
        return this;
    }

    public Check<T> returned(Object returnValue) {
        return returnValue(returnValue);
    }

    public T on() {
        return interception.getProxyFactory().createProxy(new MethodIntercept() {
            public Object intercept(Object proxy, Method method, Object[] params, SuperInvoker superInvoker) throws Throwable {
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

                return MoxieUtils.defaultValue(method.getReturnType());
            }
        }, interception.getConstructorArgTypes(),  interception.getConstructorArgs());
    }

    public T when() {
        return on();
    }

    public T get() {
        return on();
    }

    public T got() {
        return on();
    }

    public Check<T> inGroup(Group... groups) {
        if (this.groups != null) {
            throw new IllegalStateException("group(s) already specified for this check");
        }
        for (Group group : groups) {
            if (!((GroupImpl) group).isStrictlyOrdered()) {
                throw new IllegalArgumentException("must perform checks using ORDERED groups");
            }
        }
        this.groups = MoxieUtils.listFromArray(groups);
        return this;
    }

    private CardinalityImpl<CheckImpl<T>> newCardinality() {
        if (!defaultCardinality) {
            throw new IllegalStateException("already specified number of times");
        }
        defaultCardinality = false;
        CardinalityImpl<CheckImpl<T>> result = new CardinalityImpl<CheckImpl<T>>(this);
        cardinality = result;
        return result;
    }


    public Check<T> never() {
        return newCardinality().never();
    }

    public Check<T> once() {
        return newCardinality().once();
    }

    public Check<T> atLeastOnce() {
        return newCardinality().atLeastOnce();
    }

    public Check<T> atMostOnce() {
        return newCardinality().atMostOnce();
    }

    public Check<T> anyTimes() {
        return newCardinality().anyTimes();
    }

    public Check<T> times(int times) {
        return newCardinality().times(times);
    }

    public Check<T> times(int minTimes, int maxTimes) {
        return newCardinality().times(minTimes, maxTimes);
    }

    public Check<T> atLeast(int times) {
        return newCardinality().atLeast(times);
    }

    public Check<T> atMost(int times) {
        return newCardinality().atMost(times);
    }

    private void throwFailedCheckError(String message, Method checkedMethod, List<Matcher> argMatchers) {
        throw new MoxieFailedCheckError(message, checkedMethod, argMatchers, cardinality, throwableMatcher, resultMatcher, invocations);
    }
}