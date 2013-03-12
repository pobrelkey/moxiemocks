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

import java.util.List;
import junit.framework.Assert;
import moxie.Moxie;
import moxie.MoxieRule;
import moxie.Supplier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LambdaExpectationTest.SomeClass.class)
public class LambdaExpectationTest {

    public static class SomeClass {
        public static String blah() { return "blah"; }
        public SomeClass(String param) {}
        public SomeClass(String param1, String param2) {}
    }

    @Rule
    public MoxieRule moxie = new MoxieRule();

    @Test
    public void staticMethod_Runnable_happyPath() {
        // this is far prettier in Java 8, trust me...
        Moxie.expect().times(2).andReturn("wibble").on(new Runnable() {
            public void run() {
                SomeClass.blah();
            }
        });

        Assert.assertEquals("wibble", SomeClass.blah());
        Assert.assertEquals("wibble", SomeClass.blah());
    }

    @Test
    public void staticMethod_Supplier_happyPath() {
        Moxie.expect().times(2).andReturn("feep").on(new Supplier<Object>() {
            public Object get() {
                return SomeClass.blah();
            }
        });

        Assert.assertEquals("feep", SomeClass.blah());
        Assert.assertEquals("feep", SomeClass.blah());
    }

    @Test
    public void constructor_happyPath() {
        // TODO: this fails, PowerMock gets its knickers in a twist.  Hooray!
        Moxie.expect().times(3).on(new Runnable() {
            public void run() {
                new SomeClass(Moxie.startsWith("porridge"));
            }
        });
        Moxie.expect().times(2).on(new Supplier<Object>() {
            public Object get() {
                return new SomeClass(Moxie.startsWith("dog"));
            }
        });

        new SomeClass("porridge oats");
        new SomeClass("porridge-colored sky");
        new SomeClass("dog-eared");
        new SomeClass("dog's breakfast");
        new SomeClass("porridge grey");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mockInstances_happyPath() {
        // TODO: this also fails, Javassist chokes on primitive types.
        final List<String> mockList = Moxie.mock(List.class);

        Moxie.stub().andReturn("frog").on(new Runnable() {
            public void run() {
                mockList.get(2);
            }
        });
        Moxie.expect().times(2).andReturn("toad").on(new Runnable() {
            public void run() {
                mockList.get(Moxie.gt(6));
            }
        });
        Moxie.stub().andReturn("snake").on(new Supplier<Object>() {
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
