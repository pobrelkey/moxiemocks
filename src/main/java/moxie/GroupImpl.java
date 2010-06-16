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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class GroupImpl implements Group, Verifiable {

    private final String name;
    private final InstantiationStackTrace whereInstantiated;
    private final Set<ExpectationImpl> unorderedExpectations = new HashSet<ExpectationImpl>();
    private final List<ExpectationImpl> orderedExpectations = new ArrayList<ExpectationImpl>();
    private boolean defaultCardinality;
    private CardinalityImpl cardinality;
    private int cursor;
    private int checkCursor;

    private MoxieFlags flags;

    GroupImpl(String name, MoxieFlags flags) {
        this.name = name;
        this.whereInstantiated = new InstantiationStackTrace("sequence \"" + name + "\" was instantiated here");
        reset(flags);
    }

    public Cardinality<Group> willBeCalled() {
        if (!flags.isStrictlyOrdered()) {
            throw new IllegalStateException("cannot set cardinality on an unordered method group");
        }
        if (!defaultCardinality) {
            throw new IllegalStateException("already specified number of times");
        }
        defaultCardinality = false;
        CardinalityImpl<Group> result = new CardinalityImpl<Group>(this);
        cardinality = result;
        return result;
    }

    public void reset(MoxieFlags flags) {
        this.flags = flags;
        unorderedExpectations.clear();
        orderedExpectations.clear();
        defaultCardinality = true;
        cardinality = new CardinalityImpl<CardinalityImpl>().once();
        cursor = 0;
    }

    public Throwable getWhereInstantiated() {
        return whereInstantiated;
    }

    public void add(ExpectationImpl expectation) {
        if (flags.isStrictlyOrdered() && !expectation.isUnordered()) {
            orderedExpectations.add(expectation);
        } else {
            unorderedExpectations.add(expectation);
        }
    }

    public void verify() {
        for (ExpectationImpl expectation : unorderedExpectations) {
            if (!expectation.isSatisfied()) {
                // TODO nicer exception
                throw new MoxieError("not all methods in expectation invoked");
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
            // TODO nicer exception
            throw new MoxieError("not all expected methods invoked or cardinality not satisfied");
        }
    }

    public ExpectationImpl match(Method method, Object[] args, MethodBehavior behavior) {
        ExpectationImpl result = null;
        for (ExpectationImpl expectation : unorderedExpectations) {
            if (expectation.match(method, args, behavior)) {
                result = expectation;
                break;
            }
        }
        if (result == null && !orderedExpectations.isEmpty()) {
            if (cardinality.isViable()) {
                if (orderedExpectations.get(cursor).match(method, args, behavior)) {
                    result = orderedExpectations.get(cursor);
                } else {
                    cursor++;
                    if (cursor == orderedExpectations.size()) {
                        cursor = 0;
                        cardinality.incrementCount();
                    }
                    if (cardinality.isViable() && orderedExpectations.get(cursor).match(method, args, behavior)) {
                        result = orderedExpectations.get(cursor);
                    }
                }
            }
        }
        if (result != null) {
            for (GroupImpl group : (Set<GroupImpl>) result.getGroups()) {
                group.match(result);
            }
        }
        return result;
    }

    public void match(ExpectationImpl expectation) {
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
            // TODO nicer exception
            throw new MoxieError("out of sequence expectation or too many times through sequence");
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
}