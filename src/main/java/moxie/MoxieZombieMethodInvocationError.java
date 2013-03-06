/*
 * Copyright (c) 2011 Moxie contributors
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
 * <p>
 * Error thrown when an attempt is made to invoke the original implementation of
 * a method prepared for mocking by PowerMock (final/private/static methods,
 * constructors, or any method on a final class).
 * </p>
 * <p>
 * On most {@link MoxieOptions#PARTIAL PARTIAL} mocks (or methods where
 * {@link moxie.ObjectExpectation#andCallOriginal() andCallOriginal()} is specified),
 * the mock object is a subclass of the mocked class; for every overridable method,
 * CGLIB creates an overriding implementation that delegates to Moxie, which then
 * calls the original implementation on the superclass.  Mocking of final classes,
 * final/private/static methods and constructors is handled differently - as these
 * methods need to be modified in place and not overridden, PowerMock has to throw out
 * the original implementation and replace it with a stub that delegates to Moxie.
 * Any attempts to get Moxie to delegate to the original implementation in this situation
 * will result in this error.
 * </p>
 */
public class MoxieZombieMethodInvocationError extends Error {
    MoxieZombieMethodInvocationError(String s) {
        super(s);
    }
}
