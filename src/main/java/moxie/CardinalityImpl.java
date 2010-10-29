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

import org.hamcrest.Description;

import java.util.ArrayList;
import java.util.List;

class CardinalityImpl<T> implements Cardinality<T> {

    private final T returnValue;
    private Integer minTimes = null;
    private Integer maxTimes = null;
    private boolean frozen;
    private int count;
    private List<Runnable> satisfactions = null;

    CardinalityImpl(T returnValue) {
        this.returnValue = returnValue;
    }

    CardinalityImpl() {
        this.returnValue = (T) this;
    }

    public T never() {
        freeze();
        minTimes = maxTimes = 0;
        return returnValue;
    }

    public T once() {
        freeze();
        minTimes = maxTimes = 1;
        return returnValue;
    }

    public T atLeastOnce() {
        freeze();
        minTimes = 1;
        return returnValue;
    }

    public T atMostOnce() {
        freeze();
        maxTimes = 1;
        return returnValue;
    }

    public T anyTimes() {
        freeze();
        return returnValue;
    }

    public T times(int times) {
        freeze();
        minTimes = maxTimes = times;
        validate();
        return returnValue;
    }

    public T times(int minTimes, int maxTimes) {
        freeze();
        this.minTimes = minTimes;
        this.maxTimes = maxTimes;
        validate();
        return returnValue;
    }

    public T atLeast(int times) {
        freeze();
        minTimes = times;
        validate();
        return returnValue;
    }

    public T atMost(int times) {
        freeze();
        maxTimes = times;
        validate();
        return returnValue;
    }

    private void validate() {
        if (minTimes != null && minTimes < 0) {
            throw new IllegalArgumentException("minimum number of times cannot be less than zero");
        } else if (maxTimes != null && maxTimes < 0) {
            throw new IllegalArgumentException("maximum number of times cannot be less than zero");
        } else if (minTimes != null && maxTimes != null && minTimes > maxTimes) {
            throw new IllegalArgumentException("minimum number of times cannot be greater than maximum number of times");
        }
    }

    private void freeze() {
        if (frozen) {
            throw new IllegalStateException("already specified number of times");
        }
        frozen = true;
    }

    private boolean isMatch(int count) {
        return (minTimes == null || count >= minTimes) && (maxTimes == null || count <= maxTimes);
    }

    boolean isViable() {
        return (maxTimes == null || count < maxTimes);
    }

    boolean canMatch() {
        return isMatch(count + 1);
    }

    boolean incrementCount() {
        boolean result = isMatch(++count);
        if (result && satisfactions != null) {
            for (Runnable satisfaction : satisfactions) {
                satisfaction.run();
            }
            satisfactions = null;
        }
        return result;
    }

    boolean isSatisfied() {
        return isMatch(count);
    }

    void reset() {
        count = 0;
    }

    void whenCardinalitySatisfied(Runnable satisfaction) {
        if (isMatch(count)) {
            satisfaction.run();
        } else {
            if (satisfactions == null) {
                satisfactions = new ArrayList<Runnable>();
            }
            satisfactions.add(satisfaction);
        }
    }

    public Integer getMinTimes() {
        return minTimes;
    }

    public Integer getMaxTimes() {
        return maxTimes;
    }

    public void describeExpected(Description description) {
        if (minTimes == null) {
            if (maxTimes == null) {
                description.appendText("any number of times");
            } else if (maxTimes == 1) {
                description.appendText("at most once");
            } else {
                description.appendText("at most " + maxTimes + " times");
            }
        } else if (maxTimes == null) {
            if (minTimes == 1) {
                description.appendText("at least once");
            } else {
                description.appendText("at least " + maxTimes + " times");
            }
        } else if (minTimes.intValue() == maxTimes.intValue()) {
            if (minTimes == 0) {
                description.appendText("never");
            } else if (minTimes == 1) {
                description.appendText("exactly once");
            } else {
                description.appendText("exactly " + minTimes + " times");
            }
        } else {
            description.appendText("between " + minTimes + " and " + maxTimes + " times");
        }
    }

    public void describeCount(Description description) {
        if (count == 0) {
            description.appendText("never");
        } else if (count == 1) {
            description.appendText("once");
        } else {
            description.appendValue(count);
            description.appendText(" times");
        }
    }
}
