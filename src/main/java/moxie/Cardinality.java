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

/**
 * Domain-specific language methods used to specify how many times an event (method call, sequence of method calls) is expected to occur.
 *
 * @param <T> Type returned by all methods of this interface - for onward call chaining.
 */
public interface Cardinality<T> {
    /**
     * The related event should never occur.
     */
    T never();

    /**
     * The related event should occur exactly one time.
     */
    T once();

    /**
     * The related event should occur one or more times.
     */
    T atLeastOnce();

    /**
     * The related event should occur exactly once, if at all.
     */
    T atMostOnce();

    /**
     * The related event may occur any number of times, if it occurs at all.
     */
    T anyTimes();

    /**
     * The related event should occur exactly <code>times</code> times.
     *
     * @param times The number of times the related event should occur.
     */
    T times(int times);

    /**
     * The related event should occur between <code>minTimes</code> and <code>maxTimes</code> times, inclusive.
     *
     * @param minTimes The minimum number of times the related event should occur.
     * @param maxTimes The maximum number of times the related event should occur.
     */
    T times(int minTimes, int maxTimes);

    /**
     * The related event should occur at least <code>times</code> times.
     *
     * @param times The minimum number of times the related event should occur.
     */
    T atLeast(int times);

    /**
     * The related event should occur at most <code>times</code> times, if at all.
     *
     * @param times The maximum number of times the related event should occur.
     */
    T atMost(int times);
}