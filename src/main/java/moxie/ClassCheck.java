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

public interface ClassCheck<T extends ClassCheck<T>> extends Check<T> {

    /**
     * <p>
     * Specify that this check is to be performed on a static method.
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
     * Specify that this check is to be performed on a static method.
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
     * Specify that this check is to be performed on a static method.
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
     * Specify that this check is to be performed on a static method.
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
     * Specify that this check is to be performed on a static method.
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
     * Specify that this check is to be performed on a static method.
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
     * Specify that this check is to be performed on a static method.
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
     * Specify that this check is to be performed on a static method.
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

    /**
     * <p>
     * Specify that this check is to be performed on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Object...) on()}, {@link #whenNew(Object...) when()},
     * {@link #getNew(Object...) get()} and {@link #gotNew(Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param params      Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void onNew(Object... params);

    /**
     * <p>
     * Specify that this check is to be performed on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Object...) on()}, {@link #whenNew(Object...) when()},
     * {@link #getNew(Object...) get()} and {@link #gotNew(Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param params      Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void whenNew(Object... params);

    /**
     * <p>
     * Specify that this check is to be performed on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Object...) on()}, {@link #whenNew(Object...) when()},
     * {@link #getNew(Object...) get()} and {@link #gotNew(Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param params      Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void getNew(Object... params);

    /**
     * <p>
     * Specify that this check is to be performed on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Object...) on()}, {@link #whenNew(Object...) when()},
     * {@link #getNew(Object...) get()} and {@link #gotNew(Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param params      Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void gotNew(Object... params);

    /**
     * <p>
     * Specify that this check is to be performed on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Class[], Object...) on()}, {@link #whenNew(Class[], Object...) when()},
     * {@link #getNew(Class[], Object...) get()} and {@link #gotNew(Class[], Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void onNew(Class[] paramSignature, Object... params);

    /**
     * <p>
     * Specify that this check is to be performed on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Class[], Object...) on()}, {@link #whenNew(Class[], Object...) when()},
     * {@link #getNew(Class[], Object...) get()} and {@link #gotNew(Class[], Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void whenNew(Class[] paramSignature, Object... params);

    /**
     * <p>
     * Specify that this check is to be performed on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Class[], Object...) on()}, {@link #whenNew(Class[], Object...) when()},
     * {@link #getNew(Class[], Object...) get()} and {@link #gotNew(Class[], Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void getNew(Class[] paramSignature, Object... params);

    /**
     * <p>
     * Specify that this check is to be performed on a constructor.
     * </p>
     * <p>
     * Note that {@link #onNew(Class[], Object...) on()}, {@link #whenNew(Class[], Object...) when()},
     * {@link #getNew(Class[], Object...) get()} and {@link #gotNew(Class[], Object...) got()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param paramSignature  Array of {@link Class} objects forming the method's parameter signature
     * @param params          Values or {@link MoxieMatchers} invocations matching the parameters to be verified
     */
    void gotNew(Class[] paramSignature, Object... params);

}
