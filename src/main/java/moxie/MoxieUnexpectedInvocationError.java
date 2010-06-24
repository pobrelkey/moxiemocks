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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 *  {@link Error} thrown by Moxie when a method that has not been {@link Expectation expected} has been invoked on a {@link MoxieOptions#PRESCRIPTIVE PRESCRIPTIVE} mock.
 */
public class MoxieUnexpectedInvocationError extends Error {
    MoxieUnexpectedInvocationError(String message, String name, Method invokedMethod, Object[] invocationArgs, Set<ExpectationImpl> unorderedExpectations, List<ExpectationImpl> orderedExpectations) {
        super(createExceptionMessage(message, name, invokedMethod, invocationArgs, unorderedExpectations, orderedExpectations));
    }

    private static String createExceptionMessage(String message, String name, Method invokedMethod, Object[] invocationArgs, Set<ExpectationImpl> unorderedExpectations, List<ExpectationImpl> orderedExpectations) {
        SimpleDescription desc = new SimpleDescription();
        desc.appendText("On \"" + name + "\": " + message + "\n");
        if (invokedMethod != null) {
            desc.appendText("Invoked:\n");
            desc.appendText("    ");
            desc.appendText(invokedMethod.getName());
            desc.appendValueList("(", ", ", ")", invocationArgs);
            desc.appendText("\n");
        }
        MoxieUtils.describeExpectations(desc, unorderedExpectations, orderedExpectations);
        return desc.toString();
    }
}
