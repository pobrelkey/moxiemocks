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
import moxie.ThrowingRunnable;
import moxie.ThrowingSupplier;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PartiallyMocked.class)
public class LambdaExpectationTest {

    @Rule
    public MoxieRule moxie = new MoxieRule();

    @Test
    public void staticMethod_Runnable_happyPath() {
        // this is far prettier in Java 8, trust me...
        Moxie.expect().times(2).andReturn("wibble").onVoid(new ThrowingRunnable() {
            public void run() {
                PartiallyMocked.aStaticMethod(Moxie.startsWith("foo"));
            }
        });

        Assert.assertEquals("wibble", PartiallyMocked.aStaticMethod("foobar"));
        Assert.assertEquals("wibble", PartiallyMocked.aStaticMethod("foo industries ltd."));
    }

    @Test
    public void staticMethod_Supplier_happyPath() {
        Moxie.expect().times(2).andReturn("feep").on(new ThrowingSupplier<Object>() {
            public Object get() {
                return PartiallyMocked.aStaticMethod("blah");
            }
        });

        Assert.assertEquals("feep", PartiallyMocked.aStaticMethod("blah"));
        Assert.assertEquals("feep", PartiallyMocked.aStaticMethod("blah"));
    }

    @Test
    @PrepareForTest(fullyQualifiedNames = "moxietests.*")  // IMPORTANT - this PreparesForTest this method's lambda classes!
    public void constructor_happyPath() {
        Moxie.expect().times(3).onVoid(new ThrowingRunnable() {
            public void run() {
                new PartiallyMocked(Moxie.startsWith("porridge"));
            }
        });
        Moxie.expect().times(2).on(new ThrowingSupplier<Object>() {
            public Object get() {
                return new PartiallyMocked(Moxie.startsWith("dog"));
            }
        });

        new PartiallyMocked("porridge oats");
        new PartiallyMocked("porridge-colored sky");
        new PartiallyMocked("dog-eared");
        new PartiallyMocked("dog's breakfast");
        new PartiallyMocked("porridge grey");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mockInstances_happyPath() {
        final List<String> mockList = Moxie.mock(List.class);

        Moxie.stub().andReturn("frog").onVoid(new ThrowingRunnable() {
            public void run() {
                mockList.get(2);
            }
        });
        Moxie.expect().times(2).andReturn("toad").onVoid(new ThrowingRunnable() {
            public void run() {
                mockList.get(Moxie.gt(6));
            }
        });
        Moxie.stub().andReturn("snake").on(new ThrowingSupplier<Object>() {
            public Object get() {
                return mockList.get(4);
            }
        });

        Assert.assertEquals("frog", mockList.get(2));
        Assert.assertEquals("toad", mockList.get(8));
        Assert.assertEquals("toad", mockList.get(43));
        Assert.assertEquals("snake", mockList.get(4));
    }
}
