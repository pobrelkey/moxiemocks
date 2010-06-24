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

import org.hamcrest.Matcher;

import java.lang.reflect.Method;
import java.util.List;

/**
 *  {@link Error} thrown by Moxie when a {@link Check} fails.
 */
public class MoxieFailedCheckError extends Error {
    MoxieFailedCheckError(String message, Method checkedMethod, List<Matcher> argMatchers, CardinalityImpl cardinality, Matcher throwableMatcher, Matcher resultMatcher, List<Invocation> invocations) {
        super(createExceptionMessage(message, checkedMethod, argMatchers, cardinality, throwableMatcher, resultMatcher, invocations));
    }

    private static String createExceptionMessage(String message, Method checkedMethod, List<Matcher> argMatchers, CardinalityImpl cardinality, Matcher throwableMatcher, Matcher resultMatcher, List<Invocation> invocations) {
        SimpleDescription desc = new SimpleDescription();
        desc.appendText(message + "\n");

        desc.appendText("Checked:\n");
        desc.appendText("    expected ");
        cardinality.describeTo(desc);
        desc.appendText(": ");
        desc.appendText(checkedMethod.getName());
        desc.appendValueList("(", ", ", ")", argMatchers);
        if (throwableMatcher != null) {
            desc.appendText(", throws ");
            throwableMatcher.describeTo(desc);
        } else if (resultMatcher != null) {
            desc.appendText(", returns ");
            resultMatcher.describeTo(desc);
        }
        desc.appendText("\n");

        desc.appendText("Invoked:\n");
        MoxieUtils.describeIfNonEmpty(desc, "Invoked:\n", invocations);
        return desc.toString();
    }
}