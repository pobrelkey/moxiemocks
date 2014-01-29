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

package moxietests;

import moxie.Moxie;
import moxie.MoxieOptions;
import moxie.MoxieUnexpectedInvocationError;
import org.junit.Assert;
import org.junit.Test;

public class HelloWorld {
    @Test
    public void testHappyPath() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).willReturn("you're welcome").on().aMethod("thanks");

        String result = mock.aMethod("thanks");

        Moxie.verify(mock);
        Assert.assertEquals("you're welcome", result);
    }

    @Test(expected= MoxieUnexpectedInvocationError.class)
    public void testUnexpectedPrescriptive() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        mock.aMethod("hi there");
    }

    @Test
    public void testUnexpectedProscriptive() {
        TestInterface mock = Moxie.mock(TestInterface.class, MoxieOptions.PERMISSIVE);
        String result = mock.aMethod("thanks");

        Moxie.verify(mock);
        Assert.assertNull(result);
    }

    @Test(expected=TestException.class)
    public void testThrowsException() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).willThrow(new TestException()).on().aMethod("JHVH");

        mock.aMethod("JHVH");
    }

    @Test
    public void testCardinality() {
        TestInterface mock = Moxie.mock(TestInterface.class);
        Moxie.expect(mock).willReturn("/me hits snooze button").times(3).on().aMethod("WAKE UP");

        mock.aMethod("WAKE UP");
        mock.aMethod("WAKE UP");
        mock.aMethod("WAKE UP");
        try {
            mock.aMethod("WAKE UP");
            Assert.fail("didn't raise error on fourth call");
        } catch (MoxieUnexpectedInvocationError e) {
            // expected
        }

        Moxie.verify();
    }

    private static class TestException extends RuntimeException {
    }

}
