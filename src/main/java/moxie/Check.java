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
 * <p>
 * Domain-specific language methods for setting out the details of a retrospective mock validation.
 * </p>
 * <p/>
 * <h2>Syntax Overview</h2>
 * <p>
 * A typical Moxie check statement, such as this one:
 * </p>
 * <blockquote><code>
 * <span style="background-color: LightBlue">Moxie.check(mock)</span><span
 * style="background-color: LightPink">.returned(Moxie.lt(100)).times(3)</span><span
 * style="background-color: Khaki">.on()</span><span
 * style="background-color: LightGreen">.someMethod(Moxie.hasSubstring("foo"))</span>;
 * </code></blockquote>
 * <p>
 * can be divided into four parts:
 * </p>
 * <dl>
 * <dt><span style="font-weight: bold; background-color: LightBlue;">The first bit: identify the mock</span></dt>
 * <dd>
 * We specify the mock or spy object on which we want to perform the check.  This is done
 * using the {@link Moxie#check(Object) Moxie.check()} method.
 * </dd>
 * <dt><span style="font-weight: bold; background-color: LightPink;">The middle bit: add conditions</span></dt>
 * <dd>
 * Using the methods on the {@link Check} interface, we can attach variousadditional conditions to the check -
 * when and how many times the method should have been called, what it should have returned/thrown, etc.
 * See the next section for an overview of the different categories of methods.  Note that methods that go in this
 * section of a check statement can appear in any order.
 * </dd>
 * <dt><span style="font-weight: bold; background-color: Khaki;">The penultimate bit: create a proxy</span></dt>
 * <dd>
 * This returns a proxy object of the same type as the mock, on which we invoke the method we wish
 * to check was called.  Note that this may be done using the {@link #on()}, {@link #when()},  {@link #get()}
 * or {@link #got()} methods.  They perform exactly the same function; which you choose to call in any
 * given situation is a matter of aesthetics.
 * </dd>
 * <dt><span style="font-weight: bold; background-color: LightGreen;">The last bit: specify the method</span></dt>
 * <dd>
 * We call the method we wish to check was called on the mock, with the parameters that should have been
 * passed.  Note that you can use calls to {@link MoxieMatchers} to do flexible matching on the parameters
 * (specifying that a parameter can contain any value, will be less/greater than a certain value,
 * will contain a certain substring, etc.) - see method descriptions on that class for more details.
 * </dd>
 * </dl>
 * <p/>
 * <h2>Condition Syntax</h2>
 * <p/>
 * <p>
 * The "middle bit" of the check statement can contain zero or more condition-setting methods:
 * </p>
 * <dl>
 * <dt style="font-weight: bold">setting the expected number of invocations ({@link Cardinality} methods)</dt>
 * <dd><p>
 * The <code>Check</code> interface extends the {@link Cardinality} interface; methods from this interface
 * (like {@link Cardinality#never() never()}, {@link Cardinality#atLeastOnce() atLeastOnce()}, {@link Cardinality#atLeast(int) atLeast(int)},
 * {@link Cardinality#atMost(int) atMost(int)}) can be used to specify how many times the method should have
 * been called.
 * </p><p>
 * If none of these methods are invoked, the default behavior is {@link Cardinality#once() once()}.
 * </p></dd>
 * <dt style="font-weight: bold">negation: {@link #didNot()}</dt>
 * <dd><p>
 * {@link #didNot()} negates the sense of the check; an error will be raised if a method invocation (or set of invocations)
 * is found that matches the check, rather than if one is not found.
 * </p></dd>
 * <dt style="font-weight: bold">interaction with {@link Expectation}s: {@link #unexpectedly()}</dt>
 * <dd><p>
 * If {@link #unexpectedly()} is specified, the check will only match method invocations that did not fulfill
 * {@link Expectation expectations} (including {@link Moxie#stub(Object) stubs}).
 * </p><p>
 * <i>Caveat:</i> the ability to mix ahead-of-time expectation setting and after-the-fact checking in the same test
 * is provided for completeness and as an experimental feature.  Used carelessly, it is a sure way to render your tests
 * incomprehensible and unmaintainable.
 * </p></dd>
 * <dt style="font-weight: bold">associating checks with a {@link Group}: {@link #inGroup(Group...) inGroup()}</dt>
 * <dd><p>
 * By using a {@link Group}, checks can be made where method ordering is significant.  (Checks do not pay any attention
 * to the ordering of method calls otherwise.)
 * </p></dd>
 * <dt style="font-weight: bold">checking returned/thrown values: {@link #returned(Object) returned()} and {@link #threw(Throwable) threw()}</dt>
 * <dd><p>
 * On spy objects (and mocks with esoteric handlers), {@link #returned(Object) returned()} and {@link #threw(Throwable) threw()}
 * include the value returned/thrown by the method in the check.  You can use {@link MoxieMatchers} methods to perform a
 * flexible match.
 * </p><p>
 * These methods have identically-functional aliases, {@link #returnValue(Object) returnValue()} and {@link #throwException(Throwable) throwException()},
 * which give checks that use {@link #didNot()} a nicer-sounding alternative syntax.
 * </p></dd>
 * </dl>
 * <p/>
 * <h2>Flexibility of Syntax</h2>
 * <p/>
 * <p>
 * This initial release of Moxie offers a number of aliases and alternatives for the methods comprising its
 * domain-specific language.  This is partly out of recognition that the same DSL methods may sound stilted
 * in different situations; partly out of recognition that different developers will have differing personal
 * tastes; and partly out of plain lack of consensus as to what patterns method names should follow.
 * Does this make Moxie more confusing and difficult to learn, or does it make Moxie's DSL easier and more
 * enjoyable to use?  Feedback on this aspect of Moxie is welcome.
 * </p>
 */
public interface Check<C extends Check<C>> extends Cardinality<C>  {

    /**
     * <p>
     * Inverts the sense of the match; an error will be raised if
     * a call to the specified method matching this check is found,
     * rather than if one is not found.
     * </p>
     *
     * @return this object, for call chaining
     */
    C didNot();

    /**
     * <p>
     * This check will only match invocations that did not match a previously specified {@link Moxie#expect(Object) expectation}.
     * </p>
     * <p>
     * Only makes sense in conjunction with {@link MoxieOptions#PERMISSIVE} mocks.
     * </p>
     *
     * @return this object, for call chaining
     */
    C unexpectedly();

    /**
     * <p>
     * Check that the method executed after any methods already successfully checked as part of the given group(s).
     * </p>
     * <p>
     * See the discussion in the summary javadoc for the {@link Group} interface for more details.
     * </p>
     * <p>
     * This call will raise an error if any {@link MoxieOptions#UNORDERED} groups are specified.
     * </p>
     *
     * @param group one or more {@link Group}s
     * @return this object, for call chaining
     */
    C inGroup(Group... group);
}
