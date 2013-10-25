/*
 * Copyright (c) 2013 Moxie contributors
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
 * Interface containing domain-specific language methods called in "the middle bit" of statements that perform
 * post-invocation verifications using lambda syntax.
 * </p>
 * <p>
 * See documentation on the parent {@link Check} interface for a high-level introduction to the syntax.
 * </p>
 * <p>
 * <h2>Requires PowerMock</h2>
 * </p>
 * <p>
 * Setting expectations or performing checks on static methods or constructors requires that
 * <a href="http://www.powermock.org/">PowerMock</a> be on your classpath.  Additionally, in JUnit you'll need
 * to run your tests using <code>PowerMockRunner</code> and tell PowerMock to pre-instrument any classes
 * on which you'll be testing statics/constructors using the <code>PrepareForTest</code> annotation.
 * Typically you'd do this by putting annotations like these at the top of your test class:
 * <blockquote><code>
 * &#64;RunWith(PowerMockRunner.class)<br />
 * &#64;PrepareForTest(&#123; ClassWithStaticMethodToBeMocked.class &#125;)
 * </code></blockquote>
 * </p>
 * <p>
 * When calling constructors using the lambda syntax, note that the anonymous inner class that makes the call
 * to the constructor also needs to be instrumented by PowerMock.  As the compiler will typically generate
 * an unwieldy name for these classes, you can get PowerMock to instrument these by using a wildcard to
 * tell PowerMock to instrument all the classes in the package in which your test resides - for example:
 * <blockquote><code>
 * &#64;PrepareForTest(fullyQualifiedNames = "your.test.package.*")
 * </code></blockquote>
 * Note that this annotation can be applied at the individual test level.
 * </p>
 *
 * @param <R> Return type of the method being checked, or an ancestor type thereof.
 */
public interface LambdaCheck<R> extends Check<LambdaCheck<R>, R> {
    /**
     * <p>
     * Specifies the method that this check should match using a Java 8 lambda expression.
     * </p>
     * <p>
     * The lambda expression should consist solely of a call to a single method/constructor to be matched,
     * along with any {@link MoxieMatchers matcher invocations} necessary to specify parameter matching.
     * </p>
     * <p>
     * Note that {@link #on(ThrowingRunnable) on()}, {@link #when(ThrowingRunnable) when()}, {@link #get(ThrowingRunnable) get()}, {@link #got(ThrowingRunnable) got()} and {@link #that(ThrowingRunnable) that()}
     * all do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param lambda a lambda expression invoking the method to be checked
     * @return this object, for call chaining
     */
    LambdaCheck<Void> that(ThrowingRunnable lambda);

    /**
     * <p>
     * Specifies the method that this check should match using a Java 8 lambda expression.
     * </p>
     * <p>
     * The lambda expression should consist solely of a call to a single method/constructor to be matched,
     * along with any {@link MoxieMatchers matcher invocations} necessary to specify parameter matching.
     * </p>
     * <p>
     * Note that {@link #on(ThrowingRunnable) on()}, {@link #when(ThrowingRunnable) when()}, {@link #get(ThrowingRunnable) get()}, {@link #got(ThrowingRunnable) got()} and {@link #that(ThrowingRunnable) that()}
     * all do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param lambda a lambda expression invoking the method to be checked
     * @return this object, for call chaining
     */
    LambdaCheck<Void> on(ThrowingRunnable lambda);

    /**
     * <p>
     * Specifies the method that this check should match using a Java 8 lambda expression.
     * </p>
     * <p>
     * The lambda expression should consist solely of a call to a single method/constructor to be matched,
     * along with any {@link MoxieMatchers matcher invocations} necessary to specify parameter matching.
     * </p>
     * <p>
     * Note that {@link #on(ThrowingRunnable) on()}, {@link #when(ThrowingRunnable) when()}, {@link #get(ThrowingRunnable) get()}, {@link #got(ThrowingRunnable) got()} and {@link #that(ThrowingRunnable) that()}
     * all do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param lambda a lambda expression invoking the method to be checked
     * @return this object, for call chaining
     */
    LambdaCheck<Void> when(ThrowingRunnable lambda);

    /**
     * <p>
     * Specifies the method that this check should match using a Java 8 lambda expression.
     * </p>
     * <p>
     * The lambda expression should consist solely of a call to a single method/constructor to be matched,
     * along with any {@link MoxieMatchers matcher invocations} necessary to specify parameter matching.
     * </p>
     * <p>
     * Note that {@link #on(ThrowingRunnable) on()}, {@link #when(ThrowingRunnable) when()}, {@link #get(ThrowingRunnable) get()}, {@link #got(ThrowingRunnable) got()} and {@link #that(ThrowingRunnable) that()}
     * all do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param lambda a lambda expression invoking the method to be checked
     * @return this object, for call chaining
     */
    LambdaCheck<Void> get(ThrowingRunnable lambda);

    /**
     * <p>
     * Specifies the method that this check should match using a Java 8 lambda expression.
     * </p>
     * <p>
     * The lambda expression should consist solely of a call to a single method/constructor to be matched,
     * along with any {@link MoxieMatchers matcher invocations} necessary to specify parameter matching.
     * </p>
     * <p>
     * Note that {@link #on(ThrowingRunnable) on()}, {@link #when(ThrowingRunnable) when()}, {@link #get(ThrowingRunnable) get()}, {@link #got(ThrowingRunnable) got()} and {@link #that(ThrowingRunnable) that()}
     * all do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param lambda a lambda expression invoking the method to be checked
     * @return this object, for call chaining
     */
    LambdaCheck<Void> got(ThrowingRunnable lambda);

    /**
     * <p>
     * Specifies the method that this check should match using a Java 8 lambda expression.
     * </p>
     * <p>
     * The lambda expression should consist solely of a call to a single method/constructor to be matched,
     * along with any {@link MoxieMatchers matcher invocations} necessary to specify parameter matching.
     * </p>
     * <p>
     * Note that {@link #on(ThrowingSupplier) on()}, {@link #when(ThrowingSupplier) when()}, {@link #get(ThrowingSupplier) get()}, {@link #got(ThrowingSupplier) got()} and {@link #that(ThrowingSupplier) that()}
     * all do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param lambda a lambda expression invoking the method to be checked
     * @param <RR> return type of the method to be checked
     * @return this object, for call chaining
     */
    <RR extends R> LambdaCheck<RR> that(ThrowingSupplier<RR> lambda);

    /**
     * <p>
     * Specifies the method that this check should match using a Java 8 lambda expression.
     * </p>
     * <p>
     * The lambda expression should consist solely of a call to a single method/constructor to be matched,
     * along with any {@link MoxieMatchers matcher invocations} necessary to specify parameter matching.
     * </p>
     * <p>
     * Note that {@link #on(ThrowingSupplier) on()}, {@link #when(ThrowingSupplier) when()}, {@link #get(ThrowingSupplier) get()}, {@link #got(ThrowingSupplier) got()} and {@link #that(ThrowingSupplier) that()}
     * all do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param lambda a lambda expression invoking the method to be checked
     * @param <RR> return type of the method to be checked
     * @return this object, for call chaining
     */
    <RR extends R> LambdaCheck<RR> on(ThrowingSupplier<RR> lambda);

    /**
     * <p>
     * Specifies the method that this check should match using a Java 8 lambda expression.
     * </p>
     * <p>
     * The lambda expression should consist solely of a call to a single method/constructor to be matched,
     * along with any {@link MoxieMatchers matcher invocations} necessary to specify parameter matching.
     * </p>
     * <p>
     * Note that {@link #on(ThrowingSupplier) on()}, {@link #when(ThrowingSupplier) when()}, {@link #get(ThrowingSupplier) get()}, {@link #got(ThrowingSupplier) got()} and {@link #that(ThrowingSupplier) that()}
     * all do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param lambda a lambda expression invoking the method to be checked
     * @param <RR> return type of the method to be checked
     * @return this object, for call chaining
     */
    <RR extends R> LambdaCheck<RR> when(ThrowingSupplier<RR> lambda);

    /**
     * <p>
     * Specifies the method that this check should match using a Java 8 lambda expression.
     * </p>
     * <p>
     * The lambda expression should consist solely of a call to a single method/constructor to be matched,
     * along with any {@link MoxieMatchers matcher invocations} necessary to specify parameter matching.
     * </p>
     * <p>
     * Note that {@link #on(ThrowingSupplier) on()}, {@link #when(ThrowingSupplier) when()}, {@link #get(ThrowingSupplier) get()}, {@link #got(ThrowingSupplier) got()} and {@link #that(ThrowingSupplier) that()}
     * all do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param lambda a lambda expression invoking the method to be checked
     * @param <RR> return type of the method to be checked
     * @return this object, for call chaining
     */
    <RR extends R> LambdaCheck<RR> get(ThrowingSupplier<RR> lambda);

    /**
     * <p>
     * Specifies the method that this check should match using a Java 8 lambda expression.
     * </p>
     * <p>
     * The lambda expression should consist solely of a call to a single method/constructor to be matched,
     * along with any {@link MoxieMatchers matcher invocations} necessary to specify parameter matching.
     * </p>
     * <p>
     * Note that {@link #on(ThrowingSupplier) on()}, {@link #when(ThrowingSupplier) when()}, {@link #get(ThrowingSupplier) get()}, {@link #got(ThrowingSupplier) got()} and {@link #that(ThrowingSupplier) that()}
     * all do exactly the same thing - use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param lambda a lambda expression invoking the method to be checked
     * @param <RR> return type of the method to be checked
     * @return this object, for call chaining
     */
    <RR extends R> LambdaCheck<RR> got(ThrowingSupplier<RR> lambda);
}
