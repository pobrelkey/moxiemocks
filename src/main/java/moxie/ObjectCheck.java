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

public interface ObjectCheck<T> extends Check<ObjectCheck<T>>, Cardinality<ObjectCheck<T>> {

    /**
     * <p>
     * Check that the relevant method threw the given {@link Throwable}.
     * </p>
     * <p>
     * Note that you can use the {@link MoxieMatchers matcher methods} to match on the exception object.
     * </p>
     * <p>
     * Note also that that {@link #throwException(Throwable) throwException()} and {@link #threw(Throwable) threw()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param throwable the {@link Throwable} thrown, or a matcher invocation
     * @return this object, for call chaining
     */
    ObjectCheck<T> throwException(Throwable throwable);

    /**
     * <p>
     * Check that the relevant method threw the given {@link Throwable}.
     * </p>
     * <p>
     * Note that you can use the {@link MoxieMatchers matcher methods} to match on the exception object.
     * </p>
     * <p>
     * Note also that that {@link #throwException(Throwable) throwException()} and {@link #threw(Throwable) threw()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param throwable the {@link Throwable} thrown, or a matcher invocation
     * @return this object, for call chaining
     */
    ObjectCheck<T> threw(Throwable throwable);

    /**
     * <p>
     * Check that the relevant method returned the given value.
     * </p>
     * <p>
     * Note that you can use the {@link MoxieMatchers matcher methods} to match on the return value.
     * </p>
     * <p>
     * Note also that that {@link #returnValue(Object) returnValue()} and {@link #returned(Object) returned()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param returnValue the value returned, or a matcher invocation
     * @return this object, for call chaining
     */
    ObjectCheck<T> returnValue(Object returnValue);

    /**
     * <p>
     * Check that the relevant method returned the given value.
     * </p>
     * <p>
     * Note that you can use the {@link MoxieMatchers matcher methods} to match on the return value.
     * </p>
     * <p>
     * Note also that that {@link #returnValue(Object) returnValue()} and {@link #returned(Object) returned()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param returnValue the value returned, or a matcher invocation
     * @return this object, for call chaining
     */
    ObjectCheck<T> returned(Object returnValue);

    /**
     * <p>
     * The method that this check should match follows this call.
     * </p>
     * <p>
     * Note that {@link #on()}, {@link #when()}, {@link #get()} and {@link #got()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @return a magic proxy object, on which you should call the method to be verified
     */
    T on();

    /**
     * <p>
     * The method that this check should match follows this call.
     * </p>
     * <p>
     * Note that {@link #on()}, {@link #when()}, {@link #get()} and {@link #got()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @return a magic proxy object, on which you should call the method to be verified
     */
    T when();

    /**
     * <p>
     * The method that this check should match follows this call.
     * </p>
     * <p>
     * Note that {@link #on()}, {@link #when()}, {@link #get()} and {@link #got()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @return a magic proxy object, on which you should call the method to be verified
     */
    T get();

    /**
     * <p>
     * The method that this check should match follows this call.
     * </p>
     * <p>
     * Note that {@link #on()}, {@link #when()}, {@link #get()} and {@link #got()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @return a magic proxy object, on which you should call the method to be verified
     */
    T got();

    /**
     * <p>
     * Reflectively specify the method on which this check is to be performed.
     * </p>
     * <p>
     * Use this method to check invocations of protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Object...) on()}, {@link #when(String, Object...) when()},
     * {@link #get(String, Object...) get()} and {@link #got(String, Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName  The name of the method to be checked
     * @param params      Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void on(String methodName, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this check is to be performed.
     * </p>
     * <p>
     * Use this method to check invocations of protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Object...) on()}, {@link #when(String, Object...) when()},
     * {@link #get(String, Object...) get()} and {@link #got(String, Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName  The name of the method to be checked
     * @param params      Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void when(String methodName, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this check is to be performed.
     * </p>
     * <p>
     * Use this method to check invocations of protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Object...) on()}, {@link #when(String, Object...) when()},
     * {@link #get(String, Object...) get()} and {@link #got(String, Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName  The name of the method to be checked
     * @param params      Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void get(String methodName, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this check is to be performed.
     * </p>
     * <p>
     * Use this method to check invocations of protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Object...) on()}, {@link #when(String, Object...) when()},
     * {@link #get(String, Object...) get()} and {@link #got(String, Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName  The name of the method to be checked
     * @param params      Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void got(String methodName, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this check is to be performed.
     * </p>
     * <p>
     * Use this method to check invocations of protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Class[], Object...) on()}, {@link #when(String, Class[], Object...) when()},
     * {@link #get(String, Class[], Object...) get()} and {@link #got(String, Class[], Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName      The name of the method to be checked
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void on(String methodName, Class[] paramSignature, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this check is to be performed.
     * </p>
     * <p>
     * Use this method to check invocations of protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Class[], Object...) on()}, {@link #when(String, Class[], Object...) when()},
     * {@link #get(String, Class[], Object...) get()} and {@link #got(String, Class[], Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName      The name of the method to be checked
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void when(String methodName, Class[] paramSignature, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this check is to be performed.
     * </p>
     * <p>
     * Use this method to check invocations of protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Class[], Object...) on()}, {@link #when(String, Class[], Object...) when()},
     * {@link #get(String, Class[], Object...) get()} and {@link #got(String, Class[], Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName      The name of the method to be checked
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void get(String methodName, Class[] paramSignature, Object... params);

    /**
     * <p>
     * Reflectively specify the method on which this check is to be performed.
     * </p>
     * <p>
     * Use this method to check invocations of protected or package-private methods which cannot be specified using the
     * no-parameters version of this method because they are inaccessible from your test class.
     * </p>
     * <p>
     * Note that {@link #on(String, Class[], Object...) on()}, {@link #when(String, Class[], Object...) when()},
     * {@link #get(String, Class[], Object...) get()} and {@link #got(String, Class[], Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param methodName      The name of the method to be checked
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void got(String methodName, Class[] paramSignature, Object... params);
}
