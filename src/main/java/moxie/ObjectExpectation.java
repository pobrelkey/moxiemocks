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
 * @param <T> Type of the mock object for which expectations are being set.
 */
public interface ObjectExpectation<T> extends Expectation<ObjectExpectation<T>>, Cardinality<ObjectExpectation<T>> {

    /**
     * <p>
     * On a spy object, when a call otherwise fulfilling this expectation is received, delegate to the object being spied upon,
     * then only fulfill this expectation if the value returned matches the given parameter.
     * </p>
     * <p>
     * Note that you can use the {@link MoxieMatchers matcher methods} to match on the return value.
     * </p>
     * <p>
     * This method only makes sense for spy objects or {@link MoxieOptions#PARTIAL PARTIAL} mocks; calling this method on an expectation being set on a mock object raises an error.
     * </p>
     * <p>
     * Note that {@link #willReturnVerified(Object) willReturnVerified()} and {@link #andVerifyReturn(Object) andVerifyReturn()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param result a value or matcher invocation to be compared with the value returned by the underlying method
     * @return this object, for call chaining
     */
    ObjectExpectation<T> willReturnVerified(Object result);

    /**
     * <p>
     * On a spy object, when a call otherwise fulfilling this expectation is received, delegate to the object being spied upon,
     * then only fulfill this expectation if the value returned matches the given parameter.
     * </p>
     * <p>
     * Note that you can use the {@link MoxieMatchers matcher methods} to match on the return value.
     * </p>
     * <p>
     * This method only makes sense for spy objects or {@link MoxieOptions#PARTIAL PARTIAL} mocks; calling this method on an expectation being set on a mock object raises an error.
     * </p>
     * <p>
     * Note that {@link #willReturnVerified(Object) willReturnVerified()} and {@link #andVerifyReturn(Object) andVerifyReturn()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param result a value or matcher invocation to be compared with the value returned by the underlying method
     * @return this object, for call chaining
     */
    ObjectExpectation<T> andVerifyReturn(Object result);

    /**
     * <p>
     * On a spy object, when a call otherwise fulfilling this expectation is received, delegate to the object being spied upon,
     * then only fulfill this expectation if an exception is thrown matching the given parameter.
     * </p>
     * <p>
     * Note that you can use the {@link MoxieMatchers matcher methods} to match on the exception object.
     * </p>
     * <p>
     * This method only makes sense for spy objects; calling this method on an expectation being set on a mock object raises an error.
     * </p>
     * <p>
     * Note that {@link #willThrowVerified(Throwable) willThrowVerified()} and {@link #andVerifyThrow(Throwable) andVerifyThrow()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param throwable a {@link Throwable} or matcher invocation to be compared with the exception thrown by the underlying method
     * @return this object, for call chaining
     */
    ObjectExpectation<T> willThrowVerified(Throwable throwable);

    /**
     * <p>
     * On a spy object, when a call otherwise fulfilling this expectation is received, delegate to the object being spied upon,
     * then only fulfill this expectation if an exception is thrown matching the given parameter.
     * </p>
     * <p>
     * Note that you can use the {@link MoxieMatchers matcher methods} to match on the exception object.
     * </p>
     * <p>
     * This method only makes sense for spy objects; calling this method on an expectation being set on a mock object raises an error.
     * </p>
     * <p>
     * Note that {@link #willThrowVerified(Throwable) willThrowVerified()} and {@link #andVerifyThrow(Throwable) andVerifyThrow()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param throwable a {@link Throwable} or matcher invocation to be compared with the exception thrown by the underlying method
     * @return this object, for call chaining
     */
    ObjectExpectation<T> andVerifyThrow(Throwable throwable);

    /**
     * <p>
     * When a call fulfilling this expectation is received, pass control to the original implementation
     * of the method on the concrete class being mocked.
     * </p>
     * <p>
     * This option only makes sense for mocks of concrete classes; specifying it for an interface mock
     * or a spy will raise an {@link IllegalStateException}.
     * </p>
     * <p>
     * Note that {@link #willCallOriginal()} and {@link #andCallOriginal()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @return this object, for call chaining
     */
    ObjectExpectation<T> willCallOriginal();

    /**
     * <p>
     * When a call fulfilling this expectation is received, pass control to the original implementation
     * of the method on the concrete class being mocked.
     * </p>
     * <p>
     * This option only makes sense for mocks of concrete classes; specifying it for an interface mock
     * or a spy will raise an {@link IllegalStateException}.
     * </p>
     * <p>
     * Note that {@link #willCallOriginal()} and {@link #andCallOriginal()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @return this object, for call chaining
     */
    ObjectExpectation<T> andCallOriginal();

    /**
     * <p>
     * The method that this expectation should match follows this call.
     * </p>
     * <p>
     * Note that {@link #on()}, {@link #when()} and {@link #will()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @return a magic proxy object, on which you should call the method to be expected
     */
    T on();

    /**
     * <p>
     * The method that this expectation should match follows this call.
     * </p>
     * <p>
     * Note that {@link #on()}, {@link #when()} and {@link #will()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @return a magic proxy object, on which you should call the method to be expected
     */
    T when();

    /**
     * <p>
     * The method that this expectation should match follows this call.
     * </p>
     * <p>
     * Note that {@link #on()}, {@link #when()} and {@link #will()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @return a magic proxy object, on which you should call the method to be expected
     */
    T will();

    /**
     * <p>
     * Reflectively specify the method on which this expectation is to be set.
     * </p>
     * <p>
     * Use this method to set expectations on protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Object...) on()}, {@link #when(String, Object...) when()} and {@link #will(String, Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName  The name of the method on which to set the expectation
     * @param params      Values or {@link MoxieMatchers} invocations matching the expected parameters
     */
    void on(String methodName, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this expectation is to be set.
     * </p>
     * <p>
     * Use this method to set expectations on protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Object...) on()}, {@link #when(String, Object...) when()} and {@link #will(String, Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName  The name of the method on which to set the expectation
     * @param params      Values or {@link MoxieMatchers} invocations matching the expected parameters
     */
    void when(String methodName, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this expectation is to be set.
     * </p>
     * <p>
     * Use this method to set expectations on protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Object...) on()}, {@link #when(String, Object...) when()} and {@link #will(String, Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName  The name of the method on which to set the expectation
     * @param params      Values or {@link MoxieMatchers} invocations matching the expected parameters
     */
    void will(String methodName, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this expectation is to be set.
     * </p>
     * <p>
     * Use this method to set expectations on protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Class[], Object...) on()}, {@link #when(String, Class[], Object...) when()} and {@link #will(String, Class[], Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName      The name of the method on which to set the expectation
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the expected parameters
     */
    void on(String methodName, Class[] paramSignature, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this expectation is to be set.
     * </p>
     * <p>
     * Use this method to set expectations on protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Class[], Object...) on()}, {@link #when(String, Class[], Object...) when()} and {@link #will(String, Class[], Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName      The name of the method on which to set the expectation
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the expected parameters
     */
    void when(String methodName, Class[] paramSignature, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this expectation is to be set.
     * </p>
     * <p>
     * Use this method to set expectations on protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Class[], Object...) on()}, {@link #when(String, Class[], Object...) when()} and {@link #will(String, Class[], Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName      The name of the method on which to set the expectation
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the expected parameters
     */
    void will(String methodName, Class[] paramSignature, Object... params);

}
