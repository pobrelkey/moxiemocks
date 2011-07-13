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

import moxie.Mock;
import moxie.Moxie;
import moxie.MoxieRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class Bug6Test {
    interface Bug6TestInterface {
        void someVarargsString(Double a, String... b);
    }

    @Rule
    public MoxieRule moxie = new MoxieRule();

    @Mock
    Bug6TestInterface mock;

    @Test
    @Ignore("TODO - fix bug 6")
    public void thisIsBug6() {
        Moxie.expect(mock).on().someVarargsString(2.2, Moxie.anyString());
        mock.someVarargsString(2.2, "woooo!");
    }

    @Test
    @Ignore("TODO - fix bug 6")
    public void thisIsAlsoBug6() {
        Moxie.expect(mock).on().someVarargsString(3.3, Moxie.startsWith("Red"), Moxie.startsWith("Blue"));
        mock.someVarargsString(3.3, "Red Dawn", "Blue Thunder");
    }

}
