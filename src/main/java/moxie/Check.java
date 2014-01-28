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
 *
 * Domain-specific language methods for setting out the details of a retrospective mock validation.
 * <p>
 * &nbsp;
 * <h3>Syntax Overview</h3>
 *
 * A typical Moxie check statement on a mock object looks like this:
 * <p>
 * <blockquote><code>
 * <span style="background-color: LightBlue">Moxie.check(mock)</span><span
 * style="background-color: LightPink">.returned(Moxie.lt(100)).times(3)</span><span
 * style="background-color: Khaki">.on()</span><span
 * style="background-color: LightGreen">.someMethod(Moxie.hasSubstring("foo"))</span>;
 * </code></blockquote>
 *
 * It can be understood in four parts:
 * <p>
 * <dl>
 * <dt><span style="font-weight: bold; background-color: LightBlue;">The first bit: identify the mock</span></dt>
 * <dd>
 * We specify the mock or spy object on which we want to perform the check.  This is done
 * using the {@link Moxie#check(Object) Moxie.check()} method.
 * </dd>
 * <dt><span style="font-weight: bold; background-color: LightPink;">The middle bit: add conditions</span></dt>
 * <dd>
 * Using the methods on the {@link ObjectCheck} interface, we can attach various additional conditions to the check -
 * when and how many times the method should have been called, what it should have returned/thrown, etc.
 * See the next section for an overview of the different categories of methods.  Note that methods that go in this
 * section of a check statement can appear in any order.
 * </dd>
 * <dt><span style="font-weight: bold; background-color: Khaki;">The penultimate bit: create a proxy</span></dt>
 * <dd>
 * This returns a proxy object of the same type as the mock, on which we invoke the method we wish
 * to check was called.  Note that this may be done using the {@link ObjectCheck#on()}, {@link ObjectCheck#when()},
 * {@link ObjectCheck#get()} or {@link ObjectCheck#got()} methods.  They perform exactly the same function;
 * which you choose to call in any given situation is a matter of aesthetics.
 * </dd>
 * <dt><span style="font-weight: bold; background-color: LightGreen;">The last bit: specify the method</span></dt>
 * <dd>
 * We call the method we wish to check was called on the mock, with the parameters that should have been
 * passed.  Note that you can use calls to {@link MoxieMatchers} to do flexible matching on the parameters
 * (specifying that a parameter can contain any value, will be less/greater than a certain value,
 * will contain a certain substring, etc.) - see method descriptions on that class for more details.
 * </dd>
 * </dl>
 *
 * The syntax for specifying checks on static methods and constructorss is slightly different, but reads
 * similarly - see documentation on the {@link ClassCheck} interface for further detail:
 * <p>
 * <blockquote><code>
 * <span style="background-color: LightBlue">Moxie.check(SomeClass.class)</span><span
 * style="background-color: LightPink">.returned(Moxie.startsWith("x")).once()</span><span
 * style="background-color: LightGreen">.on("someStaticMethod", Moxie.endsWith("y"))</span>;
 * <br>
 * <span style="background-color: LightBlue">Moxie.check(SomeClass.class)</span><span
 * style="background-color: LightPink">.atLeast(3)</span><span
 * style="background-color: LightGreen">.onNew("constructor args or matchers")</span>;
 * </code></blockquote>
 *
 * Finally, {@link LambdaCheck}s let one perform checks on static methods and constructors with a refactorable
 * syntax that goes well with Java 8 lambdas.  (You can of course use this on older versions of Java, but the
 * corresponding anonymous-inner-class syntax is more cumbersome.)  Some examples:
 * <p>
 * <blockquote><code>
 * <span style="background-color: LightBlue">Moxie.check()</span><span
 * style="background-color: LightPink">.returned(Moxie.lt(100)).times(3)</span><span
 * style="background-color: Khaki">.on(<span
 * style="background-color: LightGreen">() -&gt; &#123; mock.someMethod(Moxie.hasSubstring("foo")); &#125;</span>)</span>;
 * <br>
 * <span style="background-color: LightBlue">Moxie.check()</span><span
 * style="background-color: LightPink">.returned(Moxie.startsWith("x")).once()</span><span
 * style="background-color: Khaki">.on(<span
 * style="background-color: LightGreen">() -&gt; &#123; SomeClass.someStaticMethod(Moxie.endsWith("y")); &#125;</span>)</span>;
 * <br>
 * <span style="background-color: LightBlue">Moxie.check()</span><span
 * style="background-color: LightPink">.atLeast(3)</span><span
 * style="background-color: Khaki">.on(<span
 * style="background-color: LightGreen">() -&gt; &#123; new SomeClass("constructor args or matchers"); &#125;</span>)</span>;
 * </code></blockquote>
 *
 * &nbsp;
 * <h3>Condition Syntax</h3>
 *
 * The "middle bit" of the check statement can contain zero or more condition-setting methods:
 * <p>
 * <dl>
 * <dt style="font-weight: bold">setting the expected number of invocations ({@link Cardinality} methods)</dt>
 * <dd>
 * The <code>Check</code> interface extends the {@link Cardinality} interface; methods from this interface
 * (like {@link Cardinality#never() never()}, {@link Cardinality#atLeastOnce() atLeastOnce()}, {@link Cardinality#atLeast(int) atLeast(int)},
 * {@link Cardinality#atMost(int) atMost(int)}) can be used to specify how many times the method should have
 * been called.
 * <p>
 * If none of these methods are invoked, the default behavior is {@link Cardinality#once() once()}.
 * <p></dd>
 * <dt style="font-weight: bold">negation: {@link #didNot()}</dt>
 * <dd>
 * {@link #didNot()} negates the sense of the check; an error will be raised if a method invocation (or set of invocations)
 * is found that matches the check, rather than if one is not found.
 * <p></dd>
 * <dt style="font-weight: bold">interaction with {@link Expectation}s: {@link #unexpectedly()}</dt>
 * <dd>
 * If {@link #unexpectedly()} is specified, the check will only match method invocations that did not fulfill
 * {@link Expectation expectations} (including {@link Moxie#stub(Object) stubs}).
 * <p>
 * <i>Caveat:</i> the ability to mix ahead-of-time expectation setting and after-the-fact checking in the same test
 * is provided for completeness and as an experimental feature.  Used carelessly, it is a sure way to render your tests
 * incomprehensible and unmaintainable.
 * <p></dd>
 * <dt style="font-weight: bold">associating checks with a {@link Group}: {@link #inGroup(Group...) inGroup()}</dt>
 * <dd>
 * By using a {@link Group}, checks can be made where method ordering is significant.  (Checks do not pay any attention
 * to the ordering of method calls otherwise.)
 * <p></dd>
 * <dt style="font-weight: bold">checking returned/thrown values: {@link ObjectCheck#returned(Object) returned()} and {@link ObjectCheck#threw(Throwable) threw()}</dt>
 * <dd>
 * On spy objects (and mocks with esoteric handlers), {@link ObjectCheck#returned(Object) returned()} and {@link ObjectCheck#threw(Throwable) threw()}
 * include the value returned/thrown by the method in the check.  You can use {@link MoxieMatchers} methods to perform a
 * flexible match.
 * <p>
 * These methods have identically-functional aliases, {@link ObjectCheck#returnValue(Object) returnValue()} and {@link ObjectCheck#throwException(Throwable) throwException()},
 * which give checks that use {@link #didNot()} a nicer-sounding alternative syntax.
 * <p>
 * Note that these methods cannot be used on final, private, static or constructor methods (thus they are only available
 * on the {@link ObjectCheck} interface).  The way these are implemented in Moxie involves using PowerMock to replace
 * the original implementation of the method/constructor with a stub; as {@link ObjectCheck#returned(Object) returned()}
 * and {@link ObjectCheck#threw(Throwable) threw()} are intended to verify the behavior of the underlying implementation
 * in the mocked class, including them would make no sense.
 * <p></dd>
 * </dl>
 * &nbsp;
 * <h3>Subclasses</h3>
 *
 * Note that most of the useful methods on this class have been migrated to one of its three subclasses -
 * please see the documentation of these classes for more detail:
 * <p>
 * <ul>
 * <li>{@link ObjectCheck} - for checking calls on most mock/spy objects in the traditional manner.</li>
 * <li>{@link ClassCheck} - for checking calls to static/constructor methods (reflection-based API).</li>
 * <li>{@link LambdaCheck} - lets one specify mock calls (either on individual mocks or to statics/constructors)
 *     using Java 8 lambda syntax.</li>
 * </ul>
 */
public interface Check<C extends Check<C, R>, R> extends Cardinality<C>  {

    /**
     *
     * Inverts the sense of the match; an error will be raised if
     * a call to the specified method matching this check is found,
     * rather than if one is not found.
     * <p>
     *
     * @return this object, for call chaining
     */
    C didNot();

    /**
     *
     * This check will only match invocations that did not match a previously specified {@link Moxie#expect(Object) expectation}.
     * <p>
     *
     * Only makes sense in conjunction with {@link MoxieOptions#PERMISSIVE} mocks.
     * <p>
     *
     * @return this object, for call chaining
     */
    C unexpectedly();

    /**
     *
     * Check that the method executed after any methods already successfully checked as part of the given group(s).
     * <p>
     *
     * See the discussion in the summary javadoc for the {@link Group} interface for more details.
     * <p>
     *
     * This call will raise an error if any {@link MoxieOptions#UNORDERED} groups are specified.
     * <p>
     *
     * @param group one or more {@link Group}s
     * @return this object, for call chaining
     */
    C inGroup(Group... group);

    /**
     *
     * Check that the relevant method threw the given {@link Throwable}.
     * <p>
     *
     * Note that you can use the {@link moxie.MoxieMatchers matcher methods} to match on the exception object.
     * <p>
     *
     * Note also that that {@link #throwException(Throwable) throwException()} and {@link #threw(Throwable) threw()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * <p>
     *
     * @param throwable the {@link Throwable} thrown, or a matcher invocation
     * @return this object, for call chaining
     */
    C throwException(Throwable throwable);

    /**
     *
     * Check that the relevant method threw the given {@link Throwable}.
     * <p>
     *
     * Note that you can use the {@link moxie.MoxieMatchers matcher methods} to match on the exception object.
     * <p>
     *
     * Note also that that {@link #throwException(Throwable) throwException()} and {@link #threw(Throwable) threw()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * <p>
     *
     * @param throwable the {@link Throwable} thrown, or a matcher invocation
     * @return this object, for call chaining
     */
    C threw(Throwable throwable);

    /**
     *
     * Check that the relevant method returned the given value.
     * <p>
     *
     * Note that you can use the {@link moxie.MoxieMatchers matcher methods} to match on the return value.
     * <p>
     *
     * Note also that that {@link #returnValue(Object) returnValue()} and {@link #returned(Object) returned()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * <p>
     *
     * @param returnValue the value returned, or a matcher invocation
     * @return this object, for call chaining
     */
    C returnValue(R returnValue);

    /**
     *
     * Check that the relevant method returned the given value.
     * <p>
     *
     * Note that you can use the {@link moxie.MoxieMatchers matcher methods} to match on the return value.
     * <p>
     *
     * Note also that that {@link #returnValue(Object) returnValue()} and {@link #returned(Object) returned()}
     * do exactly the same thing - use whichever method results in the syntax you prefer best.
     * <p>
     *
     * @param returnValue the value returned, or a matcher invocation
     * @return this object, for call chaining
     */
    C returned(R returnValue);
}
