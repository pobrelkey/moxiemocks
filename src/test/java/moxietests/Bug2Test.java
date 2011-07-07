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

package moxietests;

import moxie.Moxie;
import moxie.MoxieOptions;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertNotNull;


public class Bug2Test {

    @Test
    public void testPerformStubTask() {
        Moxie.mock(AutogenStub.class);
    }

    @Test
    public void testPerformStubTaskPermissive() {
        AutogenStub stub = Moxie.mock(AutogenStub.class, MoxieOptions.PERMISSIVE);
        assertNotNull(stub);
    }

    @Test
    public void testPerformStubTaskPermissiveWithVoidBehaviourRecorded() {
        AutogenStub stub = Moxie.mock(AutogenStub.class, MoxieOptions.PERMISSIVE);
        assertNotNull(stub);
        Moxie.expect(stub).once().on().performStubTask();
    }

    @Test
    public void testPerformStubTaskPermissiveWithReturnBehaviourRecorded() {
        AutogenStub stub = Moxie.mock(AutogenStub.class, MoxieOptions.PERMISSIVE);
        assertNotNull(stub);
        Moxie.expect(stub).andReturn(true).once().on().performStubTask();
    }

    public static class AutogenStub {
        public AutogenStub() {
            initialiseStub();
        }

        protected void initialiseStub() {
            System.out.println("yeah, yeah, doin' some initialising");
        }

        public boolean performStubTask() {
            return new Random().nextBoolean();
        }
    }
}
