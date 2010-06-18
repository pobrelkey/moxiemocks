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

import moxie.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;

@RunWith(MoxieRunner.class)
public class CheckTest {

    @Mock(options={MoxieOptions.PERMISSIVE})
    private List<String> mock;

    @Spy(options={MoxieOptions.PERMISSIVE})
    private List<String> spy = new ArrayList<String>(Arrays.asList("zero", "one", "two", "three", "four"));

    @GroupOptions(options={MoxieOptions.ORDERED})
    private Group group;

    @Test
    public void never_happyPath1() {
        mock.add("something");
        Moxie.check(mock).didNot().never().get().add("something");
    }

    @Test
    public void never_happyPath2() {
        mock.add("nothing");
        Moxie.check(mock).never().got().add("something");
    }

    @Test(expected = MoxieFailedCheckError.class)
    public void never_sadPath1() {
        mock.add("something");
        Moxie.check(mock).never().got().add("something");
    }

    @Test(expected = MoxieFailedCheckError.class)
    public void never_sadPath2() {
        mock.add("nothing");
        Moxie.check(mock).didNot().never().get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void anyTimes_sadPath1() {
        mock.add("nothing");
        Moxie.check(mock).didNot().anyTimes().get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void anyTimes_sadPath2() {
        mock.add("something");
        Moxie.check(mock).didNot().anyTimes().get().add("something");
    }

    @Test
    public void anyTimes_happyPath1() {
        mock.add("something");
        Moxie.check(mock).anyTimes().got().add("something");
    }

    @Test
    public void anyTimes_happyPath2() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).anyTimes().got().add("something");
    }

    @Test
    public void anyTimes_happyPath3() {
        mock.add("nothing");
        Moxie.check(mock).anyTimes().got().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atLeastOnce_sadPath1() {
        mock.add("nothing");
        Moxie.check(mock).atLeastOnce().got().add("something");
    }

    @Test
    public void atLeastOnce_happyPath1() {
        mock.add("something");
        Moxie.check(mock).atLeastOnce().got().add("something");
    }

    @Test
    public void atLeastOnce_happyPath2() {
        mock.add("something");
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).atLeastOnce().got().add("something");
    }

    @Test
    public void atLeastOnce_happyPath3() {
        mock.add("nothing");
        Moxie.check(mock).didNot().atLeastOnce().get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atLeastOnce_sadPath2() {
        mock.add("something");
        Moxie.check(mock).didNot().atLeastOnce().get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atLeast_sadPath1() {
        mock.add("nothing");
        Moxie.check(mock).atLeast(2).got().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atLeast_sadPath2() {
        mock.add("something");
        Moxie.check(mock).atLeast(2).got().add("something");
    }

    @Test
    public void atLeast_happyPath1() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).atLeast(2).got().add("something");
    }

    @Test
    public void atLeast_happyPath2() {
        mock.add("something");
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).atLeast(2).got().add("something");
    }

    @Test
    public void atLeast_happyPath3() {
        mock.add("something");
        Moxie.check(mock).didNot().atLeast(2).get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atLeast_sadPath3() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).didNot().atLeast(2).get().add("something");
    }

    @Test
    public void atMostOnce_happyPath1() {
        mock.add("nothing");
        Moxie.check(mock).atMostOnce().got().add("something");
    }

    @Test
    public void atMostOnce_happyPath2() {
        mock.add("something");
        Moxie.check(mock).atMostOnce().got().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atMostOnce_sadPath1() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).atMostOnce().got().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atMostOnce_sadPath2() {
        mock.add("nothing");
        Moxie.check(mock).didNot().atMostOnce().get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atMostOnce_sadPath3() {
        mock.add("something");
        Moxie.check(mock).didNot().atMostOnce().get().add("something");
    }

    @Test
    public void atMostOnce_happyPath3() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).didNot().atMostOnce().get().add("something");
    }

    @Test
    public void atMost_happyPath1() {
        mock.add("nothing");
        Moxie.check(mock).atMost(2).got().add("something");
    }

    @Test
    public void atMost_happyPath2() {
        mock.add("something");
        Moxie.check(mock).atMost(2).got().add("something");
    }

    @Test
    public void atMost_happyPath3() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).atMost(2).got().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atMost_sadPath1() {
        mock.add("something");
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).atMost(2).got().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atMost_sadPath2() {
        mock.add("nothing");
        Moxie.check(mock).didNot().atMost(2).get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atMost_sadPath3() {
        mock.add("something");
        Moxie.check(mock).didNot().atMost(2).get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void atMost_sadPath4() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).didNot().atMost(2).get().add("something");
    }

    @Test
    public void atMost_happyPath4() {
        mock.add("something");
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).didNot().atMost(2).get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void once_sadPath1() {
        mock.add("nothing");
        Moxie.check(mock).once().got().add("something");
    }

    @Test
    public void once_happyPath1() {
        mock.add("something");
        Moxie.check(mock).once().got().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void once_sadPath2() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).once().got().add("something");
    }

    @Test
    public void once_happyPath2() {
        mock.add("nothing");
        Moxie.check(mock).didNot().once().get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void once_sadPath3() {
        mock.add("something");
        Moxie.check(mock).didNot().once().get().add("something");
    }

    @Test
    public void once_happyPath3() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).didNot().once().get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void times_sadPath1() {
        mock.add("nothing");
        Moxie.check(mock).times(2).got().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void times_sadPath2() {
        mock.add("something");
        Moxie.check(mock).times(2).got().add("something");
    }

    @Test
    public void times_happyPath1() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).times(2).got().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void times_sadPath3() {
        mock.add("something");
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).times(2).got().add("something");
    }

    @Test
    public void times_happyPath2() {
        mock.add("nothing");
        Moxie.check(mock).didNot().times(2).get().add("something");
    }

    @Test
    public void times_happyPath3() {
        mock.add("something");
        Moxie.check(mock).didNot().times(2).get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void times_sadPath4() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).didNot().times(2).get().add("something");
    }

    @Test
    public void times_happyPath4() {
        mock.add("something");
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).didNot().times(2).get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void times2_sadPath1() {
        mock.add("nothing");
        Moxie.check(mock).times(1,2).got().add("something");
    }

    @Test
    public void times2_happyPath1() {
        mock.add("something");
        Moxie.check(mock).times(1,2).got().add("something");
    }

    @Test
    public void times2_happyPath2() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).times(1,2).got().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void times2_sadPath2() {
        mock.add("something");
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).times(1,2).got().add("something");
    }

    @Test
    public void times2_happyPath3() {
        mock.add("nothing");
        Moxie.check(mock).didNot().times(1,2).get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void times2_sadPath3() {
        mock.add("something");
        Moxie.check(mock).didNot().times(1,2).get().add("something");
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void times2_sadPath4() {
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).didNot().times(1,2).get().add("something");
    }

    @Test
    public void times2_happyPath4() {
        mock.add("something");
        mock.add("something");
        mock.add("something");
        Moxie.check(mock).didNot().times(1,2).get().add("something");
    }

    @Test
    public void returned_happyPath1() {
        Assert.assertEquals("two", spy.get(2));
        Moxie.check(spy).returned("two").when().get(2);
    }

    @Test
    public void returned_happyPath2() {
        Assert.assertEquals("two", spy.get(2));
        Moxie.check(spy).returned(Moxie.gt("earlier in the alphabet")).when().get(2);
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void returned_sadPath1() {
        Assert.assertEquals("two", spy.get(2));
        Moxie.check(spy).returned("three").when().get(2);
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void returned_sadPath2() {
        Assert.assertEquals("two", spy.get(2));
        Moxie.check(spy).didNot().returnValue("two").when().get(2);
    }

    @Test
    public void returned_happyPath3() {
        Assert.assertEquals("two", spy.get(2));
        Moxie.check(spy).didNot().returnValue("thirty").when().get(2);
    }

    @Test
    public void threw_happyPath1() {
        try {
            spy.get(-1);
            Assert.fail("should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected - ignore
        }
        Moxie.check(spy).threw(Moxie.isA(IndexOutOfBoundsException.class)).on().get(-1);
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void threw_sadPath1() {
        Assert.assertEquals("zero", spy.get(0));
        Moxie.check(spy).threw(Moxie.isA(IndexOutOfBoundsException.class)).on().get(0);
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void threw_sadPath2() {
        try {
            spy.get(-1);
            Assert.fail("should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected - ignore
        }
        Moxie.check(spy).didNot().throwException(Moxie.isA(IndexOutOfBoundsException.class)).on().get(-1);
    }

    @Test
    public void threw_happyPath2() {
        Assert.assertEquals("zero", spy.get(0));
        Moxie.check(spy).didNot().throwException(Moxie.isA(IndexOutOfBoundsException.class)).on().get(0);
    }

    @Test
    public void unexpectedly_happyPath1() {
        Moxie.expect(spy).willReturnVerified("one").on().get(1);
        Assert.assertEquals("one", spy.get(1));
        Moxie.check(spy).didNot().unexpectedly().get().get(1);
    }

    @Test
    public void unexpectedly_happyPath2() {
        Moxie.expect(spy).willReturnVerified("one").on().get(1);
        Assert.assertEquals("one", spy.get(1));
        Assert.assertEquals("two", spy.get(2));
        Moxie.check(spy).unexpectedly().got().get(2);
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void unexpectedly_sadPath1() {
        Moxie.expect(spy).willReturnVerified("one").on().get(1);
        Assert.assertEquals("one", spy.get(1));
        Moxie.check(spy).unexpectedly().got().get(1);
    }

    @Test(expected=MoxieFailedCheckError.class)
    public void unexpectedly_sadPath2() {
        Moxie.expect(spy).willReturnVerified("one").on().get(1);
        Assert.assertEquals("one", spy.get(1));
        Assert.assertEquals("two", spy.get(2));
        Moxie.check(spy).didNot().unexpectedly().get().get(2);
    }    

    @Test(expected = IllegalStateException.class)
    public void didNot_abuse() {
        mock.add("something");
        Moxie.check(mock).didNot().didNot().get().add("something");
    }

    @Test(expected = IllegalStateException.class)
    public void cardinality_abuse() {
        mock.add("something");
        Moxie.check(mock).times(2).times(3).got().add("something");
    }

    @Test(expected = IllegalStateException.class)
    public void returned_abuse() {
        Assert.assertEquals("three", spy.get(3));
        Moxie.check(spy).returned("zwei").returned("three").on().get(3);
    }

    @Test(expected = IllegalStateException.class)
    public void unexpectedly_abuse() {
        Assert.assertEquals("three", spy.get(3));
        Moxie.check(spy).unexpectedly().unexpectedly().returned("three").on().get(3);
    }

    @Test
    public void orderedChecks_happyPath1() {
        mock.add("one");
        mock.add("two");
        mock.add("three");
        Moxie.check(mock).inGroup(group).once().got().add("one");
        Moxie.check(mock).inGroup(group).once().got().add("two");
        Moxie.check(mock).inGroup(group).once().got().add("three");
    }

    @Test(expected= MoxieFailedCheckError.class)
    public void orderedChecks_sadPath1() {
        mock.add("one");
        mock.add("two");
        mock.add("three");
        mock.add("four");
        mock.add("five");
        Moxie.check(mock).inGroup(group).once().got().add("one");
        Moxie.check(mock).inGroup(group).once().got().add("two");
        Moxie.check(mock).inGroup(group).once().got().add("five");
        Moxie.check(mock).inGroup(group).once().got().add("four");
    }

    @Test(expected = IllegalStateException.class)
    public void threw_abuse() {
        try {
            spy.get(-1);
            Assert.fail("should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected - ignore
        }
        Moxie.check(spy).threw(Moxie.isA(Error.class)).threw(Moxie.isA(IndexOutOfBoundsException.class)).on().get(-1);
    }

    @Test(expected=MoxieUncheckedInvocationError.class)
    public void checkNothingElseHappened_sadPath() {
        mock.add("foo");
        mock.add("bar");
        mock.add("baz");
        Moxie.check(mock).got().add("foo");
        Moxie.check(mock).got().add("bar");
        Moxie.checkNothingElseHappened();
    }

    @Test
    public void checkNothingElseHappened_happyPath() {
        mock.add("foo");
        mock.add("bar");
        mock.add("baz");
        Moxie.check(mock).got().add("foo");
        Moxie.check(mock).got().add("bar");
        Moxie.check(mock).got().add("baz");
        Moxie.checkNothingElseHappened();
    }

    @Test
    public void checkNothingElseUnexpectedHappened_happyPath() {
        Moxie.expect(mock).andReturn(1).on().size();
        mock.add("foo");
        mock.size();
        Moxie.check(mock).got().add("foo");
        Moxie.checkNothingElseUnexpectedHappened();
    }

    @Test(expected = MoxieUncheckedInvocationError.class)
    public void checkNothingElseUnexpectedHappened_sadPath() {
        Moxie.expect(mock).andReturn(1).on().size();
        mock.add("foo");
        mock.add("bar");
        mock.size();
        Moxie.check(mock).got().add("foo");
        Moxie.checkNothingElseUnexpectedHappened();
    }
}
