/*
 * Copyright (c) 2013 Moxie contributors
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
import moxie.MoxieRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class Bug18Test {
    private static class ClassWithoutANoArgConstructor {
        private final String name;
        private int helloCount = 0;

        public ClassWithoutANoArgConstructor(String name) {
            this.name = name;
        }

        public String sayHello() {
            helloCount++;
            return "Hello, " + name + "!";
        }

        public int getHelloCount() {
            return helloCount;
        }
    }

    @Rule
    public MoxieRule moxie = new MoxieRule();

    @Test
    public void happyPath() {
        ClassWithoutANoArgConstructor helloJed = new ClassWithoutANoArgConstructor("Jed");
        ClassWithoutANoArgConstructor spy = Moxie.spy(helloJed);
        Moxie.expect(spy).andVerifyReturn("Hello, Jed!").on().sayHello();
        Moxie.stub(spy).on().getHelloCount();

        spy.sayHello();

        Assert.assertEquals(1, spy.getHelloCount());
    }


}
