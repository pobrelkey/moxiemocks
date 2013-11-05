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
import org.hamcrest.SelfDescribing;

import java.util.Arrays;
import java.util.Collections;

/**
 * <p>
 * Provides a simple implementation of the {@link Description} interface backed by a {@link StringBuilder}.
 * </p>
 * <p>
 * Intended as a substitute for Hamcrest's {@link org.hamcrest.StringDescription} - unlike that class this
 * implementation has some bombproofing in case it's fed a value whose {@link Object#toString() toString()}
 * method throws an error.  This is particularly important when building exception messages for
 * the "real" cause of a test failure - this process should not curtail the test by throwing a second,
 * far more obscure error from the guts of the test framework, leaving the user no wiser as to why their
 * test has failed.
 * </p>
 */
public class SimpleDescription implements Description {

    private final StringBuilder stringBuilder = new StringBuilder();

    public Description appendText(String s) {
        stringBuilder.append(s);
        return this;
    }

    public Description appendDescriptionOf(SelfDescribing selfDescribing) {
        selfDescribing.describeTo(this);
        return this;
    }

    public Description appendValue(Object o) {
        String value;
        try {
            value = String.valueOf(o);
        } catch (Throwable e) {
            value = String.format("%s@%x", o.getClass().getName(), System.identityHashCode(o));
        }
        stringBuilder.append(value);
        return this;
    }

    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        return appendValueList(start, separator, end, values != null ? Arrays.asList(values) : Collections.emptyList());
    }

    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
        stringBuilder.append(start);
        boolean first = true;
        for (T value : values) {
            if (!first) {
                stringBuilder.append(separator);
            }
            first = false;
            appendValue(value);
        }
        stringBuilder.append(end);
        return this;
    }

    public Description appendList(String start, String separator, String end, Iterable<? extends SelfDescribing> values) {
        stringBuilder.append(start);
        boolean first = true;
        for (SelfDescribing value : values) {
            if (!first) {
                stringBuilder.append(separator);
            }
            first = false;
            value.describeTo(this);
        }
        stringBuilder.append(end);
        return this;
    }

    /**
     * Returns the description built up from calls to this object so far.
     * @return the description
     */
    @Override
    public String toString() {
        return stringBuilder.toString();
    }

    /**
     * Renders the description of a {@link SelfDescribing} to a {@link String}.
     * @param selfDescribing the item whose description we'd like to obtain
     * @return the description
     */
    public static String toString(SelfDescribing selfDescribing) {
        return new SimpleDescription().appendDescriptionOf(selfDescribing).toString();
    }

    /**
     * Renders the description of a {@link SelfDescribing} to a {@link String}.
     * @param selfDescribing the item whose description we'd like to obtain
     * @return the description
     */
    public static String asString(SelfDescribing selfDescribing) {
        return toString(selfDescribing);
    }
}
