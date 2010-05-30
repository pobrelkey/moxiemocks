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

package moxie;

import org.hamcrest.Matcher;

import java.util.LinkedList;

/**
 * <p>
 * Static class comprising the main public API of Moxie, a wicked good Java mocking library.
 * </p>
 *
 * <hr />
 *
 * <h2>Intro</h2>
 *
 * <p>
 * Moxie is a library for creating <a href="http://www.mockobjects.com/">mock objects</a> in <a href="http://java.sun.com/">Java</a> unit tests.
 * </p>
 * <p>
 * Why use Moxie?  It's...
 * </p>
 *
 * <dl>
 * <dt><b>Concise</b></dt>
 * <dd>
 * <p>
 * Creating a mock takes one call.<br />
 * Setting each expectation requires exactly one Java statement - even for void methods.<br />
 * Verifying all your expectations takes just one call.<br />
 * That's all.
 * </p>
 * <p>
 * (And if you <code>@RunWith(MoxieRunner.class)</code> under <a href="http://junit.sourceforge.net/">JUnit 4</a>, you can even leave out the first and last bits!)
 * </p>
 * <p>
 * Moxie's syntax is refactoring-safe, and 100% inner class free.
 * </p>
 * </dd>
 *
 * <dt><b>Persnickety</b></dt>
 * <dd>
 * <p>
 * Unless otherwise specified, mock calls that are not explicitly expected raise an error.
 * </p>
 * <p>
 * Call ordering can also easily be verified - even across mocks.
 * </p>
 * </dd>
 *
 * <dt><b>Powerful</b></dt>
 * <dd>
 * <p>
 * You can mock concrete classes as well as interfaces, through the magic of <a href="http://cglib.sourceforge.net/">cglib</a>.  (Except final methods/classes, of course.)
 * </p>
 * <p>
 * You can also create "spy" objects - real objects wrapped in an expectation-checking proxy.
 * </p>
 * <p>
 * Parameter matching is flexible - use your favorite <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> matchers, or write your own.
 * </p>
 * </dd>
 *
 * <dt><b>Flexible</b></dt>
 * <dd>
 * <p>
 * Lets you write your tests the way you want to - either in traditional expect-run-verify style, or in <a href="http://www.mockito.org/">Mockito</a>-like stub-run-check style.
 * </p>
 * </dd>
 * </dl>
 *
 * <h2>Design goals</h2>
 * <ul>
 * <li>Minimal, consistent, refactorable, easy-to-use mocking syntax.</li>
 * <li>API errs toward simplicity and flexibility rather than forcing you to write "good" tests or follow a particular mocking approach.</li>
 * </ul>
 *
 * <h2>Examples</h2>
 *
 * <b>TODO</b>
 *
 *
 * <h2>Moxie versus the competition</h2>
 *
 * <p>
 * I've only included mocking tools I've had experience with - if anyone out there wants to contribute a comparison to another tool, feel free.
 * Tools are sorted from newest to oldest.
 * </p>
 *
 * <h3><a href="www.mockito.org">Mockito</a></h3>
 *
 * <p>
 * A simple example...
 * </p>
 * <dl>
 * <dt><u>Mockito</u></dt>
 * <dd><pre>
 * List mockList = Mockito.mock(List.class);
 * Mockito.when(mockList.get(0)).thenReturn("zero");
 * Mockito.when(mockList.get(1)).thenReturn("one");
 *
 * // code that calls the mock
 *
 * Mockito.verify(mockList).get(0);
 * Mockito.verify(mockList).get(1);
 * Mockito.verify(mockList).clear();
 * Mockito.verifyNoMoreInteractions(mockList);
 * </pre></dd>
 *
 * <dt><u>Moxie</u></dt>
 * <dd><pre>
 * List mockList = Moxie.mock(List.class);
 * Moxie.expect(mockList).willReturn("zero").on().get(0);
 * Moxie.expect(mockList).willReturn("one").on().get(1);
 * Moxie.expect(mockList).will().clear();
 *
 * // code that calls the mock
 *
 * Moxie.verify();
 * </pre></dd>
 * </dl>
 *
 * <p>
 * Admittedly not a fair test - it just shows that Mockito is ill-suited for expect-run-verify style testing.  So let's try another example that's more Mockito-style.
 * </p>
 *
 * <dl>
 * <dt><u>Mockito</u></dt>
 * <dd><pre>
 * List mockList = Mockito.mock(List.class);
 * Mockito.when(mockList.get(0)).thenReturn("zero");
 * Mockito.when(mockList.get(1)).thenReturn("one");
 *
 * // code that calls the mock
 *
 * Mockito.verify(mockList).clear();
 * </pre></dd>
 *
 * <dt><u>Moxie</u></dt>
 * <dd><pre>
 * List mockList = Moxie.mock(List.class, MoxieOptions.PERMISSIVE);
 * Moxie.stub(mockList).willReturn("zero").on().get(0);
 * Moxie.stub(mockList).willReturn("one").on().get(1);
 *
 * // code that calls the mock
 *
 * Moxie.check(mockList).got().clear();
 * </pre></dd>
 * </dl>
 *
 * <p>
 * Major behavior differences with Mockito:
 * </p>
 * <ul>
 * <li>Default mock method behavior is to return <code>null</code> on a method whose return type is a <code>Collection</code>, not an empty instance of that kind of collection.</li>
 * <li>"Magic nulls" aren't supported.  They have a Heisenberg effect on code that gets them; you may as well just set an expectation.</li>
 * </ul>
 *
 *
 * <h3><a href="http://easymock.org/">EasyMock 2</a></h3>
 *
 * <dl>
 * <dt><u>EasyMock</u></dt>
 * <dd><pre>
 * List mockList = EasyMock.createMock(List.class);
 * EasyMock.expect(mockList.get(0)).andStubReturn("one");
 * EasyMock.expect(mockList.get(1)).andStubReturn("two");
 * mockList.clear();
 *
 * EasyMock.replay(mockList);
 *
 * // code that calls the mock
 *
 * EasyMock.verify(mockList);
 * </pre></dd>
 *
 * <dt><u>Moxie</u></dt>
 * <dd><pre>
 * List mockList = Moxie.mock(List.class);
 * Moxie.expect(mockList).willReturn("zero").on().get(0);
 * Moxie.expect(mockList).willReturn("one").on().get(1);
 * Moxie.expect(mockList).will().clear();
 *
 * // code that calls the mock
 *
 * Moxie.verify();
 * </pre></dd>
 * </dl>
 *
 *
 * <h3><a href="http://www.jmock.org/">JMock 2</a></h3>
 *
 *
 * <p>The following JMock 2 code is marginally more concise if you use JMock 2's provided JUnit runner/base class for your tests.</p>
 *
 * <dl>
 * <dt><u>JMock 2</u></dt>
 *
 * <dd><pre>
 * Mockery context = new Mockery();
 * final List mockList = context.mock(List.class);
 * context.checking(new Expectations() {{
 *     oneOf(mockList).get(0);
 *     oneOf(mockList).get(1);
 *     oneOf(mockList).clear();
 * }});
 *
 * // code that calls the mock
 *
 * context.assertIsSatisfied();
 * </pre></dd>
 *
 * <dt><u>Moxie</u></dt>
 * <dd><pre>
 * List mockList = Moxie.mock(List.class);
 * Moxie.expect(mockList).willReturn("zero").on().get(0);
 * Moxie.expect(mockList).willReturn("one").on().get(1);
 * Moxie.expect(mockList).will().clear();
 *
 * // code that calls the mock
 *
 * Moxie.verify();
 * </pre></dd>
 * </dl>
 *
 *
 * <h3><a href="http://www.jmock.org/jmock1.html">JMock 1</a></h3>
 *
 * <p>
 * JMock 1 is outdated; unless you're stuck on Java 1.4 or earlier, you really ought to move to something more modern.
 * </p>
 * <p>
 * The following example assumes the JMock 1 code extends <code>MockObjectTestCase</code>.
 * </p>
 * <dl>
 * <dt><u>JMock 1</u></dt>
 * <dd><pre>
 * Mock listMock = mock(List.class);
 * listMock.expects(atLeastOnce()).method("get").with(eq(0)).will(returnValue("zero"));
 * listMock.expects(atLeastOnce()).method("get").with(eq(1)).will(returnValue("one"));
 * listMock.expects(atLeastOnce()).method("clear").withNoArguments();
 * List list = (List) listMock.proxy();
 *
 * // code that calls the mock
 *
 * // expectations automatically checked by base class
 * </pre></dd>
 *
 * <dt><u>Moxie</u></dt>
 * <dd><pre>
 * List mockList = Moxie.mock(List.class);
 * Moxie.expect(mockList).willReturn("zero").on().get(0);
 * Moxie.expect(mockList).willReturn("one").on().get(1);
 * Moxie.expect(mockList).will().clear();
 *
 * // code that calls the mock
 *
 * Moxie.verify();
 * </pre></dd>
 * </dl>
 *
 *
 * <h2>Why yet another mocking library?</h2>
 *
 * <p>
 * Not long ago I found myself on a long-running project where four separate approaches to mocking
 *     (<a href="http://www.jmock.org/jmock1.html">JMock 1</a>, <a href="http://www.jmock.org/">JMock 2</a>,
 *     <a href="http://easymock.org/">EasyMock</a>, <a href="www.mockito.org">Mockito</a>) were scattered
 *     throughout the code - partly for legacy reasons, partly out of religious squabbling.
 *     Everyone wanted to standardize on <i>something</i>, but no one could get enthusiastic about any one tool -
 *     because over time, as the iteration number pushed into the triple digits, the drawbacks of each of
 *     the contenders had become abundantly clear:
 * </p>
 *
 * <ul>
 * <li>JMock 1's syntax was good for its day, but because it's reflection-heavy, it breaks whenever you refactor.
 *     It also requires your test classes to extend JMock's test case class, which we didn't like.</li>
 * <li>JMock 2's syntax was far too chatty and peculiar for most people's taste; having to write an anonymous inner class
 *     for each test was seen as unnecessary clutter.</li>
 * <li>EasyMock was the preferred choice, but no one liked the statefulness of mock objects - i.e. having to shift them from setup mode into replay mode before use.
 *     The expectation API is also a bit clunky - nobody liked calling <code>expectLastCall()</code> when mocking void methods.</li>
 * <li>Mockito was most people's favorite to work with, but was deemed unacceptable because Mockito mocks don't blow up on unexpected calls,
 *     and require extensive manual checks after use.  It too suffers from an inconsistent void-method mocking syntax.</li>
 * </ul>
 *
 * <p>
 * Moxie was a product of this frustration - intended to address these concerns, make everyone on the team happy, and end the war.
 * </p>
 */
public abstract class Moxie extends MoxieMatchers {
    static MoxieMethods instance = MoxieUtils.createThreadLocalProxy(MoxieMethods.class, new MoxieUtils.Factory<MoxieMethods>() {
        public MoxieMethods create() {
            return new MoxieImpl();
        }
    });

    private Moxie() {
    }

    /**
     * Creates a mock object of the given type.
     *
     * @param clazz the class or interface that the mock should extend/implement
     * @param <T>   the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz) {
        return mock(clazz, (String) null, MoxieOptions.MOCK_DEFAULTS);
    }

    /**
     * Creates a mock object of the given type.
     *
     * @param clazz the class or interface that the mock should extend/implement
     * @param name  the name of the mock object - will be used in error messages
     * @param <T>   the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz, String name) {
        return mock(clazz, name, MoxieOptions.MOCK_DEFAULTS);
    }

    /**
     * Creates a mock object of the given type.
     *
     * @param clazz   the class or interface that the mock should extend/implement
     * @param options one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>     the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz, MoxieOptions... options) {
        return mock(clazz, null, options);
    }

    /**
     * Creates a mock object of the given type.
     *
     * @param clazz   the class or interface that the mock should extend/implement
     * @param name    the name of the mock object - will be used in error messages
     * @param options one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>     the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz, String name, MoxieOptions... options) {
        return instance.mock(clazz, name, options);
    }

    /**
     * Creates a spy object, i.e. a proxy which wraps an actual object on which expectations can be set
     * and checks can be performed.
     *
     * @param realObject the object the spy should wrap
     * @param <T>        type of the object to be spied upon
     * @return a new spy object
     */
    static public <T> T spy(T realObject) {
        return spy(realObject, (String) null, MoxieOptions.MOCK_DEFAULTS);
    }

    /**
     * Creates a spy object, i.e. a proxy which wraps an actual object on which expectations can be set
     * and checks can be performed.
     *
     * @param realObject the object the spy should wrap
     * @param name       the name of the spy object - will be used in error messages
     * @param <T>        type of the object to be spied upon
     * @return a new spy object
     */
    static public <T> T spy(T realObject, String name) {
        return spy(realObject, name, MoxieOptions.MOCK_DEFAULTS);
    }

    /**
     * Creates a spy object, i.e. a proxy which wraps an actual object on which expectations can be set
     * and checks can be performed.
     *
     * @param realObject the object the spy should wrap
     * @param options    one or more {@link MoxieOptions} that should apply to the spy
     * @param <T>        type of the object to be spied upon
     * @return a new spy object
     */
    static public <T> T spy(T realObject, MoxieOptions... options) {
        return spy(realObject, null, options);
    }

    /**
     * Creates a spy object, i.e. a proxy which wraps an actual object on which expectations can be set
     * and checks can be performed.
     *
     * @param realObject the object the spy should wrap
     * @param name       the name of the spy object - will be used in error messages
     * @param options    one or more {@link MoxieOptions} that should apply to the spy
     * @param <T>        type of the object to be spied upon
     * @return a new spy object
     */
    static public <T> T spy(T realObject, String name, MoxieOptions... options) {
        return instance.spy(realObject, name, options);
    }

    /**
     * <p>
     * Creates a new {@link Group} to represent a group of calls to be tracked across mocks.
     * </p>
     * <p>
     * The returned object can then be passed to {@link Expectation#inGroup(Group...) Expectation.inGroup()}
     * or {@link Check#inGroup(Group...)  Check.inGroup()} to associate expectations/checks
     * with the group.
     * </p>
     * <p>
     * See the discussion in the summary javadoc for the {@link Group}
     * interface for more details.
     * </p>
     *
     * @return a new {@link Group}
     */
    static public Group group() {
        return instance.group(null, MoxieOptions.GROUP_DEFAULTS);
    }

    /**
     * <p>
     * Creates a new {@link Group} to represent a group of calls to be tracked across mocks.
     * </p>
     * <p>
     * The returned object can then be passed to {@link Expectation#inGroup(Group...) Expectation.inGroup()}
     * or {@link Check#inGroup(Group...)  Check.inGroup()} to associate expectations/checks
     * with the group.
     * </p>
     * <p>
     * See the discussion in the summary javadoc for the {@link Group}
     * interface for more details.
     * </p>
     *
     * @param name the name of the group - will be used in error messages
     * @return a new {@link Group}
     */
    static public Group group(String name) {
        return instance.group(name);
    }

    /**
     * <p>
     * Creates a new {@link Group} to represent a group of calls to be tracked across mocks.
     * </p>
     * <p>
     * The returned object can then be passed to {@link Expectation#inGroup(Group...) Expectation.inGroup()}
     * or {@link Check#inGroup(Group...)  Check.inGroup()} to associate expectations/checks
     * with the group.
     * </p>
     * <p>
     * See the discussion in the summary javadoc for the {@link Group}
     * interface for more details.
     * </p>
     *
     * @param options {@link MoxieOptions} which should apply to the group (currently either {@link MoxieOptions#ORDERED} or {@link MoxieOptions#UNORDERED})
     * @return a new {@link Group}
     */
    static public Group group(MoxieOptions... options) {
        return instance.group(null, options);
    }

    /**
     * <p>
     * Creates a new {@link Group} to represent a group of calls to be tracked across mocks.
     * </p>
     * <p>
     * The returned object can then be passed to {@link Expectation#inGroup(Group...) Expectation.inGroup()}
     * or {@link Check#inGroup(Group...)  Check.inGroup()} to associate expectations/checks
     * with the group.
     * </p>
     * <p>
     * See the discussion in the summary javadoc for the {@link Group}
     * interface for more details.
     * </p>
     *
     * @param name    the name of the group - will be used in error messages
     * @param options {@link MoxieOptions} which should apply to the group (currently either {@link MoxieOptions#ORDERED} or {@link MoxieOptions#UNORDERED})
     * @return a new {@link Group}
     */
    static public Group group(String name, MoxieOptions... options) {
        return instance.group(name, options);
    }

    /**
     * <p>
     * Domain-specific language method - starts a clause in which an expectation is set on a mock.
     * </p>
     * <p>
     * See the discussion in the summary javadoc for the {@link Expectation} interface for more details.
     * </p>
     *
     * @param mockObject the mock or spy object on which the expectation is to be set
     * @param <T>        type of the mock or spy object
     * @return an {@link Expectation} whose methods can be used to give details of what behavior to expect
     */
    static public <T> Expectation<T> expect(T mockObject) {
        return instance.expect(mockObject);
    }

    /**
     * <p>
     * Domain-specific language method - starts a clause in which an stub expectation is set on a mock.
     * </p>
     * <p>
     * Synonym for <code>expect(mockObject).anyTimes().atAnyTime()</code>.
     * </p>
     *
     * @param mockObject the mock or spy object on which the expectation is to be set
     * @param <T>        type of the mock or spy object
     * @return an {@link Expectation} whose methods can be used to give details of what behavior to expect
     */
    static public <T> Expectation<T> stub(T mockObject) {
        return instance.expect(mockObject).anyTimes().atAnyTime();
    }

    /**
     * <p>
     * Domain-specific language method - starts a clause in which a check is performed against a mock after use.
     * </p>
     * <p>
     * See the discussion in the summary javadoc for the {@link Check} interface for more details.
     * </p>
     *
     * @param mockObject the mock or spy object on which the check is to be performed
     * @param <T>        type of the mock or spy object
     * @return a {@link Check} whose methods can be used to give details of what should have occurred
     */
    static public <T> Check<T> check(T mockObject) {
        return instance.check(mockObject);
    }

    /**
     * <p>
     * Verify that no calls other than those matched by previous {@link #check checks} occurred on the given mock(s).
     * </p>
     * <p>
     * If no parameters are given, this check is performed against all active mock/spy objects.
     * </p>
     *
     * @param mockObjects one or more mock/spy objects to be checked
     */
    static public void checkNothingElseHappened(Object... mockObjects) {
        instance.checkNothingElseHappened(mockObjects);
    }

    /**
     * <p>
     * Verify that no calls other than those matched by previous {@link #expect expectations}
     * or {@link #check checks} occurred on the given mock(s).
     * </p>
     * <p>
     * If no parameters are given, this check is performed against all active mock/spy objects.
     * </p>
     *
     * @param mockObjects one or more mock/spy objects to be checked
     */
    static public void checkNothingElseUnexpectedHappened(Object... mockObjects) {
        instance.checkNothingElseUnexpectedHappened(mockObjects);
    }

    /**
     * <p>
     * Verify {@link #expect expectations} on, and then deactivate, the given mock/spy objects.
     * </p>
     * <p>
     * After verification, the given mocks/spies will cease to be active; attempts to call methods on them
     * or perform {@link #check checks} against them will raise an error.
     * </p>
     * <p>
     * If no parameters are given, verification is performed against all active mock/spy objects.
     * </p>
     *
     * @param mockObjects one or more mock/spy objects to be checked
     */
    static public void verify(Object... mockObjects) {
        instance.verify(mockObjects);
    }

    /**
     * <p>
     * Verify {@link #expect expectations} on the given mock/spy objects.
     * </p>
     * <p>
     * Unlike {@link #verify verify()}, the given objects remain active after this method returns.
     * </p>
     * <p>
     * If no parameters are given, verification is performed against all active mock/spy objects.
     * </p>
     *
     * @param mockObjects one or more mock/spy objects to be checked
     */
    static public void verifySoFar(Object... mockObjects) {
        instance.verifySoFar(mockObjects);
    }

    /**
     * <p>
     * Verify {@link #expect expectations} on the given mock/spy objects, then make them
     * forget about any expectations or invocations up to this point.
     * </p>
     * <p>
     * If no parameters are given, this is done for all active mock/spy objects.
     * </p>
     *
     * @param mockObjects one or more mock/spy objects to be verified and reset
     */
    static public void verifyAndReset(Object... mockObjects) {
        instance.verifyAndReset(mockObjects);
    }

    /**
     * <p>
     * Verify {@link #expect expectations} on the given mock/spy object, make it
     * forget about any expectations or invocations up to this point, and set new
     * {@link MoxieOptions options}.
     * </p>
     *
     * @param mockObject   a mock/spy object to be verified and reset
     * @param firstOption  an option that should henceforth apply to the given mock
     * @param otherOptions zero or more subsequent options that should also apply
     */
    static public void verifyAndReset(Object mockObject, MoxieOptions firstOption, MoxieOptions... otherOptions) {
        instance.reset(mockObject, firstOption, otherOptions);
    }

    /**
     * <p>
     * Makes the given mock/spy objects forget about any expectations or invocations up to this point.
     * </p>
     * <p>
     * Note that this method performs no verification as to whether {@link #expect expectations}
     * have been satisfied; for that, use {@link #verifyAndReset}.
     * </p>
     * <p>
     * If no parameters are given, all active mock/spy objects are reset.
     * </p>
     *
     * @param mockObjects one or more mock/spy objects to be reset
     */
    static public void reset(Object... mockObjects) {
        instance.reset(mockObjects);
    }

    /**
     * <p>
     * Make the given mock/spy object forget about any expectations or invocations up to this point,
     * and set new {@link MoxieOptions options}.
     * </p>
     * <p>
     * Note that this method performs no verification as to whether {@link #expect expectations}
     * have been satisfied; for that, use {@link #verifyAndReset}.
     * </p>
     *
     * @param mockObject   a mock/spy object to be reset
     * @param firstOption  an option that should henceforth apply to the given mock
     * @param otherOptions zero or more subsequent options that should also apply
     */
    static public void reset(Object mockObject, MoxieOptions firstOption, MoxieOptions... otherOptions) {
        instance.reset(mockObject, firstOption, otherOptions);
    }

    /**
     * <p>
     * Deactivate the given mock/spy objects without verifying expectations.
     * </p>
     * <p>
     * If no parameters are given, this is done for all active mock/spy objects.
     * </p>
     *
     * @param mockObjects one or more mock/spy objects to be deactivated
     */
    static public void deactivate(Object... mockObjects) {
        instance.deactivate(mockObjects);
    }

    /**
     * <p>
     * Raises an error if any mock/spy objects are still active.
     * </p>
     * <p>
     * Useful for troubleshooting possible interactions between tests, and possible test memory leaks
     * due to Moxie holding on to active mocks long past their time.
     * </p>
     */
    static public void checkNoActiveMocks() {
        instance.checkNoActiveMocks();
    }

    /**
     * On the given object(s), automatically populate fields according to the following rules:
     * <ul>
     * <li>For all fields having the {@link Mock} annotation, populate that field with a new mock object.</li>
     * <li>For all fields having the {@link Spy} annotation, create a spy object that wraps the object in that field,
     * and overwrite the field's current value with that spy object.</li>
     * <li>For all fields of type {@link Group}, populate that field with a new {@link Group}, optionally using
     * the options in any {@link GroupOptions} annotation that may be found on that field.</li>
     * <li>For all fields having the {@link AutoMock} annotation, also apply these rules to fields on that object.</li>
     * </ul> 
     *
     * @param testComponents one or more objects to be auto-mocked - usually just one test instance
     */
    static public void autoMock(Object... testComponents) {
        instance.autoMock(testComponents);
    }

    /**
     * Restores fields on the given objects to their values before the objects were {@link #autoMock auto-mocked}.
     *
     * @param testComponents one or more objects to be auto-unmocked - usually just one test instance
     */
    static public void autoUnMock(Object... testComponents) {
        instance.autoUnMock(testComponents);
    }

    /**
     * <p>
     * Registers a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} with Moxie's magical parameter-matching mechanism.
     * </p>
     * <p>
     * See the discussion in the summary javadoc for the {@link MoxieMatchers} class for more details.
     * </p>
     *
     * @param matcher      a Hamcrest {@link Matcher} to be registered
     * @param expectedType optional - type of the expected parameter (particularly important if matching a primitive parameter)
     * @param <T>          type of the expected parameter
     * @return <code>null</code> if <code>expectedType</code> is an object, or the primitive's default value if <code>expectedType</code> is a primitive
     */
    static public <T> T reportMatcher(Matcher matcher, Class<T> expectedType) {
        instance.reportMatcher(matcher, expectedType);
        return MoxieUtils.defaultValue(expectedType);
    }

    static LinkedList<MatcherReport> getMatcherReports() {
        return instance.getMatcherReports();
    }
}
