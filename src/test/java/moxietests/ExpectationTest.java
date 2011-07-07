/*
 * Copyright (c) 2010-2011 Moxie contributors
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

import moxie.Group;
import moxie.Moxie;
import moxie.MoxieFailedVerificationError;
import moxie.MoxieUnexpectedInvocationError;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpectationTest {

    private static final List<String> STRINGS = Arrays.asList("zero", "one", "two", "three", "four");

    @Test
    @SuppressWarnings("unchecked")
    public void never_happyPath() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).never().will().add(Moxie.anything());
        Moxie.stub(mock).on().size();

        mock.size();
        Moxie.verify(mock);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = MoxieUnexpectedInvocationError.class)
    public void never_sadPath() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).never().will().add(Moxie.anything());
        mock.add("something");
        Assert.fail("should have failed fast");
    }

    @Test
    public void anyTimes_happyPath1() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).anyTimes().on().size();
        // and then don't call size()
        Moxie.verify(mock);
    }

    @Test
    public void anyTimes_happyPath2() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).anyTimes().on().size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test
    public void anyTimes_happyPath3() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).anyTimes().on().size();
        mock.size();
        mock.size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test
    public void atMostOnce_happyPath1() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atMostOnce().on().size();
        // and then don't call size()
        Moxie.verify(mock);
    }

    @Test
    public void atMostOnce_happyPath2() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atMostOnce().on().size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test(expected = MoxieUnexpectedInvocationError.class)
    public void atMostOnce_sadPath() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atMostOnce().on().size();
        mock.size();
        mock.size();
        Assert.fail("should have failed fast");
    }

    @Test(expected = MoxieFailedVerificationError.class)
    public void atLeastOnce_sadPath() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atLeastOnce().on().size();
        // and then don't call size()
        Moxie.verify(mock);
    }

    @Test
    public void atLeastOnce_happyPath1() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atLeastOnce().on().size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test
    public void atLeastOnce_happyPath2() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atLeastOnce().on().size();
        mock.size();
        mock.size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test(expected = MoxieFailedVerificationError.class)
    public void once_sadPath1() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).once().on().size();
        // and then don't call size()
        Moxie.verify(mock);
    }

    @Test
    public void once_happyPath() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atLeastOnce().on().size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test(expected = MoxieUnexpectedInvocationError.class)
    public void once_sadPath2() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).once().on().size();
        mock.size();
        mock.size();
        Assert.fail("should have failed fast");
    }

    @Test(expected = MoxieFailedVerificationError.class)
    public void atLeast_sadPath1() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atLeast(3).on().size();
        // and then don't call size()
        Moxie.verify(mock);
    }

    @Test(expected = MoxieFailedVerificationError.class)
    public void atLeast_sadPath2() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atLeast(3).on().size();
        mock.size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test
    public void atLeast_happyPath1() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atLeast(3).on().size();
        mock.size();
        mock.size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test
    public void atLeast_happyPath2() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atLeast(3).on().size();
        mock.size();
        mock.size();
        mock.size();
        mock.size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void atLeast_sadPath3() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atLeast(-1).on().size();
    }

    @Test
    public void atMost_happyPath1() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atMost(2).on().size();
        // and then don't call size()
        Moxie.verify(mock);
    }

    @Test
    public void atMost_happyPath2() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atMost(2).on().size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test
    public void atMost_happyPath3() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atMost(2).on().size();
        mock.size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test(expected = MoxieUnexpectedInvocationError.class)
    public void atMost_sadPath1() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atMost(2).on().size();
        mock.size();
        mock.size();
        mock.size();
        Assert.fail("should have failed fast");
    }

    @Test(expected = IllegalArgumentException.class)
    public void atMost_sadPath2() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).atMost(-1).on().size();
    }

    @Test(expected = MoxieFailedVerificationError.class)
    public void times_sadPath1() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).times(2).on().size();
        // and then don't call size()
        Moxie.verify(mock);
    }

    @Test(expected = MoxieFailedVerificationError.class)
    public void times_sadPath2() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).times(2).on().size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test
    public void times_happyPath() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).times(2).on().size();
        mock.size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test(expected = MoxieUnexpectedInvocationError.class)
    public void times_sadPath3() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).times(2).on().size();
        mock.size();
        mock.size();
        mock.size();
        Assert.fail("should have failed fast");
    }

    @Test(expected = MoxieFailedVerificationError.class)
    public void times2_sadPath1() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).times(2,3).on().size();
        // and then don't call size()
        Moxie.verify(mock);
    }

    @Test(expected = MoxieFailedVerificationError.class)
    public void times2_sadPath2() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).times(2,3).on().size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test
    public void times2_happyPath1() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).times(2,3).on().size();
        mock.size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test
    public void times2_happyPath2() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).times(2,3).on().size();
        mock.size();
        mock.size();
        mock.size();
        Moxie.verify(mock);
    }

    @Test(expected = MoxieUnexpectedInvocationError.class)
    public void times2_sadPath3() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).times(2,3).on().size();
        mock.size();
        mock.size();
        mock.size();
        mock.size();
        Assert.fail("should have failed fast");
    }

    @Test(expected = IllegalArgumentException.class)
    public void times2_sadPath4() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).times(3,2).on().size();
    }

    @Test
    public void andReturn() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).andReturn("san").when().get(3);
        Assert.assertEquals("san", mock.get(3));
        Moxie.verify(mock);
    }

    private static class MostUnusualError extends Error {}

    @Test(expected=MostUnusualError.class)
    public void andThrow() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).andThrow(new MostUnusualError()).when().get(5);
        mock.get(5);
        Assert.fail("should have thrown error");
    }

    @Test
    public void andVerifyReturn_happyPath() {
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Moxie.expect(spy).andVerifyReturn("two").when().get(2);
        Assert.assertEquals("two", spy.get(2));
        Moxie.verify(spy);
    }

    @Test(expected = MoxieUnexpectedInvocationError.class)
    public void andVerifyReturn_sadPath() {
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Moxie.expect(spy).andVerifyReturn("dwei").when().get(2);
        spy.get(2);
        Assert.fail("should have failed fast");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @SuppressWarnings("unchecked")
    public void andVerifyThrow_happyPath() {
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Moxie.expect(spy).andVerifyThrow(Moxie.isA(IndexOutOfBoundsException.class)).when().get(-1);
        spy.get(-1);
        Assert.fail("where's the IndexOutOfBoundsException?");
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    @SuppressWarnings("unchecked")
    public void andVerifyThrow_sadPath1() {
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Moxie.expect(spy).andVerifyThrow(Moxie.isA(IndexOutOfBoundsException.class)).when().get(2);
        Assert.assertEquals("two", spy.get(2));
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    @SuppressWarnings("unchecked")
    public void andVerifyThrow_sadPath2() {
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Moxie.expect(spy).andVerifyThrow(Moxie.isA(MostUnusualError.class)).when().get(-1);
        spy.get(1);
        Assert.fail("should have failed fast");
    }

    @Test
    public void andDelegateTo_happyPath1() {
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Moxie.expect(spy).andDelegateTo(new ArrayList<String>(Arrays.asList("nul", "un", "deux"))).when().get(1);
        Assert.assertEquals("un", spy.get(1));
        Moxie.verify(spy);
    }

    @Test
    @Ignore(value = "TODO: be smart about interfaces")
    public void andDelegateTo_happyPath2() {
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Moxie.expect(spy).andDelegateTo(Arrays.asList("nul", "un", "deux")).when().get(1);
        Assert.assertEquals("un", spy.get(1));
        Moxie.verify(spy);
    }

    @Test
    public void andHandleWith() {
        InvocationHandler italianHandler = new InvocationHandler() {
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return "due";
            }
        };
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).andHandleWith(italianHandler).when().get(2);
        Assert.assertEquals("due", mock.get(2));
        Moxie.verify(mock);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void groupedExpectations_happyPath1() {
        List mock = Moxie.mock(List.class);
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Group group = Moxie.group();
        Moxie.expect(spy).inGroup(group).on().get(0);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("zero");
        Moxie.expect(spy).inGroup(group).on().get(1);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("one");
        Moxie.expect(spy).inGroup(group).on().get(2);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("two");

        mock.add(spy.get(0));
        mock.add(spy.get(1));
        mock.add(spy.get(2));

        Moxie.verify(mock, spy, group);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    @SuppressWarnings("unchecked")
    public void groupedExpectations_sadPath1() {
        List mock = Moxie.mock(List.class);
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Group group = Moxie.group();
        Moxie.expect(spy).inGroup(group).on().get(0);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("zero");
        Moxie.expect(spy).inGroup(group).on().get(1);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("one");
        Moxie.expect(spy).inGroup(group).on().get(2);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("two");

        mock.add(spy.get(2));
        mock.add(spy.get(1));
        mock.add(spy.get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void groupedExpectations_happyPath2() {
        List mock = Moxie.mock(List.class);
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Group group = Moxie.group();
        group.willBeCalled().times(3);
        Moxie.expect(spy).inGroup(group).on().get(0);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("zero");
        Moxie.expect(spy).inGroup(group).on().get(1);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("one");
        Moxie.expect(spy).inGroup(group).on().get(2);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("two");

        mock.add(spy.get(0));
        mock.add(spy.get(1));
        mock.add(spy.get(2));
        mock.add(spy.get(0));
        mock.add(spy.get(1));
        mock.add(spy.get(2));
        mock.add(spy.get(0));
        mock.add(spy.get(1));
        mock.add(spy.get(2));

        Moxie.verify(mock, spy, group);
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    @SuppressWarnings("unchecked")
    public void groupedExpectations_sadPath2() {
        List mock = Moxie.mock(List.class);
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Group group = Moxie.group();
        group.willBeCalled().times(3);
        Moxie.expect(spy).inGroup(group).on().get(0);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("zero");
        Moxie.expect(spy).inGroup(group).on().get(1);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("one");
        Moxie.expect(spy).inGroup(group).on().get(2);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("two");

        mock.add(spy.get(2));
        mock.add(spy.get(1));
        mock.add(spy.get(0));
        mock.add(spy.get(2));
        mock.add(spy.get(1));
        mock.add(spy.get(0));
        mock.add(spy.get(2));
        mock.add(spy.get(1));
        mock.add(spy.get(0));
    }

    @Test(expected=MoxieUnexpectedInvocationError.class)
    @SuppressWarnings("unchecked")
    public void groupedExpectations_sadPath3() {
        List mock = Moxie.mock(List.class);
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Group group = Moxie.group();
        group.willBeCalled().times(3);
        Moxie.expect(spy).inGroup(group).on().get(0);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("zero");
        Moxie.expect(spy).inGroup(group).on().get(1);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("one");
        Moxie.expect(spy).inGroup(group).on().get(2);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("two");

        mock.add(spy.get(0));
        mock.add(spy.get(1));
        mock.add(spy.get(2));
        mock.add(spy.get(0));
        mock.add(spy.get(1));
        mock.add(spy.get(2));
        mock.add(spy.get(0));
        mock.add(spy.get(1));
        mock.add(spy.get(2));
        mock.add(spy.get(0));
        mock.add(spy.get(1));
        mock.add(spy.get(2));
    }

    @Test(expected=MoxieFailedVerificationError.class)
    @SuppressWarnings("unchecked")
    public void groupedExpectations_sadPath4() {
        List mock = Moxie.mock(List.class);
        List<String> spy = Moxie.spy(new ArrayList<String>(STRINGS));
        Group group = Moxie.group();
        group.willBeCalled().times(3);
        Moxie.expect(spy).inGroup(group).on().get(0);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("zero");
        Moxie.expect(spy).inGroup(group).on().get(1);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("one");
        Moxie.expect(spy).inGroup(group).on().get(2);
        Moxie.expect(mock).inGroup(group).andReturn(true).when().add("two");

        mock.add(spy.get(0));
        mock.add(spy.get(1));
        mock.add(spy.get(2));
        mock.add(spy.get(0));
        mock.add(spy.get(1));
        mock.add(spy.get(2));

        Moxie.verify(mock, spy, group);
    }

    @Test(expected=IllegalArgumentException.class)
    public void wrongReturnType_sadPath() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).andReturn("two").on().size();
    }

    @Test(expected=IllegalArgumentException.class)
    public void wrongExceptionType_sadPath() throws Exception {
        Writer mock = Moxie.mock(Writer.class);
        Moxie.expect(mock).andThrow(new InterruptedException()).on().write("two");
    }

    @Test(expected=IllegalArgumentException.class)
    public void returningValueOnVoidMethod_sadPath() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).andReturn("two").on().clear();
    }

    @Test
    public void behaviorOnConsecutiveCalls_happyPath() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).andConsecutivelyReturn("one", "two").andConsecutivelyThrow(new RuntimeException("three"),new RuntimeException("four")).times(4).on().get(0);

        Assert.assertEquals("one", mock.get(0));
        Assert.assertEquals("two", mock.get(0));
        try {
            mock.get(0);
            Assert.fail("should have thrown exception");
        } catch (RuntimeException e) {
            Assert.assertEquals("three", e.getMessage());
        }
        try {
            mock.get(0);
            Assert.fail("should have thrown exception");
        } catch (RuntimeException e) {
            Assert.assertEquals("four", e.getMessage());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void behaviorOnConsecutiveCalls_tooFew() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).andConsecutivelyReturn("one", "two", "three").times(4).on().get(0);
    }

    @Test(expected = IllegalStateException.class)
    public void behaviorOnConsecutiveCalls_tooMany() {
        List mock = Moxie.mock(List.class);
        Moxie.expect(mock).andConsecutivelyReturn("one", "two", "three").times(2).on().get(0);
    }

}
