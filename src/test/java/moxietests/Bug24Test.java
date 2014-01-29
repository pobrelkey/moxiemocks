/*
 * Copyright (c) 2014 Moxie contributors
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
import moxie.Moxie;
import moxie.MoxieRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class Bug24Test {

    @Rule
    public MoxieRule moxie = new MoxieRule();

    @Mock private Bug24_SomeImplementation blah = null;

    private Bug24_UnderTest<String> underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new Bug24_UnderTest<String>(blah);
    }

    @Test
    public void simpleHappyPath() {
        Moxie.expect(blah).once().andReturn("returned from mock").on().doSomething(Moxie.anyString());

        Assert.assertEquals("Dependency did: returned from mock", underTest.getDependencyToDoSomething("not important"));
    }

    private static class Bug24_UnderTest<T> {
        private final Bug24_SomeInterface<T> dependency;
        public Bug24_UnderTest(Bug24_SomeInterface<T> dependency) {
            this.dependency = dependency;
        }
        public String getDependencyToDoSomething(String param) {
            return "Dependency did: " + dependency.doSomething(param).toString();
        }
    }

    public static class Bug24_SomeImplementation implements Bug24_SomeInterface<String> {
        public String doSomething(String param) {
            throw new Error("Don't call the original!");
        }
    }

    public static interface Bug24_SomeInterface<T> {
        T doSomething(String param);
    }
}
