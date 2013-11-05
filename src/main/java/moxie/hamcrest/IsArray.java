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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Matches any array (including primitive arrays) where each element of the array satisfies the corresponding
 * {@link Matcher} in a sequence of <code>Matcher</code>s, and with the same number of elements as the
 * sequence of <code>Matcher</code>s.
 * </p>
 * <p>
 * Intended as a substitute for Hamcrest's {@link org.hamcrest.collection.IsArrayContaining} -
 * unlike the original, this class can work with primitive arrays as well as arrays of objects.
 * </p>
 * @param <T> type of the array to be matched (NOT the element type of the array)
 */
public class IsArray<T> extends TypeSafeMatcher<T> {
    private final List<Matcher> elementMatchers;

    @SuppressWarnings("unchecked")
    public IsArray(Matcher... elementMatchers) {
        this((List) Arrays.asList(elementMatchers.clone()));
    }

    public IsArray(List<Matcher> elementMatchers) {
        this.elementMatchers = new ArrayList<Matcher>(elementMatchers);
    }

    static public <T> IsArray<T[]> array(Matcher<? super T>... elementMatchers) {
        return new IsArray<T[]>(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public <T> IsArray<T[]> array(List<Matcher<? super T>> elementMatchers) {
        return new IsArray<T[]>((List) elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<boolean[]> booleanArray(Matcher<? super Boolean>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<byte[]> byteArray(Matcher<? super Byte>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<char[]> charArray(Matcher<? super Character>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<short[]> shortArray(Matcher<? super Short>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<int[]> intArray(Matcher<? super Integer>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<long[]> longArray(Matcher<? super Long>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<float[]> floatArray(Matcher<? super Float>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<double[]> doubleArray(Matcher<? super Double>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static private IsArray arrayEquals(final Object arrayValue) {
        List<Matcher> matchers = new ArrayList<Matcher>();
        int arraySize = Array.getLength(arrayValue);
        for (int i = 0; i < arraySize; i++) {
            Object element = Array.get(arrayValue, i);
            matchers.add(IsEqual.equalTo(element));
        }
        return new IsArray(matchers);
    }

    @SuppressWarnings("unchecked")
    static public <T> IsArray<T[]> arrayEqualTo(T... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<boolean[]> booleanArrayEqualTo(boolean... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<byte[]> byteArrayEqualTo(byte... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<char[]> charArrayEqualTo(char... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<short[]> shortArrayEqualTo(short... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<int[]> intArrayEqualTo(int... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<long[]> longArrayEqualTo(long... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<float[]> floatArrayEqualTo(float... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<double[]> doubleArrayEqualTo(double... elements) {
        return arrayEquals(elements);
    }


    @Override
    protected boolean matchesSafely(T item) {
        if (!item.getClass().isArray()) {
            return false;
        }
        int arraySize = Array.getLength(item);
        if (elementMatchers.size() != arraySize) {
            return false;
        }
        for (int i = 0; i < arraySize; i++) {
            if (!elementMatchers.get(i).matches(Array.get(item, i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        if (!item.getClass().isArray()) {
            mismatchDescription.appendText("was not an array");
            return;
        }
        int arraySize = Array.getLength(item);
        if (elementMatchers.size() != arraySize) {
            mismatchDescription.appendText("length was ").appendValue(arraySize);
            return;
        }
        for (int i = 0; i < arraySize; i++) {
            Matcher elementMatcher = elementMatchers.get(i);
            Object element = Array.get(item, i);
            if (!elementMatcher.matches(element)) {
                mismatchDescription.appendText("element ").appendValue(i).appendText(" ");
                elementMatcher.describeMismatch(element, mismatchDescription);
                return;
            }
        }
    }

    public void describeTo(Description description) {
        description.appendList("[", ", ", "]", elementMatchers);
    }
}
