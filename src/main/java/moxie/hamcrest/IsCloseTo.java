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
import org.hamcrest.Matcher;

// Hamcrest's IsCloseTo matcher only works on Doubles.
public class IsCloseTo<T extends Number> extends BaseMatcher<T> {
    private final double value;
    private final double delta;

    public IsCloseTo(double value, double delta) {
        this.value = value;
        this.delta = delta;
    }

    public static <T extends Number> IsCloseTo<T> closeTo(final double value, final double delta) {
        return new IsCloseTo<T>(value, delta);
    }

    public boolean matches(Object o) {
        if (o == null || !(o instanceof Number)) {
            return false;
        }
        return Math.abs(((Number) o).doubleValue() - value) <= delta;
    }

    public void describeTo(Description description) {
        description.appendText("a Number within ").appendValue(delta).appendText(" of ").appendValue(value);
    }
}
