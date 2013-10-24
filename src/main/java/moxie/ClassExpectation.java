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
 * <p>
 * Interface containing domain-specific language methods called in "the middle bit" of statements that
 * set expectations on static methods and constructors.
 * </p>
 * <p>
 * See documentation on the parent {@link Expectation} interface for a high-level introduction to the syntax.
 * </p>
 * <p>
 * <h2>Requires PowerMock</h2>
 * </p>
 * <p>
 * Setting expectations on static methods or constructors requires that
 * <a href="http://www.powermock.org/">PowerMock</a> be on your classpath.  Additionally, in JUnit you'll need
 * to run your tests using <code>PowerMockRunner</code> and tell PowerMock to pre-instrument any classes
 * on which you'll be testing statics/constructors using the <code>PrepareForTest</code> annotation.
 * Typically you'd do this by putting annotations like these at the top of your test class:
 * <blockquote><code>
 * &#64;RunWith(PowerMockRunner.class)<br />
 * &#64;PrepareForTest(&#123; ClassWithStaticMethodToBeMocked.class &#125;)
 * </code></blockquote>
 * </p>
 *
 * @param <T> Class on which expectations are being set.
 */
public interface ClassExpectation<T> extends Expectation<ClassExpectation<T>, Object>, Cardinality<ClassExpectation<T>> {

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
     */
    void onNew(Object... params);

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
     */
    void whenNew(Object... params);

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
     */
    void willNew(Object... params);

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
     */
    void onNew(Class[] paramSignature, Object... params);

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
     */
    void whenNew(Class[] paramSignature, Object... params);

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
     */
    void willNew(Class[] paramSignature, Object... params);

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
     */
    void on(String methodName, Object... params);

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
     */
    void when(String methodName, Object... params);

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
     */
    void will(String methodName, Object... params);

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
     */
    void on(String methodName, Class[] paramSignature, Object... params);

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
     */
    void when(String methodName, Class[] paramSignature, Object... params);

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
     */
    void will(String methodName, Class[] paramSignature, Object... params);
    
}
