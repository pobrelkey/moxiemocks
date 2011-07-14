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

package moxie;

/**
 * Primary point of interaction between Moxie and test classes.
 */
public interface MoxieControl {
    /**
     * Creates a mock object of the given type.
     *
     * @param clazz the class or interface that the mock should extend/implement
     * @param <T>   the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    <T> T mock(Class<T> clazz);

    /**
     * Creates a mock object of the given type.
     *
     * @param clazz the class or interface that the mock should extend/implement
     * @param name  the name of the mock object - will be used in error messages
     * @param <T>   the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    <T> T mock(Class<T> clazz, String name);

    /**
     * Creates a mock object of the given type.
     *
     * @param clazz   the class or interface that the mock should extend/implement
     * @param options one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>     the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    <T> T mock(Class<T> clazz, MoxieOptions... options);

    /**
     * Creates a mock object of the given type.
     *
     * @param clazz   the class or interface that the mock should extend/implement
     * @param name    the name of the mock object - will be used in error messages
     * @param options one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>     the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    <T> T mock(Class<T> clazz, String name, MoxieOptions... options);

    /**
     * <p>
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * </p>
     * <p>
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * </p>
     *
     * @param clazz               the class that the mock should extend
     * @param constructorArgTypes the parameter signature of the constructor to be used
     * @param constructorArgs     values to be passed to the constructor
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    <T> T mock(Class<T> clazz, Class[] constructorArgTypes, Object[] constructorArgs);

    /**
     * <p>
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * </p>
     * <p>
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * </p>
     *
     * @param clazz               the class that the mock should extend
     * @param name                the name of the mock object - will be used in error messages
     * @param constructorArgTypes the parameter signature of the constructor to be used
     * @param constructorArgs     values to be passed to the constructor
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    <T> T mock(Class<T> clazz, String name, Class[] constructorArgTypes, Object[] constructorArgs);

    /**
     * <p>
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * </p>
     * <p>
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * </p>
     *
     * @param clazz               the class that the mock should extend
     * @param constructorArgTypes the parameter signature of the constructor to be used
     * @param constructorArgs     values to be passed to the constructor
     * @param options             one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    <T> T mock(Class<T> clazz, Class[] constructorArgTypes, Object[] constructorArgs, MoxieOptions... options);

    /**
     * <p>
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * </p>
     * <p>
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * </p>
     *
     * @param clazz               the class that the mock should extend
     * @param name                the name of the mock object - will be used in error messages
     * @param constructorArgTypes the parameter signature of the constructor to be used
     * @param constructorArgs     values to be passed to the constructor
     * @param options             one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    <T> T mock(Class<T> clazz, String name, Class[] constructorArgTypes, Object[] constructorArgs, MoxieOptions... options);

    /**
     * Creates a spy object, i.e. a proxy which wraps an actual object on which expectations can be set
     * and checks can be performed.
     *
     * @param realObject the object the spy should wrap
     * @param options    one or more {@link MoxieOptions} that should apply to the spy
     * @param <T>        type of the object to be spied upon
     * @return a new spy object
     */
    <T> T spy(T realObject, MoxieOptions... options);

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
    <T> T spy(T realObject, String name, MoxieOptions... options);

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
    Group group(MoxieOptions... options);

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
    Group group(String name, MoxieOptions... options);

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
    <T> Expectation<T> expect(T mockObject);

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
    <T> Expectation<T> stub(T mockObject);

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
    <T> Check<T> check(T mockObject);

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
    void checkNothingElseHappened(Object... mockObjects);

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
    void checkNothingElseUnexpectedHappened(Object... mockObjects);

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
    void verify(Object... mockObjects);

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
    void verifySoFar(Object... mockObjects);

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
    void verifyAndReset(Object... mockObjects);

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
    void verifyAndReset(Object mockObject, MoxieOptions firstOption, MoxieOptions... otherOptions);

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
    void reset(Object... mockObjects);

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
    void reset(Object mockObject, MoxieOptions firstOption, MoxieOptions... otherOptions);

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
    void deactivate(Object... mockObjects);

    /**
     * <p>
     * Raises an error if any mock/spy objects are still active.
     * </p>
     * <p>
     * Useful for troubleshooting possible interactions between tests, and possible test memory leaks
     * due to Moxie holding on to active mocks long past their time.
     * </p>
     *
     */
    void checkNoActiveMocks();

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
     * @return an array containing all mock/spy objects created by this operation
     */
    Object[] autoMock(Object... testComponents);

    /**
     * Restores fields on the given objects to their values before the objects were {@link #autoMock auto-mocked}.
     *
     * @param testComponents one or more objects to be auto-unmocked - usually just one test instance
     */
    void autoUnMock(Object... testComponents);

}