/*
 * Copyright (c) 2010-2012 Moxie contributors
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
 *
 * Static class of convenience methods for Moxie, a wicked good Java mocking library.
 * <p>
 *
 *
 * Methods on this class fall into one of the following categories:
 * <p>
 * <ul>
 * <li><b>{@link MoxieControl} creation</b> - Use {@link Moxie#newControl() newControl()} to create a new
 * {@link MoxieControl}, or {@link Moxie#threadLocalControl() threadLocalControl()} to access the same thread-local
 * instance of {@link MoxieControl} used by {@link MoxieRunner}.<p></li>
 * <li><b>Thread-local {@link MoxieControl} methods</b> - Most of the static methods on this class delegate to the
 * instance of {@link MoxieControl} returned by {@link Moxie#threadLocalControl() threadLocalControl()}.
 * Using the thread-local instance should suffice
 * for most people, and using the methods on this class will save the extra line of code involved in explicitly
 * creating a {@link MoxieControl}.<p></li>
 * <li><b>{@link MoxieMatchers} methods</b> - For convenience this class inherits from {@link MoxieMatchers}, enabling
 * you to use those methods with less typing/fewer <code>import</code> statements.<p></li>
 * </ul>
 *
 * @see moxie.MoxieControl
 * @see moxie.MoxieMatchers
 */
public abstract class Moxie extends MoxieMatchers {
    static MoxieControl instance = MoxieUtils.createThreadLocalProxy(MoxieControl.class, new MoxieUtils.Factory<MoxieControl>() {
        public MoxieControl create() {
            return new MoxieControlImpl();
        }
    });

    private Moxie() {
    }

    /**
     *
     * Returns a proxy to a thread-local instance of {@link MoxieControl}.
     * <p>
     *
     * This is the same {@link MoxieControl} instance used by {@link MoxieRunner}, but not {@link MoxieRule} -
     * see {@link MoxieRule#getControl() MoxieRule.getControl()}.
     * <p>
     *
     * @return a proxy to a thread-local instance of {@link MoxieControl}
     */
    static public MoxieControl threadLocalControl() {
        return instance;
    }

    /**
     *
     * Creates and returns a fresh instance of {@link MoxieControl}.
     * <p>
     * @return a newly created {@link MoxieControl}
     */
    static public MoxieControl newControl() {
        return new MoxieControlImpl();
    }


    ////////////////////////////////////////////////////////////////////

    /**
     * Creates a mock object of the given type.
     *
     * @param clazz the class or interface that the mock should extend/implement
     * @param <T>   the class or interface that the mock should extend/implement
     * @return a new mock object
     * @see MoxieControl#mock(Class)
     */
    static public <T> T mock(Class<T> clazz) {
        return instance.mock(clazz);
    }

    /**
     * Creates a mock object of the given type.
     *
     * @param clazz the class or interface that the mock should extend/implement
     * @param name  the name of the mock object - will be used in error messages
     * @param <T>   the class or interface that the mock should extend/implement
     * @return a new mock object
     * @see MoxieControl#mock(Class, String)
     */
    static public <T> T mock(Class<T> clazz, String name) {
        return instance.mock(clazz, name);
    }

    /**
     * Creates a mock object of the given type.
     *
     * @param clazz   the class or interface that the mock should extend/implement
     * @param options one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>     the class or interface that the mock should extend/implement
     * @return a new mock object
     * @see MoxieControl#mock(Class, MoxieOptions...)
     */
    static public <T> T mock(Class<T> clazz, MoxieOptions... options) {
        return instance.mock(clazz, options);
    }

    /**
     * Creates a mock object of the given type.
     *
     * @param clazz   the class or interface that the mock should extend/implement
     * @param name    the name of the mock object - will be used in error messages
     * @param options one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>     the class or interface that the mock should extend/implement
     * @return a new mock object
     * @see MoxieControl#mock(Class, String, MoxieOptions...)
     */
    static public <T> T mock(Class<T> clazz, String name, MoxieOptions... options) {
        return instance.mock(clazz, name, options);
    }

    /**
     *
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * <p>
     *
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * <p>
     *
     * @param clazz               the class that the mock should extend
     * @param constructorArgs     values to be passed to the constructor
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz, Object... constructorArgs) {
        return instance.mock(clazz, constructorArgs);
    }

    /**
     *
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * <p>
     *
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * <p>
     *
     * @param clazz               the class that the mock should extend
     * @param constructorArgTypes the parameter signature of the constructor to be used
     * @param constructorArgs     values to be passed to the constructor
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz, Class[] constructorArgTypes, Object[] constructorArgs) {
        return instance.mock(clazz, constructorArgTypes, constructorArgs);
    }

    /**
     *
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * <p>
     *
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * <p>
     *
     * @param clazz               the class that the mock should extend
     * @param name                the name of the mock object - will be used in error messages
     * @param constructorArgs     values to be passed to the constructor
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz, String name, Object[] constructorArgs) {
        return instance.mock(clazz, name, constructorArgs);
    }

    /**
     *
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * <p>
     *
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * <p>
     *
     * @param clazz               the class that the mock should extend
     * @param name                the name of the mock object - will be used in error messages
     * @param constructorArgTypes the parameter signature of the constructor to be used
     * @param constructorArgs     values to be passed to the constructor
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz, String name, Class[] constructorArgTypes, Object[] constructorArgs) {
        return instance.mock(clazz, name, constructorArgTypes, constructorArgs);
    }

    /**
     *
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * <p>
     *
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * <p>
     *
     * @param clazz               the class that the mock should extend
     * @param constructorArgs     values to be passed to the constructor
     * @param options             one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz, Object[] constructorArgs, MoxieOptions... options) {
        return instance.mock(clazz, constructorArgs, options);
    }

    /**
     *
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * <p>
     *
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * <p>
     *
     * @param clazz               the class that the mock should extend
     * @param constructorArgTypes the parameter signature of the constructor to be used
     * @param constructorArgs     values to be passed to the constructor
     * @param options             one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz, Class[] constructorArgTypes, Object[] constructorArgs, MoxieOptions... options) {
        return instance.mock(clazz, constructorArgTypes, constructorArgs, options);
    }

    /**
     *
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * <p>
     *
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * <p>
     *
     * @param clazz               the class that the mock should extend
     * @param name                the name of the mock object - will be used in error messages
     * @param constructorArgs     values to be passed to the constructor
     * @param options             one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz, String name, Object[] constructorArgs, MoxieOptions... options) {
        return instance.mock(clazz, name, constructorArgs, options);
    }

    /**
     *
     * Creates a mock object of the given type, calling a constructor with the given arguments to instantiate the mock.
     * This method is provided for partial mocking of objects that use constructor dependency injection,
     * and to ease portability of legacy JMock 1/EasyMock tests.
     * <p>
     *
     * You don't need to call this method if you are mocking a concrete class without a no-arg constructor.
     * By default Moxie uses the <a href="http://www.objenesis.org/">Objenesis</a> library to instantiate mocks -
     * this uses JVM black magic to create objects without calling their constructors.  Moxie will revert
     * to using constructors if Objenesis isn't on the classpath.
     * <p>
     *
     * @param clazz               the class that the mock should extend
     * @param name                the name of the mock object - will be used in error messages
     * @param constructorArgTypes the parameter signature of the constructor to be used
     * @param constructorArgs     values to be passed to the constructor
     * @param options             one or more {@link MoxieOptions} that should apply to the mock
     * @param <T>                 the class or interface that the mock should extend/implement
     * @return a new mock object
     */
    static public <T> T mock(Class<T> clazz, String name, Class[] constructorArgTypes, Object[] constructorArgs, MoxieOptions... options) {
        return instance.mock(clazz, name, constructorArgTypes, constructorArgs, options);
    }


    /**
     * Creates a spy object, i.e. a proxy which wraps an actual object on which expectations can be set
     * and checks can be performed.
     *
     * @param realObject the object the spy should wrap
     * @param options    one or more {@link MoxieOptions} that should apply to the spy
     * @param <T>        type of the object to be spied upon
     * @return a new spy object
     * @see MoxieControl#spy(Object, MoxieOptions...)
     */
    static public <T> T spy(T realObject, MoxieOptions... options) {
        return instance.spy(realObject, options);
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
     * @see MoxieControl#spy(Object, String, MoxieOptions...)
     */
    static public <T> T spy(T realObject, String name, MoxieOptions... options) {
        return instance.spy(realObject, name, options);
    }

    /**
     *
     * Creates a new {@link Group} to represent a group of calls to be tracked across mocks.
     * <p>
     *
     * The returned object can then be passed to {@link Expectation#inGroup(Group...) Expectation.inGroup()}
     * or {@link Check#inGroup(Group...)  Check.inGroup()} to associate expectations/checks
     * with the group.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Group}
     * interface for more details.
     * <p>
     *
     * @param options {@link MoxieOptions} which should apply to the group (currently either {@link MoxieOptions#ORDERED} or {@link MoxieOptions#UNORDERED})
     * @return a new {@link Group}
     * @see MoxieControl#group(MoxieOptions...)
     */
    static public Group group(MoxieOptions... options) {
        return instance.group(options);
    }

    /**
     *
     * Creates a new {@link Group} to represent a group of calls to be tracked across mocks.
     * <p>
     *
     * The returned object can then be passed to {@link Expectation#inGroup(Group...) Expectation.inGroup()}
     * or {@link Check#inGroup(Group...)  Check.inGroup()} to associate expectations/checks
     * with the group.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Group}
     * interface for more details.
     * <p>
     *
     * @param name    the name of the group - will be used in error messages
     * @param options {@link MoxieOptions} which should apply to the group (currently either {@link MoxieOptions#ORDERED} or {@link MoxieOptions#UNORDERED})
     * @return a new {@link Group}
     * @see MoxieControl#group(String, MoxieOptions...)
     */
    static public Group group(String name, MoxieOptions... options) {
        return instance.group(name, options);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which an expectation is set on a mock.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Expectation} interface for more details.
     * <p>
     *
     * @param mockObject the mock or spy object on which the expectation is to be set
     * @param <T>        type of the mock or spy object
     * @return an {@link ObjectExpectation} whose methods can be used to give details of what behavior to expect
     * @see MoxieControl#expect(Object)
     */
    static public <T> ObjectExpectation<T> expect(T mockObject) {
        return instance.expect(mockObject);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which an expectation is set on static or constructor methods of a class.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Expectation} interface for more details.
     * <p>
     *
     * @param clazz the class on which the expectation is to be set
     * @param <T> the class on which the expectation is to be set
     * @return a {@link ClassExpectation} whose methods can be used to give details of what behavior to expect
     * @see MoxieControl#expect(Class)
     */
    static public <T> ClassExpectation<T> expect(Class<T> clazz) {
        return instance.expect(clazz);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which an expectation is set using Java 8 lambda syntax.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Expectation} interface for more details.
     * <p>
     *
     * @return a {@link LambdaExpectation} whose methods can be used to give details of what behavior to expect
     * @see MoxieControl#expect()
     */
    static public LambdaExpectation<Object> expect() {
        return instance.expect();
    }

    /**
     *
     * Domain-specific language method - starts a clause in which an expectation is set using Java 8 lambda syntax.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Expectation} interface for more details.
     * <p>
     *
     * This method is a synonym for <code>expect().thatVoid(lambda)</code>.
     * <p>
     *
     * @param lambda a lambda expression invoking the method to be mocked, using {@link MoxieMatchers parameter matchers} as necessary
     * @return a {@link LambdaExpectation} whose methods can be used to give details of what behavior to expect
     * @see MoxieControl#expectVoid(ThrowingRunnable)
     */
    static public LambdaExpectation<Void> expectVoid(ThrowingRunnable lambda) {
        return instance.expectVoid(lambda);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which an expectation is set using Java 8 lambda syntax.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Expectation} interface for more details.
     * <p>
     *
     * This method is a synonym for <code>expect().that(lambda)</code>.
     * <p>
     *
     * @param lambda a lambda expression invoking the method to be mocked, using {@link MoxieMatchers parameter matchers} as necessary
     * @param <R> return type of the method/constructor to be mocked
     * @return a {@link LambdaExpectation} whose methods can be used to give details of what behavior to expect
     * @see MoxieControl#expect(ThrowingSupplier)
     */
    static public <R> LambdaExpectation<R> expect(ThrowingSupplier<R> lambda) {
        return instance.expect(lambda);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which an stub expectation is set on a mock.
     * <p>
     *
     * Synonym for <code>expect(mockObject).anyTimes().atAnyTime()</code>.
     * <p>
     *
     * @param mockObject the mock or spy object on which the expectation is to be set
     * @param <T>        type of the mock or spy object
     * @return an {@link ObjectExpectation} whose methods can be used to give details of what behavior to expect
     * @see MoxieControl#stub(Object)
     */
    static public <T> ObjectExpectation<T> stub(T mockObject) {
        return instance.stub(mockObject);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which an expectation is set on static or constructor methods of a class.
     * <p>
     *
     * Synonym for <code>expect(clazz).anyTimes().atAnyTime()</code>.
     * <p>
     *
     * @param clazz the class on which the expectation is to be set
     * @return a {@link ClassExpectation} whose methods can be used to give details of what behavior to expect
     * @see MoxieControl#stub(Class)
     */
    static public <T> ClassExpectation<T> stub(Class<T> clazz) {
        return instance.stub(clazz);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which a stub expectation is set using Java 8 lambda syntax.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Expectation} interface for more details.
     * <p>
     *
     * This method is a synonym for <code>expect().anyTimes().atAnyTime()</code>.
     * <p>
     *
     * @return a {@link LambdaExpectation} whose methods can be used to give details of what behavior to perform
     * @see MoxieControl#stub()
     */
    static public LambdaExpectation<Object> stub() {
        return instance.stub();
    }

    /**
     *
     * Domain-specific language method - starts a clause in which a stub expectation is set using Java 8 lambda syntax.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Expectation} interface for more details.
     * <p>
     *
     * This method is a synonym for <code>expect().anyTimes().atAnyTime().thatVoid(lambda)</code>.
     * <p>
     *
     * @param lambda a lambda expression invoking the method to be stubbed, using {@link MoxieMatchers parameter matchers} as necessary
     * @return a {@link LambdaExpectation} whose methods can be used to give details of what behavior to perform
     * @see MoxieControl#stubVoid(ThrowingRunnable)
     */
    static public LambdaExpectation<Void> stubVoid(ThrowingRunnable lambda) {
        return instance.stubVoid(lambda);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which a stub expectation is set using Java 8 lambda syntax.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Expectation} interface for more details.
     * <p>
     *
     * This method is a synonym for <code>expect().anyTimes().atAnyTime().that(lambda)</code>.
     * <p>
     *
     * @param lambda a lambda expression invoking the method to be stubbed, using {@link MoxieMatchers parameter matchers} as necessary
     * @param <R> return type of the method/constructor to be stubbed
     * @return a {@link LambdaExpectation} whose methods can be used to give details of what behavior to perform
     * @see MoxieControl#stub(ThrowingSupplier)
     */
    static public <R> LambdaExpectation<R> stub(ThrowingSupplier<R> lambda) {
        return instance.stub(lambda);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which a check is performed against a mock after use.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Check} interface for more details.
     * <p>
     *
     * @param mockObject the mock or spy object on which the check is to be performed
     * @param <T>        type of the mock or spy object
     * @return an {@link ObjectCheck} whose methods can be used to give details of what should have occurred
     * @see MoxieControl#check(Object)
     */
    static public <T> ObjectCheck<T> check(T mockObject) {
        return instance.check(mockObject);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which a check is performed on a static method or constructor of a class after use.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Check} interface for more details.
     * <p>
     *
     * @param clazz the class on which the check is to be performed
     * @return a {@link ClassCheck} whose methods can be used to give details of what should have occurred
     * @see MoxieControl#check(Class)
     */
    static public ClassCheck check(Class clazz) {
        return instance.check(clazz);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which a check is performed, as specified using Java 8 lambda syntax.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Check} interface for more details.
     * <p>
     *
     * @return a {@link LambdaCheck} whose methods can be used to give details of what should have occurred
     * @see MoxieControl#check()
     */
    static public LambdaCheck<Object> check() {
        return instance.check();
    }

    /**
     *
     * Domain-specific language method - starts a clause in which a check is performed, as specified using Java 8 lambda syntax.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Check} interface for more details.
     * <p>
     *
     * This method is a synonym for <code>check().thatVoid(lambda)</code>.
     * <p>
     *
     * @param lambda a lambda expression invoking the method to be checked, using {@link MoxieMatchers parameter matchers} as necessary
     * @return a {@link LambdaCheck} whose methods can be used to give details of what should have occurred
     * @see MoxieControl#checkVoid(ThrowingRunnable)
     */
    static public LambdaCheck<Void> checkVoid(ThrowingRunnable lambda) {
        return instance.checkVoid(lambda);
    }

    /**
     *
     * Domain-specific language method - starts a clause in which a check is performed, as specified using Java 8 lambda syntax.
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Check} interface for more details.
     * <p>
     *
     * This method is a synonym for <code>check().that(lambda)</code>.
     * <p>
     *
     * @param lambda a lambda expression invoking the method to be checked, using {@link MoxieMatchers parameter matchers} as necessary
     * @param <R> return type of the method/constructor to be checked
     * @return a {@link LambdaCheck} whose methods can be used to give details of what should have occurred
     * @see MoxieControl#check(ThrowingSupplier)
     */
    static public <R> LambdaCheck<R> check(ThrowingSupplier<R> lambda) {
        return instance.check(lambda);
    }

    /**
     *
     * Verify that no calls other than those matched by previous {@link #check checks} occurred on the given mock(s).
     * <p>
     *
     * If no parameters are given, this check is performed against all active mock/spy objects.
     * <p>
     *
     * @param mockObjects one or more mock/spy objects to be checked
     * @see MoxieControl#checkNothingElseHappened(Object...)
     */
    static public void checkNothingElseHappened(Object... mockObjects) {
        instance.checkNothingElseHappened(mockObjects);
    }

    /**
     *
     * Verify that no calls other than those matched by previous {@link #expect expectations}
     * or {@link #check checks} occurred on the given mock(s).
     * <p>
     *
     * If no parameters are given, this check is performed against all active mock/spy objects.
     * <p>
     *
     * @param mockObjects one or more mock/spy objects to be checked
     * @see MoxieControl#checkNothingElseUnexpectedHappened(Object...)
     */
    static public void checkNothingElseUnexpectedHappened(Object... mockObjects) {
        instance.checkNothingElseUnexpectedHappened(mockObjects);
    }

    /**
     *
     * Verify {@link #expect expectations} on, and then deactivate, the given mock/spy objects.
     * <p>
     *
     * After verification, the given mocks/spies will cease to be active; attempts to call methods on them
     * or perform {@link #check checks} against them will raise an error.
     * <p>
     *
     * If no parameters are given, verification is performed against all active mock/spy objects.
     * <p>
     *
     * @param mockObjects one or more mock/spy objects to be checked
     * @see MoxieControl#verify(Object...)
     */
    static public void verify(Object... mockObjects) {
        instance.verify(mockObjects);
    }

    /**
     *
     * Verify {@link #expect expectations} on the given mock/spy objects.
     * <p>
     *
     * Unlike {@link #verify verify()}, the given objects remain active after this method returns.
     * <p>
     *
     * If no parameters are given, verification is performed against all active mock/spy objects.
     * <p>
     *
     * @param mockObjects one or more mock/spy objects to be checked
     * @see MoxieControl#verifySoFar(Object...)
     */
    static public void verifySoFar(Object... mockObjects) {
        instance.verifySoFar(mockObjects);
    }

    /**
     *
     * Verify {@link #expect expectations} on the given mock/spy objects, then make them
     * forget about any expectations or invocations up to this point.
     * <p>
     *
     * If no parameters are given, this is done for all active mock/spy objects.
     * <p>
     *
     * @param mockObjects one or more mock/spy objects to be verified and reset
     * @see MoxieControl#verifyAndReset(Object...)
     */
    static public void verifyAndReset(Object... mockObjects) {
        instance.verifyAndReset(mockObjects);
    }

    /**
     *
     * Verify {@link #expect expectations} on the given mock/spy object, make it
     * forget about any expectations or invocations up to this point, and set new
     * {@link MoxieOptions options}.
     * <p>
     *
     * @param mockObject   a mock/spy object to be verified and reset
     * @param firstOption  an option that should henceforth apply to the given mock
     * @param otherOptions zero or more subsequent options that should also apply
     * @see MoxieControl#verifyAndReset(Object, MoxieOptions, MoxieOptions...)
     */
    static public void verifyAndReset(Object mockObject, MoxieOptions firstOption, MoxieOptions... otherOptions) {
        instance.verifyAndReset(mockObject, firstOption, otherOptions);
    }

    /**
     *
     * Makes the given mock/spy objects forget about any expectations or invocations up to this point.
     * <p>
     *
     * Note that this method performs no verification as to whether {@link #expect expectations}
     * have been satisfied; for that, use {@link #verifyAndReset}.
     * <p>
     *
     * If no parameters are given, all active mock/spy objects are reset.
     * <p>
     *
     * @param mockObjects one or more mock/spy objects to be reset
     * @see MoxieControl#reset(Object...)
     */
    static public void reset(Object... mockObjects) {
        instance.reset(mockObjects);
    }

    /**
     *
     * Make the given mock/spy object forget about any expectations or invocations up to this point,
     * and set new {@link MoxieOptions options}.
     * <p>
     *
     * Note that this method performs no verification as to whether {@link #expect expectations}
     * have been satisfied; for that, use {@link #verifyAndReset}.
     * <p>
     *
     * @param mockObject   a mock/spy object to be reset
     * @param firstOption  an option that should henceforth apply to the given mock
     * @param otherOptions zero or more subsequent options that should also apply
     * @see MoxieControl#reset(Object, MoxieOptions, MoxieOptions...)
     */
    static public void reset(Object mockObject, MoxieOptions firstOption, MoxieOptions... otherOptions) {
        instance.reset(mockObject, firstOption, otherOptions);
    }

    /**
     *
     * Deactivate the given mock/spy objects without verifying expectations.
     * <p>
     *
     * If no parameters are given, this is done for all active mock/spy objects.
     * <p>
     *
     * @param mockObjects one or more mock/spy objects to be deactivated
     * @see MoxieControl#deactivate(Object...)
     */
    static public void deactivate(Object... mockObjects) {
        instance.deactivate(mockObjects);
    }

    /**
     *
     * Raises an error if any mock/spy objects are still active.
     * <p>
     *
     * Useful for troubleshooting possible interactions between tests, and possible test memory leaks
     * due to Moxie holding on to active mocks long past their time.
     * <p>
     *
     * @see moxie.MoxieControl#checkNoActiveMocks()
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
     * @return an array containing all mock/spy objects created by this operation
     * @see MoxieControl#autoMock(Object...)
     */
    static public Object[] autoMock(Object... testComponents) {
       return instance.autoMock(testComponents);
    }

    /**
     * Restores fields on the given objects to their values before the objects were {@link #autoMock auto-mocked}.
     *
     * @param testComponents one or more objects to be auto-unmocked - usually just one test instance
     * @see MoxieControl#autoUnMock(Object...)
     */
    static public void autoUnMock(Object... testComponents) {
        instance.autoUnMock(testComponents);
    }

}
