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

import moxie.hamcrest.SimpleDescription;

import java.util.Collection;
import java.util.List;

/**
 *  {@link Error} thrown by Moxie when {@link Moxie#verify(Object...) verification} of a group or a mock/spy object uncovers unfulfilled expectations.
 */
public class MoxieFailedVerificationError extends Error {
    MoxieFailedVerificationError(String message, String mockOrGroupName, List<Invocation> invocations, Collection<ExpectationImpl> unorderedExpectations, Collection<ExpectationImpl> orderedExpectations) {
        super(createExceptionMessage(message, mockOrGroupName, invocations, unorderedExpectations, orderedExpectations));
    }

    private static String createExceptionMessage(String message, String mockOrGroupName, List<Invocation> invocations, Collection<ExpectationImpl> unorderedExpectations, Collection<ExpectationImpl> orderedExpectations) {
        SimpleDescription desc = new SimpleDescription();
        desc.appendText("On \"" + mockOrGroupName + "\": " + message + "\n");
        MoxieUtils.describeIfNonEmpty(desc, "Invoked:\n", invocations);
        MoxieUtils.describeExpectations(desc, unorderedExpectations, orderedExpectations);
        return desc.toString();
    }

}