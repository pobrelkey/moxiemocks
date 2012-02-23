/*
 * Copyright (c) 2011-2012 Moxie contributors
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
import moxie.MoxieMatchers;
import moxie.MoxieOptions;
import moxie.MoxieRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PartiallyMocked.class)
public class PowerMockTest {

    @Rule
    public MoxieRule moxie = new MoxieRule();

    @Test
    public void privateAndFinalMethods() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Moxie.expect(mock).andReturn("orange").on("partF", MoxieMatchers.hasSubstring("x"));
        Moxie.expect(mock).andReturn("blah").on("partE", MoxieMatchers.hasSubstring("x"));
        Assert.assertEquals("sixieme(xxx) blah orange", mock.partsDEandF("xxx"));
        Moxie.verify(mock);
    }

    @Test
    public void staticMethods() {
        Moxie.expect(PartiallyMocked.class).andReturn("wibble").on("aStaticMethod", Moxie.endsWith("bar"));
        Assert.assertEquals("wibble", PartiallyMocked.aStaticMethod("foobar"));
        Moxie.verify(PartiallyMocked.class);
    }

}
