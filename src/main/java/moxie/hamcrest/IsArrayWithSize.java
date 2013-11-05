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
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;

import java.lang.reflect.Array;

/**
 * <p>
 * Matches any array (including primitive arrays) whose size satisfies a nested {@link Matcher}.
 * </p>
 * <p>
 * Intended as a substitute for Hamcrest's {@link org.hamcrest.collection.IsArrayWithSize} -
 * unlike the original, this class can work with primitive arrays as well as arrays of objects.
 * </p>
 * @param <T> type of the array to be matched (NOT the element type of the array)
 */
public class IsArrayWithSize<T> extends TypeSafeMatcher<T> {
    private final Matcher sizeMatcher;

    public IsArrayWithSize(Matcher<? super Integer> sizeMatcher) {
        this.sizeMatcher = sizeMatcher;
    }

    @SuppressWarnings("unchecked")
    public static <T> IsArrayWithSize<T> arrayWithSize(Matcher<? super Integer> sizeMatcher) {
        return new IsArrayWithSize(sizeMatcher);
    }

    public static <T> IsArrayWithSize<T> arrayWithSize(int size) {
        return arrayWithSize(IsEqual.equalTo(size));
    }

    public static <T> IsArrayWithSize<T> emptyArray() {
        return arrayWithSize(0);
    }

    public static IsArrayWithSize<boolean[]> booleanArrayWithSize(Matcher<? super Integer> sizeMatcher) {
        return arrayWithSize(sizeMatcher);
    }

    public static IsArrayWithSize<boolean[]> booleanArrayWithSize(int size) {
        return arrayWithSize(size);
    }

    public static IsArrayWithSize<boolean[]> emptyBooleanArray() {
        return arrayWithSize(0);
    }

    public static IsArrayWithSize<byte[]> byteArrayWithSize(Matcher<? super Integer> sizeMatcher) {
        return arrayWithSize(sizeMatcher);
    }

    public static IsArrayWithSize<byte[]> byteArrayWithSize(int size) {
        return arrayWithSize(size);
    }

    public static IsArrayWithSize<byte[]> emptyByteArray() {
        return arrayWithSize(0);
    }

    public static IsArrayWithSize<char[]> charArrayWithSize(Matcher<? super Integer> sizeMatcher) {
        return arrayWithSize(sizeMatcher);
    }

    public static IsArrayWithSize<char[]> charArrayWithSize(int size) {
        return arrayWithSize(size);
    }

    public static IsArrayWithSize<char[]> emptyCharArray() {
        return arrayWithSize(0);
    }

    public static IsArrayWithSize<short[]> shortArrayWithSize(Matcher<? super Integer> sizeMatcher) {
        return arrayWithSize(sizeMatcher);
    }

    public static IsArrayWithSize<short[]> shortArrayWithSize(int size) {
        return arrayWithSize(size);
    }

    public static IsArrayWithSize<short[]> emptyShortArray() {
        return arrayWithSize(0);
    }

    public static IsArrayWithSize<int[]> intArrayWithSize(Matcher<? super Integer> sizeMatcher) {
        return arrayWithSize(sizeMatcher);
    }

    public static IsArrayWithSize<int[]> intArrayWithSize(int size) {
        return arrayWithSize(size);
    }

    public static IsArrayWithSize<int[]> emptyIntArray() {
        return arrayWithSize(0);
    }

    public static IsArrayWithSize<long[]> longArrayWithSize(Matcher<? super Integer> sizeMatcher) {
        return arrayWithSize(sizeMatcher);
    }

    public static IsArrayWithSize<long[]> longArrayWithSize(int size) {
        return arrayWithSize(size);
    }

    public static IsArrayWithSize<long[]> emptyLongArray() {
        return arrayWithSize(0);
    }

    public static IsArrayWithSize<float[]> floatArrayWithSize(Matcher<? super Integer> sizeMatcher) {
        return arrayWithSize(sizeMatcher);
    }

    public static IsArrayWithSize<float[]> floatArrayWithSize(int size) {
        return arrayWithSize(size);
    }

    public static IsArrayWithSize<float[]> emptyFloatArray() {
        return arrayWithSize(0);
    }

    public static IsArrayWithSize<double[]> doubleArrayWithSize(Matcher<? super Integer> sizeMatcher) {
        return arrayWithSize(sizeMatcher);
    }

    public static IsArrayWithSize<double[]> doubleArrayWithSize(int size) {
        return arrayWithSize(size);
    }

    public static IsArrayWithSize<double[]> emptyDoubleArray() {
        return arrayWithSize(0);
    }

    @Override
    protected boolean matchesSafely(T item) {
        return item.getClass().isArray() && sizeMatcher.matches(Array.getLength(item));
    }

    public void describeTo(Description description) {
        description.appendText("an array with size ");
        sizeMatcher.describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        if (!item.getClass().isArray()) {
            mismatchDescription.appendText("was not an array");
        } else {
            mismatchDescription.appendText("size ");
            sizeMatcher.describeMismatch(Array.getLength(item), mismatchDescription);
        }
    }
}
