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
package moxie;

/**
 * @param <T> Class on which expectations are being set.
 */
public interface ClassExpectation<T> extends Expectation<ClassExpectation<T>>, Cardinality<ClassExpectation<T>> {

    /**
     * <p>
     * Specify that this expectation is to be set on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Object...) onNew()}, {@link #whenNew(Object...) whenNew()} and {@link #willNew(Object...) willNew()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     * 
     * @param params      Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type being constructed (for optional deep mocking)
     */
    T onNew(Object... params);

    /**
     * <p>
     * Specify that this expectation is to be set on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Object...) onNew()}, {@link #whenNew(Object...) whenNew()} and {@link #willNew(Object...) willNew()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param params      Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type being constructed (for optional deep mocking)
     */
    T whenNew(Object... params);

    /**
     * <p>
     * Specify that this expectation is to be set on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Object...) onNew()}, {@link #whenNew(Object...) whenNew()} and {@link #willNew(Object...) willNew()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param params      Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type being constructed (for optional deep mocking)
     */
    T willNew(Object... params);

    /**
     * <p>
     * Specify that this expectation is to be set on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Class[], Object...) onNew()}, {@link #whenNew(Class[], Object...) whenNew()} and {@link #willNew(Class[], Object...) willNew()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type being constructed (for optional deep mocking)
     */
    T onNew(Class[] paramSignature, Object... params);

    /**
     * <p>
     * Specify that this expectation is to be set on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Class[], Object...) onNew()}, {@link #whenNew(Class[], Object...) whenNew()} and {@link #willNew(Class[], Object...) willNew()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type being constructed (for optional deep mocking)
     */
    T whenNew(Class[] paramSignature, Object... params);

    /**
     * <p>
     * Specify that this expectation is to be set on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Class[], Object...) onNew()}, {@link #whenNew(Class[], Object...) whenNew()} and {@link #willNew(Class[], Object...) willNew()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type being constructed (for optional deep mocking)
     */
    T willNew(Class[] paramSignature, Object... params);

    /**
     * <p>
     * Specify that this expectation is to be set on a static method.
     * </p>
     * <p>
     * Note that {@link #on(String, Object...) on()}, {@link #when(String, Object...) when()} and {@link #will(String, Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName  The name of the method on which to set the expectation
     * @param params      Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type returned by the method (for optional deep mocking), or <code>null</code> if a void method
     */
    Object on(String methodName, Object... params);

    /**
     * <p>
     * Specify that this expectation is to be set on a static method.
     * </p>
     * <p>
     * Note that {@link #on(String, Object...) on()}, {@link #when(String, Object...) when()} and {@link #will(String, Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName  The name of the method on which to set the expectation
     * @param params      Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type returned by the method (for optional deep mocking), or <code>null</code> if a void method
     */
    Object when(String methodName, Object... params);

    /**
     * <p>
     * Specify that this expectation is to be set on a static method.
     * </p>
     * <p>
     * Note that {@link #on(String, Object...) on()}, {@link #when(String, Object...) when()} and {@link #will(String, Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName  The name of the method on which to set the expectation
     * @param params      Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type returned by the method (for optional deep mocking), or <code>null</code> if a void method
     */
    Object will(String methodName, Object... params);

    /**
     * <p>
     * Specify that this expectation is to be set on a static method.
     * </p>
     * <p>
     * Note that {@link #on(String, Class[], Object...) on()}, {@link #when(String, Class[], Object...) when()} and {@link #will(String, Class[], Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName      The name of the method on which to set the expectation
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type returned by the method (for optional deep mocking), or <code>null</code> if a void method
     */
    Object on(String methodName, Class[] paramSignature, Object... params);

    /**
     * <p>
     * Specify that this expectation is to be set on a static method.
     * </p>
     * <p>
     * Note that {@link #on(String, Class[], Object...) on()}, {@link #when(String, Class[], Object...) when()} and {@link #will(String, Class[], Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName      The name of the method on which to set the expectation
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type returned by the method (for optional deep mocking), or <code>null</code> if a void method
     */
    Object when(String methodName, Class[] paramSignature, Object... params);

    /**
     * <p>
     * Specify that this expectation is to be set on a static method.
     * </p>
     * <p>
     * Note that {@link #on(String, Class[], Object...) on()}, {@link #when(String, Class[], Object...) when()} and {@link #will(String, Class[], Object...) will()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName      The name of the method on which to set the expectation
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the expected parameters
     * @return a stub of the type returned by the method (for optional deep mocking), or <code>null</code> if a void method
     */
    Object will(String methodName, Class[] paramSignature, Object... params);
    
}
