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

import java.lang.reflect.InvocationHandler;

/**
 * <p>
 * Domain-specific language methods for setting out the details of a mock object expectation.
 * </p>
 * <p/>
 * <h2>Syntax Overview</h2>
 * <p>
 * A typical Moxie expectation statement on a mock object looks like this:
 * </p>
 * <blockquote><code>
 * <span style="background-color: LightBlue">Moxie.expect(mock)</span><span
 * style="background-color: LightPink">.willReturn("someValue").atLeastOnce()</span><span
 * style="background-color: Khaki">.on()</span><span
 * style="background-color: LightGreen">.someMethod(Moxie.leq(42))</span>;
 * </code></blockquote>
 * <p>
 * It can be understood in four parts:
 * </p>
 * <dl>
 * <dt><span style="font-weight: bold; background-color: LightBlue;">The first bit: identify the mock</span></dt>
 * <dd>
 * We specify the mock or spy object on which we want to set an expectation.  This is usually done
 * using the {@link Moxie#expect(Object) Moxie.expect()} method, though we can also use the
 * {@link Moxie#stub(Object) Moxie.stub()} method to create a stub, i.e. an expectation that
 * won't affect the test if it doesn't get fulfilled.
 * </dd>
 * <dt><span style="font-weight: bold; background-color: LightPink;">The middle bit: set conditions and behaviors</span></dt>
 * <dd>
 * Using the methods on the {@link ObjectExpectation} interface, we attach various conditions and behaviors to the expectation -
 * when and how many times we expect the method to be called, what to return/throw/do when this happens, etc.
 * See the next section for an overview of the different categories of methods.  Note that methods that go in this
 * section of an expectation statement can appear in any order.
 * </dd>
 * <dt><span style="font-weight: bold; background-color: Khaki;">The penultimate bit: create a proxy</span></dt>
 * <dd>
 * This returns a proxy object of the same type as the mock, on which we invoke the method we expect
 * to be called.  Note that this may be done using the {@link ObjectExpectation#on()}, {@link ObjectExpectation#when()}
 * or {@link ObjectExpectation#will()} methods.  They perform exactly the same function; which you choose to call
 * in any given situation is a matter of aesthetics.
 * </dd>
 * <dt><span style="font-weight: bold; background-color: LightGreen;">The last bit: specify the method</span></dt>
 * <dd>
 * We call the method we expect will be called on the mock, with the parameters to be expected.
 * Note that you can use calls to {@link MoxieMatchers} to do flexible matching on method parameters
 * (specifying that a parameter can contain any value, will be less/greater than a certain value,
 * will contain a certain substring, etc.) - see method descriptions on that class for more details.
 * </dd>
 * </dl>
 * <p>
 * The syntax for setting expectations on static methods and constructors is slightly different, but reads
 * similarly - see documentation on the {@link ClassExpectation} interface for further detail:
 * </p>
 * <blockquote><code>
 * <span style="background-color: LightBlue">Moxie.expect(SomeClass.class)</span><span
 * style="background-color: LightPink">.times(2).andReturn("wibble")</span><span
 * style="background-color: LightGreen">.on("aStaticMethod", Moxie.gt(5))</span>;<br />
 * <span style="background-color: LightBlue">Moxie.expect(SomeClass.class)</span><span
 * style="background-color: LightPink">.once()</span><span
 * style="background-color: LightGreen">.onNew("constructor args", Moxie.hasSubstring("or matchers"))</span>;
 * </code></blockquote>
 * <p>
 * Finally, {@link LambdaExpectation}s let one set expectations on static methods and constructors with a
 * refactorable syntax that goes well with Java 8 lambdas.  (You can of course use this on older versions
 * of Java, but the corresponding anonymous-inner-class syntax is more cumbersome.)  Some examples:
 * </p>
 * <blockquote><code>
 * <span style="background-color: LightBlue">Moxie.expect()</span><span
 * style="background-color: LightPink">.willReturn("someValue").atLeastOnce()</span><span
 * style="background-color: Khaki">.on(<span
 * style="background-color: LightGreen">() -> &#123; mock.someMethod(Moxie.leq(42)); &#125;</span>)</span>;<br />
 * <span style="background-color: LightBlue">Moxie.expect()</span><span
 * style="background-color: LightPink">.times(2).andReturn("wibble")</span><span
 * style="background-color: Khaki">.on(<span
 * style="background-color: LightGreen">() -> &#123; SomeClass.aStaticMethod(Moxie.gt(5)); &#125;</span>)</span>;<br />
 * <span style="background-color: LightBlue">Moxie.expect()</span><span
 * style="background-color: LightPink">.once()</span><span
 * style="background-color: Khaki">.on(<span
 * style="background-color: LightGreen">() -> &#123; new SomeClass("constructor args", Moxie.hasSubstring("or matchers")); &#125;</span>)</span>;
 * </code></blockquote>
 * <p>
 * <h2>Conditions and Behaviors</h2>
 * </p>
 * <p>
 * The "middle bit" of the expectation statement can contain zero or more condition/expectation setting methods:
 * </p>
 * <dl>
 * <dt style="font-weight: bold">setting the expected number of invocations ({@link Cardinality} methods)</dt>
 * <dd><p>
 * The <code>Expectation</code> interface extends the {@link Cardinality} interface; methods from this interface
 * (like {@link Cardinality#never() never()}, {@link Cardinality#atLeastOnce() atLeastOnce()}, {@link Cardinality#atLeast(int) atLeast(int)},
 * {@link Cardinality#atMost(int) atMost(int)}) can be used to specify how many times we expect this method call
 * to be received.
 * </p><p>
 * If none of these methods are invoked, the default behavior is {@link Cardinality#once() once()}.
 * </p></dd>
 * <dt style="font-weight: bold">setting stub behavior</dt>
 * <dd><p>
 * We can specify what the mock object will do when the method is called using the
 * {@link #andReturn(Object) andReturn()}, {@link #andThrow(Throwable) andThrow()}, {@link #andDelegateTo(Object) andDelegateTo()}
 * and {@link #andHandleWith(InvocationHandler) andHandleWith()} methods.  (Note that these methods have the aliases
 * {@link #willReturn(Object) willReturn()}, {@link #willThrow(Throwable) willThrow()}, {@link #willDelegateTo(Object) willDelegateTo()}
 * and {@link #willHandleWith(InvocationHandler) willHandleWith()}, respectively; they do exactly the same thing as their
 * similarly-named cousins.)
 * </p><p>
 * Note that on spy objects, calling one of these methods specifies behavior that will be performed instead of
 * delegating to the underlying object.  If you wish to verify that the underlying object returns/throws something,
 * read on.
 * </p><p>
 * If none of these methods are invoked, the default behavior for mocks is to do nothing on void methods
 * and return the default value (<code>null</code> for objects, zero/<code>false</code> for primitives)
 * on non-void methods; for spies, the default behavior is to delegate to the same method on the object
 * being spied upon.
 * </p></dd>
 * <dt style="font-weight: bold">setting stub behavior on consecutive calls</dt>
 * <dd><p>
 * Sometimes we want the method to exhibit different behavior on consecutive calls - for instance, an iterator
 * may return a series of different values as it traverses a collection.  The {@link #andConsecutivelyReturn(Object...) andConsecutivelyReturn()}
 * method can be used for this purpose.  Similarly, {@link #andConsecutivelyThrow(Throwable...) andConsecutivelyThrow()}
 * will specify a series of <code>Throwable</code>s to be thrown on successive method calls. (These methods have
 * the aliases {@link #willConsecutivelyReturn(Object...) willConsecutivelyReturn()} and
 * {@link #willConsecutivelyThrow(Throwable...) willConsecutivelyThrow()} respectively.)
 * </p><p>
 * If we want to mix and match returning values and throwing <code>Throwable</code>s on consecutive calls, we can
 * accomplish this by calling multiple behavior-setting methods within an expectation statement. For example,
 * to get a method to return the value <code>1</code>, then throw an <code>Exception</code>, then return the value
 * <code>"three"</code> across three consecutive calls, use the following:
 * </p>
 * <blockquote style="text-indent: -4em; padding-left: 4em"><code>
 * Moxie<nobr>.expect(mock)</nobr><wbr /><nobr>.andReturn(1)</nobr><wbr /><nobr>.andThrow(new Exception("Two!"))</nobr><wbr /><nobr>.andReturn("three")</nobr><wbr /><nobr>.times(3)</nobr><wbr /><nobr>.on().someMethod();</nobr>
 * </code></blockquote>
 * <p>
 * This works equally effectively as the "consecutively" methods described above, which are essentially just shorthand
 * for this longer notation.
 * </p></dd>
 * <dt style="font-weight: bold">verifying returned/thrown values (spies only)</dt>
 * <dd><p>
 * On spy objects, you can use the {@link ObjectExpectation#andVerifyReturn(Object) andVerifyReturn()} and
 * {@link ObjectExpectation#andVerifyThrow(Throwable) andVerifyThrow()} methods (and their aliases,
 * {@link ObjectExpectation#willReturnVerified(Object) willReturnVerified()} and
 * {@link ObjectExpectation#willThrowVerified(Throwable) willThrowVerified()}) to check that the object spied upon
 * returns/throws a given value.  You can use {@link MoxieMatchers} methods to perform a
 * flexible match.
 * </p><p>
 * Note that these methods only make any sense on spy objects; if used on mock objects,
 * they will throw an error.
 * </p></dd>
 * <dt style="font-weight: bold">associating expectations with a {@link Group}: {@link #inGroup(Group...) inGroup()}</dt>
 * <dd><p>
 * The {@link #inGroup(Group...) inGroup()} method makes an expectation part of a {@link Group}, allowing us to verify
 * the ordering of a set of calls across mocks, or to verify a partial set of expectations midway through the test.
 * See the discussion and examples in the summary javadoc of the {@link Group} interface for more details.
 * </p></dd>
 * <dt style="font-weight: bold">specifying that ordering doesn't matter: {@link #atAnyTime()} ({@link MoxieOptions#ORDERED ORDERED} mocks only)</dt>
 * <dd><p>
 * Mocks/spies created with the {@link MoxieOptions#ORDERED ORDERED} option verify the order in which method calls
 * are received. By calling {@link #atAnyTime()} when setting up the expectation, we can specify that order
 * checking will not be performed on this expectation.  This call is meaningless on {@link MoxieOptions#UNORDERED UNORDERED}
 * mocks.
 * </p></dd>
 * </dl>
 * <p/>
 * <h2>Subclasses</h2>
 * <p/>
 * <p>
 * Note that most of the useful methods on this class have been migrated to one of its three subclasses -
 * please see the documentation of these classes for more detail:
 * <ul>
 * <li>{@link ObjectExpectation} - for setting expectations on most mock/spy objects in the traditional manner.</li>
 * <li>{@link ClassExpectation} - for expectations on static/constructor methods (reflection-based API).</li>
 * <li>{@link LambdaExpectation} - lets one specify mock calls (either on individual mocks or to statics/constructors)
 *     using Java 8 lambda syntax.</li>
 * </ul>
 * </p>
 */
public interface Expectation<E extends Expectation<E, R>, R> extends Cardinality<E> {

    /**
     * Specifies that this expectation should comprise part of the given group(s).
     *
     * @param groups one or more {@link Group}s created by {@link MoxieControl#group(MoxieOptions...) MoxieControl.group()}
     * @return this object, for call chaining
     */
    E inGroup(Group... groups);

    /**
     * <p>
     * Specifies that this expectation may be fulfilled at any time.
     * </p>
     * <p>
     * Only meaningful for {@link MoxieOptions#ORDERED ORDERED} mocks - expectations on which <code>atAnyTime()</code> is called
     * will not be checked as part of the mock's call ordering.
     * </p>
     *
     * @return this object, for call chaining
     */
    E atAnyTime();

    /**
     * <p>
     * When a call fulfilling this expectation is received, return the given value.
     * </p>
     * <p>
     * Note that {@link #willReturn(Object) willReturn()} and {@link #andReturn(Object) andReturn()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param result the value to be returned
     * @return this object, for call chaining
     */
    E willReturn(R result);

    /**
     * <p>
     * When a call fulfilling this expectation is received, return the given value.
     * </p>
     * <p>
     * Note that {@link #willReturn(Object) willReturn()} and {@link #andReturn(Object) andReturn()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param result the value to be returned
     * @return this object, for call chaining
     */
    E andReturn(R result);

    /**
     * <p>
     * When a call fulfilling this expectation is received, return the first value on the first invocation,
     * the second value on the second invocation, et cetera.
     * <p>
     * Note that {@link #willConsecutivelyReturn(Object...) willConsecutivelyReturn()} and {@link #andConsecutivelyReturn(Object...) andConsecutivelyReturn()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param results the values to be returned
     * @return this object, for call chaining
     */
    E willConsecutivelyReturn(R... results);

    /**
     * <p>
     * When a call fulfilling this expectation is received, return the first value on the first invocation,
     * the second value on the second invocation, et cetera.
     * <p>
     * Note that {@link #willConsecutivelyReturn(Object...) willConsecutivelyReturn()} and {@link #andConsecutivelyReturn(Object...) andConsecutivelyReturn()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param results the values to be returned
     * @return this object, for call chaining
     */
    E andConsecutivelyReturn(R... results);

    /**
     * <p>
     * When a call fulfilling this expectation is received, return the first value in the {@link Iterable}
     * on the first invocation, the second value on the second invocation, et cetera.
     * <p>
     * Note that {@link #willConsecutivelyReturn(Iterable) willConsecutivelyReturn()} and {@link #andConsecutivelyReturn(Iterable) andConsecutivelyReturn()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param results the values to be returned
     * @return this object, for call chaining
     */
    E willConsecutivelyReturn(Iterable<? super R> results);

    /**
     * <p>
     * When a call fulfilling this expectation is received, return the first value in the {@link Iterable}
     * on the first invocation, the second value on the second invocation, et cetera.
     * <p>
     * Note that {@link #willConsecutivelyReturn(Iterable) willConsecutivelyReturn()} and {@link #andConsecutivelyReturn(Iterable) andConsecutivelyReturn()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param results the values to be returned
     * @return this object, for call chaining
     */
    E andConsecutivelyReturn(Iterable<? super R> results);

    /**
     * <p>
     * When a call fulfilling this expectation is received, throw the given {@link Throwable}.
     * </p>
     * <p>
     * Note that {@link #willThrow(Throwable) willThrow()} and {@link #andThrow(Throwable) andThrow()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param throwable the {@link Throwable} to be thrown
     * @return this object, for call chaining
     */
    E willThrow(Throwable throwable);

    /**
     * <p>
     * When a call fulfilling this expectation is received, throw the given {@link Throwable}.
     * </p>
     * <p>
     * Note that {@link #willThrow(Throwable) willThrow()} and {@link #andThrow(Throwable) andThrow()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param throwable the {@link Throwable} to be thrown
     * @return this object, for call chaining
     */
    E andThrow(Throwable throwable);

    /**
     * <p>
     * When a call fulfilling this expectation is received, throw the first {@link Throwable} on the first invocation,
     * the second {@link Throwable} on the second invocation, et cetera.
     * <p>
     * Note that {@link #willConsecutivelyThrow(Throwable...) willConsecutivelyThrow()} and {@link #andConsecutivelyThrow(Throwable...) andConsecutivelyThrow()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param throwables the {@link Throwable}s to be thrown
     * @return this object, for call chaining
     */
    E willConsecutivelyThrow(Throwable... throwables);


    /**
     * <p>
     * When a call fulfilling this expectation is received, throw the first {@link Throwable} on the first invocation,
     * the second {@link Throwable} on the second invocation, et cetera.
     * <p>
     * Note that {@link #willConsecutivelyThrow(Throwable...) willConsecutivelyThrow()} and {@link #andConsecutivelyThrow(Throwable...) andConsecutivelyThrow()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param throwables the {@link Throwable}s to be thrown
     * @return this object, for call chaining
     */
    E andConsecutivelyThrow(Throwable... throwables);

    /**
     * <p>
     * When a call fulfilling this expectation is received, delegate the call to the given object.
     * </p>
     * <p>
     * Note that {@link #willDelegateTo(Object) willDelegateTo()} and {@link #andDelegateTo(Object) andDelegateTo()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param delegate the object to which the call should be delegated
     * @return this object, for call chaining
     */
    E willDelegateTo(Object delegate);

    /**
     * <p>
     * When a call fulfilling this expectation is received, delegate the call to the given object.
     * </p>
     * <p>
     * Note that {@link #willDelegateTo(Object) willDelegateTo()} and {@link #andDelegateTo(Object) andDelegateTo()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param delegate the object to which the call should be delegated
     * @return this object, for call chaining
     */
    E andDelegateTo(Object delegate);

    /**
     * <p>
     * When a call fulfilling this expectation is received, handle the call using the given {@link java.lang.reflect.InvocationHandler}.
     * </p>
     * <p>
     * Note that {@link #willHandleWith(java.lang.reflect.InvocationHandler) willHandleWith()} and {@link #andHandleWith(java.lang.reflect.InvocationHandler) andHandleWith()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param handler the {@link java.lang.reflect.InvocationHandler} that should handle this call
     * @return this object, for call chaining
     */
    E willHandleWith(InvocationHandler handler);

    /**
     * <p>
     * When a call fulfilling this expectation is received, handle the call using the given {@link java.lang.reflect.InvocationHandler}.
     * </p>
     * <p>
     * Note that {@link #willHandleWith(java.lang.reflect.InvocationHandler) willHandleWith()} and {@link #andHandleWith(java.lang.reflect.InvocationHandler) andHandleWith()} do exactly the same thing -
     * use whichever method results in the syntax you prefer best.
     * </p>
     *
     * @param handler the {@link java.lang.reflect.InvocationHandler} that should handle this call
     * @return this object, for call chaining
     */
    E andHandleWith(InvocationHandler handler);

}
