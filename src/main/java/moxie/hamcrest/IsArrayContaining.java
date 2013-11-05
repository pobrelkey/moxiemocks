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
import org.hamcrest.core.IsEqual;

import java.lang.reflect.Array;

// Hamcrest's IsArrayContaining matcher doesn't look as if it works on primitive arrays.
public class IsArrayContaining<T> extends BaseMatcher<T[]> {
    private final Matcher elementMatcher;

    public IsArrayContaining(Matcher<? super T> elementMatcher) {
        this.elementMatcher = elementMatcher;
    }

    public static <T> IsArrayContaining<T> hasItemInArray(Matcher<? super T> elementMatcher) {
        return new IsArrayContaining<T>(elementMatcher);
    }

    public static <T> IsArrayContaining<T> hasItemInArray(T element) {
        return new IsArrayContaining<T>(IsEqual.equalTo(element));
    }

    public boolean matches(Object o) {
        if (o == null || !o.getClass().isArray()) {
            return false;
        }
        int arraySize = Array.getLength(o);
        for (int i = 0; i < arraySize; i++) {
            if (elementMatcher.matches(Array.get(o, i))) {
                return true;
            }
        }
        return false;
    }

    public void describeTo(Description description) {
        description.appendText("an array containing ");
        elementMatcher.describeTo(description);
    }
}
