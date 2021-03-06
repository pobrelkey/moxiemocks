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

import moxie.hamcrest.IsArray;
import org.hamcrest.Matcher;

import java.util.List;

abstract class CheckImpl<C extends CheckImpl<C,I,R>, I extends Interception, R> {
    protected final List<Invocation> invocations;
    private boolean negated = false;
    private boolean unexpectedly = false;
    private CardinalityImpl cardinality = new CardinalityImpl<CardinalityImpl>().once();
    private boolean defaultCardinality = true;
    private List<GroupImpl> groups;
    protected Matcher resultMatcher;
    protected Matcher throwableMatcher;

    public CheckImpl(List<Invocation> invocations) {
        this.invocations = invocations;
    }

    @SuppressWarnings("unchecked")
    public C didNot() {
        if (negated) {
            throw new MoxieSyntaxError("no double negatives!");
        }
        negated = true;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C unexpectedly() {
        if (unexpectedly) {
            throw new MoxieSyntaxError("you already called unexpectedly()!");
        }
        unexpectedly = true;
        return (C) this;
    }

    protected Object handleInvocation(InvocableAdapter invocable, Object[] params) {
        List<Matcher> argMatchers = MatcherSyntax.methodCall(invocable, params);
        @SuppressWarnings("unchecked")
        Matcher argsMatcher = new IsArray(argMatchers);

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
            if (getInterception().equals(invocation.getInterception()) && invocable.equals(invocation.getInvocable()) && argsMatcher.matches(invocation.getArguments())) {
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
            throwFailedCheckError("check matched one or more method invocations", invocable, argMatchers);
        } else if (!cardinality.isSatisfied() && !negated) {
            throwFailedCheckError("check failed to match the correct number of method invocations", invocable, argMatchers);
        }

        if (lastMatch != null && groups != null && !negated) {
            for (GroupImpl group : groups) {
                group.setCheckCursor(lastMatch);
            }
        }

        // TODO: deep mock checks
        return MoxieUtils.defaultValue(invocable.getReturnType());
    }

    @SuppressWarnings("unchecked")
    public C inGroup(Group... groups) {
        if (this.groups != null) {
            throw new MoxieSyntaxError("group(s) already specified for this check");
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
            throw new MoxieSyntaxError("already specified number of times");
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

    private void throwFailedCheckError(String message, InvocableAdapter checkedInvocable, List<Matcher> argMatchers) {
        throw new MoxieFailedCheckError(message, checkedInvocable, argMatchers, cardinality, throwableMatcher, resultMatcher, invocations);
    }

    protected abstract I getInterception();

    @SuppressWarnings("unchecked")
    public C throwException(Throwable throwable) {
        Matcher matcher = MatcherSyntax.singleMatcherExpression(Throwable.class, throwable);
        if (this.throwableMatcher != null) {
            throw new MoxieSyntaxError("already specified a Throwable for this check");
        }
        this.throwableMatcher = matcher;
        return (C) this;
    }

    public C threw(Throwable throwable) {
        return throwException(throwable);
    }

    @SuppressWarnings("unchecked")
    public C returnValue(R returnValue) {
        Matcher matcher = MatcherSyntax.singleMatcherExpression(null, returnValue);
        if (this.resultMatcher != null) {
            throw new MoxieSyntaxError("cannot specify a return value twice");
        }
        this.resultMatcher = matcher;
        return (C) this;
    }

    public C returned(R returnValue) {
        return returnValue(returnValue);
    }
}
