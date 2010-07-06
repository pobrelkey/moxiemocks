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

import moxie.Mock;
import moxie.MoxieRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static moxie.Moxie.stub;

public class Bug1Test {

    @Rule
    public MoxieRule moxie = new MoxieRule();

    @Mock
    Runnable someBehaviour;

    Runnable underTest;

    @Before
    public void init() {
        underTest = new SimpleRunnable(someBehaviour);
    }

    @Test
    public void reproduceBug1() throws Throwable {
        stub(someBehaviour).when().run();
        underTest.run();
    }


    private static class SimpleRunnable implements Runnable {

        private final Runnable delegate;

        public SimpleRunnable(Runnable delegate) {
            this.delegate = delegate;
        }

        public void run() {
            delegate.run();
        }
    }
}
