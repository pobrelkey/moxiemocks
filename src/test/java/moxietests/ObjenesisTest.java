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
import org.junit.Test;

public class ObjenesisTest {
    public static class ProblematicNoArgConstructorClass {
        public ProblematicNoArgConstructorClass() {
            throw new Error("Constructor throws mad crazy errors!  You need Objenesis to construct me!");
        }

        public void hello(String s) {
            throw new UnsupportedOperationException("this code shouldn't be reached");
        }
    }
    public static class OneArgConstructorClass {
        public OneArgConstructorClass(String arg) {
            throw new Error("Hi, I'm OneArgConstructorClass(" + arg + ") - bypass my constructor with Objenesis.");
        }

        public void hello(String s) {
            throw new UnsupportedOperationException("this code shouldn't be reached");
        }
    }
    public class NonStaticInnerClass {
        public NonStaticInnerClass() {
            throw new Error("Non-static inner classes need Objenesis to be constructed independently.");
        }

        public void hello(String s) {
            throw new UnsupportedOperationException("this code shouldn't be reached");
        }
    }

    @Test
    public void problematicNoArgConstructor() {
        ProblematicNoArgConstructorClass mock = Moxie.mock(ProblematicNoArgConstructorClass.class);
        Moxie.expect(mock).on().hello("Mike");
        mock.hello("Mike");
    }

    @Test
    public void oneArgConstructor() {
        OneArgConstructorClass mock = Moxie.mock(OneArgConstructorClass.class);
        Moxie.expect(mock).on().hello("Brad");
        mock.hello("Brad");
    }

    @Test
    public void nonStaticInnerClass() {
        NonStaticInnerClass mock = Moxie.mock(NonStaticInnerClass.class);
        Moxie.expect(mock).on().hello("Andrew");
        mock.hello("Andrew");
    }

}
