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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class GroupImpl implements Group, Verifiable {

    private final String name;
    private final InstantiationStackTrace whereInstantiated;
    private final List<ExpectationImpl> unorderedExpectations = new ArrayList<ExpectationImpl>();
    private final List<ExpectationImpl> orderedExpectations = new ArrayList<ExpectationImpl>();
    private final List<MoxieUnexpectedInvocationError> unexpectedInvocations = new ArrayList<MoxieUnexpectedInvocationError>();
    private boolean defaultCardinality;
    private CardinalityImpl cardinality;
    private int cursor;

    private int checkCursor;
    private MoxieFlags flags;

    GroupImpl(String name, MoxieFlags flags) {
        this.name = name;
        this.whereInstantiated = MoxieUtils.unbox(flags.isTracing(), false) ? new InstantiationStackTrace("sequence \"" + name + "\" was instantiated here") : null;
        reset(flags);
    }

    public Cardinality<Group> willBeCalled() {
        if (!flags.isStrictlyOrdered()) {
            throw new MoxieSyntaxError("cannot set cardinality on an unordered method group");
        }
        if (!defaultCardinality) {
            throw new MoxieSyntaxError("already specified number of times");
        }
        defaultCardinality = false;
        CardinalityImpl<Group> result = new CardinalityImpl<Group>(this);
        cardinality = result;
        return result;
    }

    public void reset(MoxieFlags flags) {
        if (flags != null) {
            this.flags = this.flags != null ? MoxieOptions.mergeWithDefaults(this.flags, flags) : flags;
        }
        unorderedExpectations.clear();
        orderedExpectations.clear();
        defaultCardinality = true;
        cardinality = new CardinalityImpl<CardinalityImpl>().once();
        cursor = 0;
        unexpectedInvocations.clear();
    }

    public Throwable getWhereInstantiated() {
        return whereInstantiated;
    }

    public String getName() {
        return name;
    }

    public void add(ExpectationImpl expectation) {
        if (flags.isStrictlyOrdered() && !expectation.isUnordered()) {
            orderedExpectations.add(expectation);
        } else {
            unorderedExpectations.add(expectation);
        }
    }

    public void verify() {
        // keep track of invocations so we can report them nicely when we fail
        verify(null);
    }

    public void verifyNoBackgroundErrors() {
        if (MoxieUtils.unbox(flags.isBackgroundAware(), true)) {
            if (unexpectedInvocations.size() == 1) {
                throw unexpectedInvocations.get(0);
            } else if (unexpectedInvocations.size() > 1) {
                throw new MoxieUnexpectedInvocationError(unexpectedInvocations);
            }
        }
    }

    void verify(List<Invocation> invocations) {
        for (ExpectationImpl expectation : unorderedExpectations) {
            if (!expectation.isSatisfied()) {
                throwFailedVerificationError("not all expected methods invoked or cardinality not satisfied", invocations);
            }
        }
        if (!orderedExpectations.isEmpty()) {
            if (cursor == orderedExpectations.size() - 1 && orderedExpectations.get(cursor).isSatisfied()) {
                cursor = 0;
                cardinality.incrementCount();
            }
            if (cursor == 0 && cardinality.isSatisfied()) {
                return;
            }
            throwFailedVerificationError("not all expected methods invoked or cardinality not satisfied", invocations);
        }
    }

    @SuppressWarnings("unchecked")
    public ExpectationImpl match(InvocableAdapter invocable, Object[] args, MethodBehavior behavior) {
        ExpectationImpl result = null;
        for (ExpectationImpl expectation : unorderedExpectations) {
            if (expectation.match(invocable, args, behavior, this)) {
                result = expectation;
                break;
            }
        }
        if (result == null && !orderedExpectations.isEmpty()) {
            if (cardinality.isViable()) {
                if (orderedExpectations.get(cursor).match(invocable, args, behavior, this)) {
                    result = orderedExpectations.get(cursor);
                } else {
                    cursor++;
                    if (cursor == orderedExpectations.size()) {
                        cursor = 0;
                        cardinality.incrementCount();
                    }
                    if (cardinality.isViable() && orderedExpectations.get(cursor).match(invocable, args, behavior, this)) {
                        result = orderedExpectations.get(cursor);
                    }
                }
            }
        }
        if (result != null) {
            for (GroupImpl group : (Set<GroupImpl>) result.getGroups()) {
                group.match(result, invocable, args);
            }
        }
        return result;
    }

    public void match(ExpectationImpl expectation, InvocableAdapter invocable, Object[] args) {
        if (!orderedExpectations.isEmpty()) {
            if (cardinality.isViable()) {
                if (orderedExpectations.get(cursor) == expectation) {
                    return;
                }
                cursor++;
                if (cursor == orderedExpectations.size()) {
                    cursor = 0;
                    cardinality.incrementCount();
                }
                if (cardinality.isViable() && orderedExpectations.get(cursor) == expectation) {
                    return;
                }
            }
            throwUnexpectedInvocationError("out of sequence expectation or too many times through sequence", invocable, args);
        }
    }

    int getCheckCursor() {
        return checkCursor;
    }

    void setCheckCursor(int checkCursor) {
        this.checkCursor = checkCursor;
    }

    boolean isStrictlyOrdered() {
        return flags.isStrictlyOrdered();
    }

    void throwUnexpectedInvocationError(String message, InvocableAdapter invoked, Object[] invocationArgs) {
        MoxieUnexpectedInvocationError moxieUnexpectedInvocationError = new MoxieUnexpectedInvocationError(message, name, invoked, invocationArgs, unorderedExpectations, orderedExpectations);
        this.unexpectedInvocations.add(moxieUnexpectedInvocationError);
        throw moxieUnexpectedInvocationError;
    }

    private void throwFailedVerificationError(String message, List<Invocation> invocations) {
        throw new MoxieFailedVerificationError(message, name, invocations, unorderedExpectations, orderedExpectations);
    }

}