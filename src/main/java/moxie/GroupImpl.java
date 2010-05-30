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
    private final List<ExpectationImpl> expectations = new ArrayList<ExpectationImpl>();
//    private final Set<ExpectationImpl> matchedUnordered = new HashSet<ExpectationImpl>();
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
        expectations.clear();
//        matchedUnordered.clear();
        defaultCardinality = true;
        cardinality = new CardinalityImpl<CardinalityImpl>().once();
        cursor = -1;
    }

    public Throwable getWhereInstantiated() {
        return whereInstantiated;
    }

    public void add(ExpectationImpl expectation) {
        expectations.add(expectation);
    }


    private Set<ExpectationImpl> getUnorderedExpectations() {
        HashSet<ExpectationImpl> result = new HashSet<ExpectationImpl>();
        if (!flags.isStrictlyOrdered()) {
            result.addAll(expectations);
        } else {
            for (ExpectationImpl expectation : expectations) {
                if (expectation.isUnordered()) {
                    result.add(expectation);
                }
            }
        }
        return result;
    }

    public void verify() {
        Set<ExpectationImpl> unmatchedUnordered = getUnorderedExpectations();
        for (ExpectationImpl expectation : unmatchedUnordered) {
            if (!expectation.isSatisfied()) {
                // TODO nicer exception
                throw new MoxieError("not all methods in expectation invoked");
            }
        }
        if (flags.isStrictlyOrdered()) {
            if (expectations.isEmpty() || ((cursor == -1 || cursor == expectations.size() - 1) && cardinality.isSatisfied())) {
                return;
            }
            // TODO nicer exception
            throw new MoxieError("not all expected methods invoked or cardinality not satisfied");
        }
    }

    public ExpectationImpl match(Method method, Object[] args, MethodBehavior behavior) {
        ExpectationImpl result = null;
        for (ExpectationImpl expectation : getUnorderedExpectations()) {
            if (expectation.match(method, args, behavior)) {
//                matchedUnordered.add(expectation);
                result = expectation;
                break;
            }
        }
        if (result == null && flags.isStrictlyOrdered()) {
            if (cardinality.isViable()) {
                if (cursor != -1 && expectations.get(cursor).match(method, args, behavior)) {
                    result = expectations.get(cursor);
                } else if (advanceCursor()) {
                    cursor = 0;
                    cardinality.incrementCount();
                    if (cardinality.isViable() && expectations.get(cursor).match(method, args, behavior)) {
                        result = expectations.get(cursor);
                    }
                }
            }
        }
        if (result != null) {
            for (GroupImpl group : (Set<GroupImpl>) result.getGroups()) {
                group.match(result, method, args);
            }
        }
        return result;
    }

    private boolean advanceCursor() {
        while (++cursor < expectations.size()) {
            if (!expectations.get(cursor).isUnordered()) {
                return false;
            }
        }
        return true;
    }

    public void match(ExpectationImpl expectation, Method method, Object[] args) {
//        if (expectation.isUnordered() || !flags.isStrictlyOrdered()) {
//            matchedUnordered.add(expectation);
//        } else
        if (flags.isStrictlyOrdered()) {
            if (cardinality.isViable()) {
                if (cursor != -1 && expectations.get(cursor) == expectation) {
                    return;
                }
                if (advanceCursor()) {
                    cursor = 0;
                    cardinality.incrementCount();
                }
                if (cardinality.isViable() && expectations.get(cursor) == expectation) {
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