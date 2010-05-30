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

import junit.framework.Assert;
import moxie.Mock;
import moxie.Moxie;
import moxie.MoxieRunner;
import moxie.Spy;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(MoxieRunner.class)
public class MoxieRunnerTest {
    @Mock
    private TestInterface mock;

    @Spy
    private List<String> strings = new ArrayList<String>();


    @Test
    public void testMagicStuff() {
        Moxie.expect(mock).willReturn("foo").on().aMethod("bar");
        Moxie.expect(strings).willReturn("hi").on().get(0);
        Moxie.expect(strings).willReturn("guten tag").on().get(Moxie.geq(2));

        String result = mock.aMethod("bar");
        Assert.assertEquals("foo", result);

        String result2 = strings.get(0);
        Assert.assertEquals("hi", result2);

        String result3 = strings.get(3);
        Assert.assertEquals("guten tag", result3);
    }
}
