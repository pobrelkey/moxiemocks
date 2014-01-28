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

package moxie.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 *
 * Matches any {@link Number} whose {@link Number#doubleValue() doubleValue()} is equal to a desired value
 * to within an acceptable tolerance.
 * <p>
 *
 * Intended to be a drop-in replacement for Hamcrest's {@link org.hamcrest.number.IsCloseTo} - unlike the original,
 * it can work with any <code>Number</code> and not just {@link Double}s (so it works with
 * {@link java.math.BigDecimal BigDecimal}s, {@link Integer}s, {@link Long}s, {@link Float}s, etc.).
 * <p>
 * @param <T> type of the <code>Number</code> to be matched
 */
public class IsCloseTo<T extends Number> extends TypeSafeMatcher<T> {
    private final double value;
    private final double tolerance;

    public IsCloseTo(double value, double tolerance) {
        this.value = value;
        this.tolerance = tolerance;
    }

    public static <T extends Number> IsCloseTo<T> closeTo(final double value, final double tolerance) {
        return new IsCloseTo<T>(value, tolerance);
    }

    @Override
    protected boolean matchesSafely(T item) {
        return variation(item) <= tolerance;
    }

    private double variation(T item) {
        return Math.abs(item.doubleValue() - value);
    }

    public void describeTo(Description description) {
        description.appendText("a Number within ").appendValue(tolerance).appendText(" of ").appendValue(value);
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText("was outside of tolerance by").appendValue(variation(item)-tolerance);
    }
}
