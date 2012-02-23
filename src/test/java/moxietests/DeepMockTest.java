/*
 * Copyright (c) 2012 Moxie contributors
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
import moxie.MoxieRule;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

public class DeepMockTest {
    @Rule
    public MoxieRule moxie = new MoxieRule();


    private static interface DeepMockable {
        List<String> getList();
    }

    @Test
    @Ignore("not yet implemented")
    public void deepExpectation_happyPath1() {
        DeepMockable mock = Moxie.mock(DeepMockable.class);
        Moxie.expect(mock).andReturn(1).on().getList().size();
        Moxie.expect(mock).will().getList().set(1, Moxie.endsWith("there"));

        Assert.assertEquals(1, mock.getList().size());
        mock.getList().set(1, "hi there");
    }

    @Test
    @Ignore("not yet implemented")
    public void deepExpectation_happyPath2() {
        DeepMockable mock = Moxie.mock(DeepMockable.class);
        Moxie.expect(mock).andReturn("Basingstoke").on().getList().subList(1, Moxie.leq(10)).get(Moxie.geq(2));

        Assert.assertEquals("Basingstoke", mock.getList().subList(1, 5).get(4));
    }

    @Test
    @Ignore("not yet implemented")
    public void deepCheck() {
        Assert.fail("TODO: deep stubbing option as in Mockito");
    }

}
