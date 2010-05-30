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

import java.util.LinkedList;

interface MoxieMethods {
    <T> T mock(Class<T> clazz, String name, MoxieOptions... options);

    <T> T spy(T realObject, String name, MoxieOptions... options);

    public Group group(String name, MoxieOptions... options);

    <T> Expectation<T> expect(T mockObject);

    <T> Check<T> check(T mockObject);

    void checkNothingElseHappened(Object... mockObjects);

    void checkNothingElseUnexpectedHappened(Object... mockObjects);

    void verify(Object... mockObjects);

    void verifySoFar(Object... mockObjects);

    void verifyAndReset(Object... mockObjects);

    void reset(Object... mockObjects);

    void deactivate(Object... mockObjects);

    void verifyAndReset(Object mockObject, MoxieOptions firstOption, MoxieOptions... otherOptions);

    void reset(Object mockObject, MoxieOptions firstOption, MoxieOptions... otherOptions);

    void checkNoActiveMocks();

    void reportMatcher(Matcher matcher, Class expectedType);

    void autoMock(Object... testComponents);

    void autoUnMock(Object... testComponents);

    // Moxie-internal methods below this line...
    LinkedList<MatcherReport> getMatcherReports();
}
