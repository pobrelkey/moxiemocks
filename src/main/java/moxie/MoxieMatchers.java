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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.IsSame;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.hamcrest.text.StringContains;
import org.hamcrest.text.StringEndsWith;
import org.hamcrest.text.StringStartsWith;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>
 * Static class whose methods magically signal Moxie to flexibly match parameters of a mock expectation.
 * </p>
 * <p/>
 * <h2>What are these methods?</h2>
 * <p>
 * On certain method calls where you specify values that Moxie should expect as part of observed behavior -
 * for example, method parameters on mock object calls, or return values on calls to spy objects - you can
 * use the methods on <code>MoxieMatchers</code> to set flexible criteria for each parameter.
 * </p>
 * <p>
 * These are used to generate <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher}s
 * which will be matched against the actual value(s).
 * </p>
 * <p/>
 * <h2>How do they work?</h2>
 * <p>
 * (The gory details of the magic behind this class follow - you may safely skip to the next section
 * if this bores/confuses you.)
 * </p>
 * <p>
 * Every time you call a method on <code>MoxieMatchers</code>, a <code>Matcher</code> is pushed onto a stack,
 * along with (in many cases, especially for primitives) an expected parameter type.  The method then returns
 * the default value (<code>null</code> for methods returning an object, zero/<code>false</code> for methods
 * returning a primitive value).
 * </p>
 * <p>
 * When you call a method that can take matcher invocations as parameters and the matcher stack is non-empty,
 * the arguments to that method are examined from right to left.  For arguments that are equal to the default value,
 * the <code>Matcher</code> on top of the stack is popped off and used (unless it expects a value of an
 * incompatible type, in which case an error is raised).  For non-default arguments, an
 * {@link org.hamcrest.core.IsEqual IsEqual} matcher is created which will match on a value equal to that argument.
 * </p>
 * <p>
 * When you call a method that can take matcher invocations as parameters and the matcher stack is empty,
 * an {@link org.hamcrest.core.IsEqual IsEqual} matcher is created for each parameter.
 * </p>
 * <p>
 * At the end of this process, the matcher stack is checked, and an error is raised if it is non-empty.
 * (Note that this does not occur in methods like {@link #and(Object[]) and()}, {@link #or(Object[]) or()}
 * and {@link #not(Object) not()} which may themselves be part of matcher expressions.)
 * </p>
 * <p/>
 * <h2>What do I need to remember?</h2>
 * <ul>
 * <li>Never save the result of a call to <code>MoxieMatchers</code> to a variable - always plug it straight in to where it needs to go.</li>
 * <li>If you're using a <code>MoxieMatchers</code> call anywhere in a method call, make sure all zero/<code>false</code>/<code>null</code>
 * parameters to that call are replaced with {@link #eq(int) eq(0)}, {@link #eq(boolean) eq(false)} or {@link #isNull()} respectively.</li>
 * </ul>
 */
public abstract class MoxieMatchers {
    private static final BaseMatcher ANY_ARRAY_MATCHER = new BaseMatcher() {
        public boolean matches(Object o) {
            return o != null && o.getClass().isArray();
        }

        public void describeTo(Description description) {
            description.appendText("any array");
        }
    };

    private static ThreadLocal<LinkedList<MatcherReport>> matchers = new ThreadLocal<LinkedList<MatcherReport>>();

    MoxieMatchers() {
    }

    /**
     * Matches any array.
     *
     * @param <T> element type of the array
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T[] anyArray() {
        return (T[]) reportMatcher(new AnyOf(Arrays.asList(new IsNull(), ANY_ARRAY_MATCHER)), Object[].class);
    }

    /**
     * Matches any <code>boolean</code> value.
     *
     * @return <code>false</code>
     */
    static public boolean anyBoolean() {
        return any(Boolean.TYPE);
    }

    /**
     * Matches any <code>byte</code> value.
     *
     * @return <code>0</code>
     */
    static public byte anyByte() {
        return any(Byte.TYPE);
    }

    /**
     * Matches any <code>char</code> value.
     *
     * @return <code>'\0'</code>
     */
    static public char anyChar() {
        return any(Character.TYPE);
    }

    /**
     * Matches any <code>double</code> value.
     *
     * @return <code>0</code>
     */
    static public double anyDouble() {
        return any(Double.TYPE);
    }

    /**
     * Matches any <code>float</code> value.
     *
     * @return <code>0</code>
     */
    static public float anyFloat() {
        return any(Float.TYPE);
    }

    /**
     * Matches any <code>int</code> value.
     *
     * @return <code>0</code>
     */
    static public int anyInt() {
        return any(Integer.TYPE);
    }

    /**
     * Matches any <code>long</code> value.
     *
     * @return <code>0</code>
     */
    static public long anyLong() {
        return any(Long.TYPE);
    }

    /**
     * Matches any <code>short</code> value.
     *
     * @return <code>0</code>
     */
    static public short anyShort() {
        return any(Short.TYPE);
    }

    /**
     * Matches any value, including <code>null</code>.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T anything() {
        return (T) any(Object.class);
    }

    /**
     * Synonym for {@link #anything}.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T anyObject() {
        return (T) anything();
    }

    /**
     * Matches any {@link String} or <code>null</code>.
     *
     * @return <code>null</code>
     */
    static public String anyString() {
        return any(String.class);
    }

    /**
     * Matches any value assignable to the given class, or <code>null</code>.
     *
     * @return <code>null</code> if the given class is an object, or the primitive's default value if the given class is a primitive
     */
    @SuppressWarnings("unchecked")
    static public <T> T any(Class<T> clazz) {
        return reportMatcher(new AnyOf(Arrays.asList(new IsNull(), new IsInstanceOf(MoxieUtils.toNonPrimitive(clazz)))), clazz);
    }

    /**
     * Matches an object parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T argThat(Matcher<? super T> matcher) {
        return argThat(null, matcher);
    }

    /**
     * <p>
     * Matches an object parameter using the given custom matcher.
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param clazz   Type of the parameter to be matched
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T argThat(Class<T> clazz, Matcher<? super T> matcher) {
        return reportMatcher(matcher, clazz);
    }

    /**
     * Matches an array parameter or varargs array using the given custom matcher.
     *
     * @param matcher A <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T[] arrayThat(Matcher<? super T> matcher) {
        return (T[]) arrayThat(Object.class, (Matcher) matcher);
    }

    /**
     * <p>
     * Matches an array parameter or varargs array using the given custom matcher.
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param clazz   Constituent type of the array
     * @param matcher A <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>null</code>
     */
    static public <T> T[] arrayThat(Class<T> clazz, Matcher<? super T> matcher) {
        return reportMatcher(matcher, MoxieUtils.arrayClassFor(clazz));
    }

    /**
     * Matches a <code>boolean</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>false</code>
     */
    static public boolean booleanThat(Matcher<? super Boolean> matcher) {
        return reportMatcher(matcher, Boolean.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public byte byteThat(Matcher<? super Byte> matcher) {
        return reportMatcher(matcher, Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>'\0'</code>
     */
    static public char charThat(Matcher<? super Character> matcher) {
        return reportMatcher(matcher, Character.TYPE);
    }

    /**
     * Matches a <code>double</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public double doubleThat(Matcher<? super Double> matcher) {
        return reportMatcher(matcher, Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public float floatThat(Matcher<? super Float> matcher) {
        return reportMatcher(matcher, Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public int intThat(Matcher<? super Integer> matcher) {
        return reportMatcher(matcher, Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public long longThat(Matcher<? super Long> matcher) {
        return reportMatcher(matcher, Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public short shortThat(Matcher<? super Short> matcher) {
        return reportMatcher(matcher, Short.TYPE);
    }

    /**
     * Matches a <code>boolean</code> parameter equal to the given value.
     *
     * @return <code>false</code>
     */
    @SuppressWarnings("unchecked")
    static public boolean eq(boolean value) {
        return reportMatcher(new IsEqual(value), Boolean.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    @SuppressWarnings("unchecked")
    static public byte eq(byte value) {
        return reportMatcher(new IsEqual(value), Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter equal to the given value.
     *
     * @return <code>'\0'</code>
     */
    @SuppressWarnings("unchecked")
    static public char eq(char value) {
        return reportMatcher(new IsEqual(value), Character.TYPE);
    }

    /**
     * Matches a <code>double</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    @SuppressWarnings("unchecked")
    static public double eq(double value) {
        return reportMatcher(new IsEqual(value), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    @SuppressWarnings("unchecked")
    static public float eq(float value) {
        return reportMatcher(new IsEqual(value), Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    @SuppressWarnings("unchecked")
    static public int eq(int value) {
        return reportMatcher(new IsEqual(value), Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    @SuppressWarnings("unchecked")
    static public long eq(long value) {
        return reportMatcher(new IsEqual(value), Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    @SuppressWarnings("unchecked")
    static public short eq(short value) {
        return reportMatcher(new IsEqual(value), Short.TYPE);
    }

    /**
     * Matches a parameter equal to the given value.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T eq(T value) {
        return (T) reportMatcher(new IsEqual(value), (value != null ? value.getClass() : null));
    }

    /**
     * Matches a non-<code>null</code> parameter assignable to the given class.
     *
     * @return <code>null</code>
     */
    static public <T> T isA(Class<T> clazz) {
        return (T) reportMatcher(new IsInstanceOf(MoxieUtils.toNonPrimitive(clazz)), clazz);
    }

    /**
     * Matches any non-<code>null</code> parameter.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T isNotNull() {
        return (T) isNotNull(Object.class);
    }

    /**
     * <p>
     * Matches any non-<code>null</code> parameter.
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @return <code>null</code>
     */
    static public <T> T isNotNull(Class<T> clazz) {
        return reportMatcher(new IsNot<T>(new IsNull<T>()), clazz);
    }

    /**
     * Matches only when the parameter is <code>null</code>.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T isNull() {
        return (T) isNull(Object.class);
    }

    /**
     * <p>
     * Matches only when the parameter is <code>null</code>.
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @return <code>null</code>
     */
    static public <T> T isNull(Class<T> clazz) {
        return reportMatcher(new IsNull<T>(), clazz);
    }

    /**
     * Synonym for {@link #isNotNull() isNotNull}.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T notNull() {
        return (T) notNull(Object.class);
    }

    /**
     * <p>
     * Synonym for {@link #isNotNull(Class) isNotNull}.
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @return <code>null</code>
     */
    static public <T> T notNull(Class<T> clazz) {
        return isNotNull(clazz);
    }
    
    /**
     * Matches when the parameter is referentially equal to the given value.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T same(T value) {
        return (T) reportMatcher(new IsSame(value), (value != null ? value.getClass() : null));
    }

    /**
     * Matches a {@link String} parameter containing the given substring.
     *
     * @return <code>null</code>
     */
    static public String hasSubstring(String substring) {
        return reportMatcher(new StringContains(substring), String.class);
    }

    /**
     * Matches a {@link String} parameter ending with the given suffix.
     *
     * @return <code>null</code>
     */
    static public String endsWith(String suffix) {
        return reportMatcher(new StringEndsWith(suffix), String.class);
    }

    /**
     * Matches a {@link String} parameter starting with the given prefix.
     *
     * @return <code>null</code>
     */
    static public String startsWith(String prefix) {
        return reportMatcher(new StringStartsWith(prefix), String.class);
    }

    /**
     * Matches a <code>boolean</code> parameter satisfying all of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>false</code>
     */
    static public boolean and(boolean... matchers) {
        return reportAnd(matchers, Boolean.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter satisfying all of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public byte and(byte... matchers) {
        return reportAnd(matchers, Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter satisfying all of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>'\0'</code>
     */
    static public char and(char... matchers) {
        return reportAnd(matchers, Character.TYPE);
    }

    /**
     * Matches a <code>double</code> parameter satisfying all of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public double and(double... matchers) {
        return reportAnd(matchers, Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter satisfying all of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public float and(float... matchers) {
        return reportAnd(matchers, Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter satisfying all of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public int and(int... matchers) {
        return reportAnd(matchers, Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter satisfying all of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public long and(long... matchers) {
        return reportAnd(matchers, Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter satisfying all of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public short and(short... matchers) {
        return reportAnd(matchers, Short.TYPE);
    }

    /**
     * Matches a parameter satisfying all of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T and(T... matchers) {
        return (T) reportAnd(matchers, null);
    }

    /**
     * Matches a <code>boolean</code> parameter if and only if it is not matched by the given matcher.
     *
     * @param matcher a match value or {@link MoxieMatchers} invocation
     * @return <code>false</code>
     */
    static public boolean not(boolean matcher) {
        return reportNot(matcher, Boolean.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter if and only if it is not matched by the given matcher.
     *
     * @param matcher a match value or {@link MoxieMatchers} invocation
     * @return <code>0</code>
     */
    static public byte not(byte matcher) {
        return reportNot(matcher, Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter if and only if it is not matched by the given matcher.
     *
     * @param matcher a match value or {@link MoxieMatchers} invocation
     * @return <code>'\0'</code>
     */
    static public char not(char matcher) {
        return reportNot(matcher, Character.TYPE);
    }

    /**
     * Matches a <code>double</code> parameter if and only if it is not matched by the given matcher.
     *
     * @param matcher a match value or {@link MoxieMatchers} invocation
     * @return <code>0</code>
     */
    static public double not(double matcher) {
        return reportNot(matcher, Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter if and only if it is not matched by the given matcher.
     *
     * @param matcher a match value or {@link MoxieMatchers} invocation
     * @return <code>0</code>
     */
    static public float not(float matcher) {
        return reportNot(matcher, Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter if and only if it is not matched by the given matcher.
     *
     * @param matcher a match value or {@link MoxieMatchers} invocation
     * @return <code>0</code>
     */
    static public int not(int matcher) {
        return reportNot(matcher, Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter if and only if it is not matched by the given matcher.
     *
     * @param matcher a match value or {@link MoxieMatchers} invocation
     * @return <code>0</code>
     */
    static public long not(long matcher) {
        return reportNot(matcher, Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter if and only if it is not matched by the given matcher.
     *
     * @param matcher a match value or {@link MoxieMatchers} invocation
     * @return <code>0</code>
     */
    static public short not(short matcher) {
        return reportNot(matcher, Short.TYPE);
    }

    /**
     * Matches a parameter if and only if it is not matched by the given matcher.
     *
     * @param matcher a match value or {@link MoxieMatchers} invocation
     * @return <code>null</code>
     */
    static public <T> T not(T matcher) {
        return (T) reportNot(matcher, null);
    }

    /**
     * Matches a <code>boolean</code> parameter satisfying any of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>false</code>
     */
    static public boolean or(boolean... matchers) {
        return reportOr(matchers, Boolean.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter satisfying any of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public byte or(byte... matchers) {
        return reportOr(matchers, Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter satisfying any of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>'\0'</code>
     */
    static public char or(char... matchers) {
        return reportOr(matchers, Character.TYPE);
    }

    /**
     * Matches a <code>double</code> parameter satisfying any of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public double or(double... matchers) {
        return reportOr(matchers, Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter satisfying any of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public float or(float... matchers) {
        return reportOr(matchers, Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter satisfying any of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public int or(int... matchers) {
        return reportOr(matchers, Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter satisfying any of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public long or(long... matchers) {
        return reportOr(matchers, Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter satisfying any of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>0</code>
     */
    static public short or(short... matchers) {
        return reportOr(matchers, Short.TYPE);
    }

    /**
     * Matches a parameter satisfying any of the given matcher invocations.
     *
     * @param matchers one or more match values or {@link MoxieMatchers} invocations
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T or(T... matchers) {
        return (T) reportOr(matchers, null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T reportAnd(Object matchValuesArray, Class<T> clazz) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(clazz, matchValuesArray);
        return reportMatcher(new AllOf(matchers), clazz);
    }

    @SuppressWarnings("unchecked")
    private static <T> T reportOr(Object matchValuesArray, Class<T> clazz) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(clazz, matchValuesArray);
        return reportMatcher(new AnyOf(matchers), clazz);
    }

    @SuppressWarnings("unchecked")
    private static <T> T reportNot(T matchValue, Class<T> clazz) {
        Matcher matcher = MatcherSyntax.singleMatcherFragment(clazz, matchValue);
        return reportMatcher(new IsNot(matcher), clazz);
    }

    /**
     * Matches a {@link String} parameter equal to the given value ignoring case.
     *
     * @return <code>null</code>
     */
    static public String eqIgnoreCase(String value) {
        return reportMatcher(new IsEqualIgnoringCase(value), String.class);
    }

    /**
     * Matches a {@link String} matching the given regular expression pattern.
     *
     * @param pattern Regular expression to match against the string
     * @return <code>null</code>
     */
    static public String matchesRegexp(String pattern) {
        return matchesRegexp(String.class, pattern);
    }

    /**
     * <p>
     * Matches a parameter whose {@link String} representation matches the given regular expression pattern.
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param clazz   Type of the object to be matched
     * @param pattern Regular expression to match against object's string representation
     * @return <code>null</code>
     */
    static public <T> T matchesRegexp(Class<T> clazz, String pattern) {
        return matchesRegexp(clazz, Pattern.compile(pattern));
    }

    /**
     * Matches a {@link String} matching the given {@link Pattern}.
     *
     * @return <code>null</code>
     */
    static public String matchesRegexp(Pattern pattern) {
        return matchesRegexp(String.class, pattern);
    }

    /**
     * <p>
     * Matches a parameter whose {@link String} representation matches the given {@link Pattern}.
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param clazz   Type of the object to be matched
     * @param pattern Regular expression to match against object's string representation
     * @return <code>null</code>
     */
    static public <T> T matchesRegexp(Class<T> clazz, final Pattern pattern) {
        return reportMatcher(new BaseMatcher() {
            public boolean matches(Object o) {
                return (o != null) && pattern.matcher(o.toString()).matches();
            }

            public void describeTo(Description description) {
                description.appendText("matches /" + pattern.pattern() + '/');
            }
        }, (Class<T>) null);
    }

    /**
     * Matches a <code>double</code> parameter equal to <code>value</code> plus or minus <code>delta</code>.
     *
     * @return <code>0</code>
     */
    static public double eq(double value, double delta) {
        return reportMatcher(isCloseMatcher(value, delta), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter equal to <code>value</code> plus or minus <code>delta</code>.
     *
     * @return <code>0</code>
     */
    static public float eq(float value, float delta) {
        return reportMatcher(isCloseMatcher(value, delta), Float.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public byte geq(byte value) {
        return reportMatcher(greaterThanOrEqualTo(value), Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter greater than or equal to the given value.
     *
     * @return <code>'\0'</code>
     */
    static public char geq(char value) {
        return reportMatcher(greaterThanOrEqualTo(value), Character.TYPE);
    }

    /**
     * Matches a {@link Comparable} parameter greater than or equal to the given value.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T extends Comparable<T>> T geq(T value) {
        return (T) reportMatcher(greaterThanOrEqualTo(value), Comparable.class);
    }

    /**
     * Matches a <code>double</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public double geq(double value) {
        return reportMatcher(greaterThanOrEqualTo(value), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public float geq(float value) {
        return reportMatcher(greaterThanOrEqualTo(value), Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public int geq(int value) {
        return reportMatcher(greaterThanOrEqualTo(value), Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public long geq(long value) {
        return reportMatcher(greaterThanOrEqualTo(value), Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public short geq(short value) {
        return reportMatcher(greaterThanOrEqualTo(value), Short.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public byte gt(byte value) {
        return reportMatcher(greaterThan(value), Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter greater than the given value.
     *
     * @return <code>'\0'</code>
     */
    static public char gt(char value) {
        return reportMatcher(greaterThan(value), Character.TYPE);
    }

    /**
     * Matches a {@link Comparable} parameter greater than the given value.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T extends Comparable<T>> T gt(T value) {
        return (T) reportMatcher(greaterThan(value), Comparable.class);
    }

    /**
     * Matches a <code>double</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public double gt(double value) {
        return reportMatcher(greaterThan(value), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public float gt(float value) {
        return reportMatcher(greaterThan(value), Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public int gt(int value) {
        return reportMatcher(greaterThan(value), Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public long gt(long value) {
        return reportMatcher(greaterThan(value), Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public short gt(short value) {
        return reportMatcher(greaterThan(value), Short.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public byte leq(byte value) {
        return reportMatcher(lessThanOrEqualTo(value), Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter less than or equal to the given value.
     *
     * @return <code>'\0'</code>
     */
    static public char leq(char value) {
        return reportMatcher(lessThanOrEqualTo(value), Character.TYPE);
    }

    /**
     * Matches a {@link Comparable} parameter less than or equal to the given value.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T extends Comparable<T>> T leq(T value) {
        return (T) reportMatcher(lessThanOrEqualTo(value), Comparable.class);
    }

    /**
     * Matches a <code>double</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public double leq(double value) {
        return reportMatcher(lessThanOrEqualTo(value), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public float leq(float value) {
        return reportMatcher(lessThanOrEqualTo(value), Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public int leq(int value) {
        return reportMatcher(lessThanOrEqualTo(value), Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public long leq(long value) {
        return reportMatcher(lessThanOrEqualTo(value), Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public short leq(short value) {
        return reportMatcher(lessThanOrEqualTo(value), Short.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public byte lt(byte value) {
        return reportMatcher(lessThan(value), Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter less than the given value.
     *
     * @return <code>'\0'</code>
     */
    static public char lt(char value) {
        return reportMatcher(lessThan(value), Character.TYPE);
    }

    /**
     * Matches a {@link Comparable} parameter less than the given value.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T extends Comparable<T>> T lt(T value) {
        return (T) reportMatcher(lessThan(value), Comparable.class);
    }

    /**
     * Matches a <code>double</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public double lt(double value) {
        return reportMatcher(lessThan(value), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public float lt(float value) {
        return reportMatcher(lessThan(value), Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public int lt(int value) {
        return reportMatcher(lessThan(value), Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public long lt(long value) {
        return reportMatcher(lessThan(value), Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public short lt(short value) {
        return reportMatcher(lessThan(value), Short.TYPE);
    }


    /**
     * <p>
     * Matches an array of <code>boolean</code> values matching the given array.
     * </p>
     * <p>
     * Note that the elements of the array may be either constants or {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public boolean[] array(boolean... value) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(Boolean.TYPE, value);
        return reportMatcher(isArrayMatcher(matchers), boolean[].class);
    }

    /**
     * <p>
     * Matches an array of <code>byte</code> values matching the given array.
     * </p>
     * <p>
     * Note that the elements of the array may be either constants or {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public byte[] array(byte... value) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(Byte.TYPE, value);
        return reportMatcher(isArrayMatcher(matchers), byte[].class);
    }

    /**
     * <p>
     * Matches an array of <code>char</code> values matching the given array.
     * </p>
     * <p>
     * Note that the elements of the array may be either constants or {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public char[] array(char... value) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(Character.TYPE, value);
        return reportMatcher(isArrayMatcher(matchers), char[].class);
    }

    /**
     * <p>
     * Matches an array of <code>double</code> values matching the given array.
     * </p>
     * <p>
     * Note that the elements of the array may be either constants or {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public double[] array(double... value) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(Double.TYPE, value);
        return reportMatcher(isArrayMatcher(matchers), double[].class);
    }

    /**
     * <p>
     * Matches an array of <code>float</code> values matching the given array.
     * </p>
     * <p>
     * Note that the elements of the array may be either constants or {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public float[] array(float... value) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(Float.TYPE, value);
        return reportMatcher(isArrayMatcher(matchers), float[].class);
    }

    /**
     * <p>
     * Matches an array of <code>int</code> values matching the given array.
     * </p>
     * <p>
     * Note that the elements of the array may be either constants or {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public int[] array(int... value) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(Integer.TYPE, value);
        return reportMatcher(isArrayMatcher(matchers), int[].class);
    }

    /**
     * <p>
     * Matches an array of <code>long</code> values matching the given array.
     * </p>
     * <p>
     * Note that the elements of the array may be either constants or {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public long[] array(long... value) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(Long.TYPE, value);
        return reportMatcher(isArrayMatcher(matchers), long[].class);
    }

    /**
     * <p>
     * Matches an array of <code>short</code> values matching the given array.
     * </p>
     * <p>
     * Note that the elements of the array may be either constants or {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public short[] array(short... value) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(Short.TYPE, value);
        return reportMatcher(isArrayMatcher(matchers), short[].class);
    }

    /**
     * <p>
     * Matches an array of values matching the given array.
     * </p>
     * <p>
     * Note that the elements of the array may be either constants or {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T[] array(T... value) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(null, value);
        return (T[]) reportMatcher(isArrayMatcher(matchers), Object[].class);
    }

    /**
     * <p>
     * Matches an array of <code>boolean</code> values equal to the given array according to the rules of {@link Arrays#equals(Object[],Object[]) Arrays.equals()}.
     * </p>
     * <p>
     * Note that the elements of the array may NOT be {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public boolean[] aryEq(boolean... value) {
        return reportMatcher(arrayEqualsMatcher(value), boolean[].class);
    }

    /**
     * <p>
     * Matches an array of <code>byte</code> values equal to the given array according to the rules of {@link Arrays#equals(Object[],Object[]) Arrays.equals()}.
     * </p>
     * <p>
     * Note that the elements of the array may NOT be {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public byte[] aryEq(byte... value) {
        return reportMatcher(arrayEqualsMatcher(value), byte[].class);
    }

    /**
     * <p>
     * Matches an array of <code>char</code> values equal to the given array according to the rules of {@link Arrays#equals(Object[],Object[]) Arrays.equals()}.
     * </p>
     * <p>
     * Note that the elements of the array may NOT be {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public char[] aryEq(char... value) {
        return reportMatcher(arrayEqualsMatcher(value), char[].class);
    }

    /**
     * <p>
     * Matches an array of <code>double</code> values equal to the given array according to the rules of {@link Arrays#equals(Object[],Object[]) Arrays.equals()}.
     * </p>
     * <p>
     * Note that the elements of the array may NOT be {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public double[] aryEq(double... value) {
        return reportMatcher(arrayEqualsMatcher(value), double[].class);
    }

    /**
     * <p>
     * Matches an array of <code>float</code> values equal to the given array according to the rules of {@link Arrays#equals(Object[],Object[]) Arrays.equals()}.
     * </p>
     * <p>
     * Note that the elements of the array may NOT be {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public float[] aryEq(float... value) {
        return reportMatcher(arrayEqualsMatcher(value), float[].class);
    }

    /**
     * <p>
     * Matches an array of <code>int</code> values equal to the given array according to the rules of {@link Arrays#equals(Object[],Object[]) Arrays.equals()}.
     * </p>
     * <p>
     * Note that the elements of the array may NOT be {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public int[] aryEq(int... value) {
        return reportMatcher(arrayEqualsMatcher(value), int[].class);
    }

    /**
     * <p>
     * Matches an array of <code>long</code> values equal to the given array according to the rules of {@link Arrays#equals(Object[],Object[]) Arrays.equals()}.
     * </p>
     * <p>
     * Note that the elements of the array may NOT be {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public long[] aryEq(long... value) {
        return reportMatcher(arrayEqualsMatcher(value), long[].class);
    }

    /**
     * <p>
     * Matches an array of <code>short</code> values equal to the given array according to the rules of {@link Arrays#equals(Object[],Object[]) Arrays.equals()}.
     * </p>
     * <p>
     * Note that the elements of the array may NOT be {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    static public short[] aryEq(short... value) {
        return reportMatcher(arrayEqualsMatcher(value), short[].class);
    }

    /**
     * <p>
     * Matches an array of values equal to the given array according to the rules of {@link Arrays#equals(Object[],Object[]) Arrays.equals()}.
     * </p>
     * <p>
     * Note that the elements of the array may NOT be {@link MoxieMatchers} invocations.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T[] aryEq(T... value) {
        return (T[]) reportMatcher(arrayEqualsMatcher(value), Object[].class);
    }

    /**
     * Matches a non-primitive array containing an element matching the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T[] arrayWith(T item) {
        Matcher itemMatcher = MatcherSyntax.singleMatcherFragment(Object.class, item);
        return (T[]) reportMatcher(isArrayContainingMatcher(itemMatcher), Object[].class);
    }

    /**
     * <p>
     * Matches a non-primitive array containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * Note that the order in which the values to be matched are specified is not significant; the array merely needs to
     * contain a match for each given parameter in any order.  For a matcher where ordering is significant, use {@link MoxieMatchers#array(Object[]) array()}.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T[] arrayWithAll(T... items) {
        List<Matcher> itemMatchers = MatcherSyntax.matcherListFragment(Object.class, items);
        List<Matcher> arrayMatchers = new ArrayList<Matcher>();
        for (Matcher itemMatcher : itemMatchers) {
            arrayMatchers.add(isArrayContainingMatcher(itemMatcher));
        }
        return (T[]) reportMatcher(new AllOf(arrayMatchers), Object[].class);
    }

    /**
     * Matches an array of <code>boolean</code> containing an element matching the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @return <code>null</code>
     */
    static public boolean[] booleanArrayWith(boolean item) {
        Matcher itemMatcher = MatcherSyntax.singleMatcherFragment(boolean.class, item);
        return reportMatcher(isArrayContainingMatcher(itemMatcher), boolean[].class);
    }

    /**
     * <p>
     * Matches an array of <code>boolean</code> containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * Note that the order in which the values to be matched are specified is not significant; the array merely needs to
     * contain a match for each given parameter in any order.  For a matcher where ordering is significant, use {@link MoxieMatchers#array(boolean...) array()}.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public boolean[] booleanArrayWithAll(boolean... items) {
        List<Matcher> itemMatchers = MatcherSyntax.matcherListFragment(boolean.class, items);
        List<Matcher> arrayMatchers = new ArrayList<Matcher>();
        for (Matcher itemMatcher : itemMatchers) {
            arrayMatchers.add(isArrayContainingMatcher(itemMatcher));
        }
        return reportMatcher(new AllOf(arrayMatchers), boolean[].class);
    }

    /**
     * Matches an array of <code>byte</code> containing an element matching the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @return <code>null</code>
     */
    static public byte[] byteArrayWith(byte item) {
        Matcher itemMatcher = MatcherSyntax.singleMatcherFragment(byte.class, item);
        return reportMatcher(isArrayContainingMatcher(itemMatcher), byte[].class);
    }

    /**
     * <p>
     * Matches an array of <code>byte</code> containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * Note that the order in which the values to be matched are specified is not significant; the array merely needs to
     * contain a match for each given parameter in any order.  For a matcher where ordering is significant, use {@link MoxieMatchers#array(byte...) array()}.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public byte[] byteArrayWithAll(byte... items) {
        List<Matcher> itemMatchers = MatcherSyntax.matcherListFragment(byte.class, items);
        List<Matcher> arrayMatchers = new ArrayList<Matcher>();
        for (Matcher itemMatcher : itemMatchers) {
            arrayMatchers.add(isArrayContainingMatcher(itemMatcher));
        }
        return reportMatcher(new AllOf(arrayMatchers), byte[].class);
    }

    /**
     * Matches an array of <code>char</code> containing an element matching the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @return <code>null</code>
     */
    static public char[] charArrayWith(char item) {
        Matcher itemMatcher = MatcherSyntax.singleMatcherFragment(char.class, item);
        return reportMatcher(isArrayContainingMatcher(itemMatcher), char[].class);
    }

    /**
     * <p>
     * Matches an array of <code>char</code> containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * Note that the order in which the values to be matched are specified is not significant; the array merely needs to
     * contain a match for each given parameter in any order.  For a matcher where ordering is significant, use {@link MoxieMatchers#array(char...) array()}.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public char[] charArrayWithAll(char... items) {
        List<Matcher> itemMatchers = MatcherSyntax.matcherListFragment(char.class, items);
        List<Matcher> arrayMatchers = new ArrayList<Matcher>();
        for (Matcher itemMatcher : itemMatchers) {
            arrayMatchers.add(isArrayContainingMatcher(itemMatcher));
        }
        return reportMatcher(new AllOf(arrayMatchers), char[].class);
    }

    /**
     * Matches an array of <code>short</code> containing an element matching the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @return <code>null</code>
     */
    static public short[] shortArrayWith(short item) {
        Matcher itemMatcher = MatcherSyntax.singleMatcherFragment(short.class, item);
        return reportMatcher(isArrayContainingMatcher(itemMatcher), short[].class);
    }

    /**
     * <p>
     * Matches an array of <code>short</code> containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * Note that the order in which the values to be matched are specified is not significant; the array merely needs to
     * contain a match for each given parameter in any order.  For a matcher where ordering is significant, use {@link MoxieMatchers#array(short...) array()}.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public short[] shortArrayWithAll(short... items) {
        List<Matcher> itemMatchers = MatcherSyntax.matcherListFragment(short.class, items);
        List<Matcher> arrayMatchers = new ArrayList<Matcher>();
        for (Matcher itemMatcher : itemMatchers) {
            arrayMatchers.add(isArrayContainingMatcher(itemMatcher));
        }
        return reportMatcher(new AllOf(arrayMatchers), short[].class);
    }

    /**
     * Matches an array of <code>int</code> containing an element matching the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @return <code>null</code>
     */
    static public int[] intArrayWith(int item) {
        Matcher itemMatcher = MatcherSyntax.singleMatcherFragment(int.class, item);
        return reportMatcher(isArrayContainingMatcher(itemMatcher), int[].class);
    }

    /**
     * <p>
     * Matches an array of <code>int</code> containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * Note that the order in which the values to be matched are specified is not significant; the array merely needs to
     * contain a match for each given parameter in any order.  For a matcher where ordering is significant, use {@link MoxieMatchers#array(int...) array()}.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public int[] intArrayWithAll(int... items) {
        List<Matcher> itemMatchers = MatcherSyntax.matcherListFragment(int.class, items);
        List<Matcher> arrayMatchers = new ArrayList<Matcher>();
        for (Matcher itemMatcher : itemMatchers) {
            arrayMatchers.add(isArrayContainingMatcher(itemMatcher));
        }
        return reportMatcher(new AllOf(arrayMatchers), int[].class);
    }

    /**
     * Matches an array of <code>long</code> containing an element matching the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @return <code>null</code>
     */
    static public long[] longArrayWith(long item) {
        Matcher itemMatcher = MatcherSyntax.singleMatcherFragment(long.class, item);
        return reportMatcher(isArrayContainingMatcher(itemMatcher), long[].class);
    }

    /**
     * <p>
     * Matches an array of <code>long</code> containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * Note that the order in which the values to be matched are specified is not significant; the array merely needs to
     * contain a match for each given parameter in any order.  For a matcher where ordering is significant, use {@link MoxieMatchers#array(long...)  array()}.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public long[] longArrayWithAll(long... items) {
        List<Matcher> itemMatchers = MatcherSyntax.matcherListFragment(long.class, items);
        List<Matcher> arrayMatchers = new ArrayList<Matcher>();
        for (Matcher itemMatcher : itemMatchers) {
            arrayMatchers.add(isArrayContainingMatcher(itemMatcher));
        }
        return reportMatcher(new AllOf(arrayMatchers), long[].class);
    }

    /**
     * Matches an array of <code>float</code> containing an element matching the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @return <code>null</code>
     */
    static public float[] floatArrayWith(float item) {
        Matcher itemMatcher = MatcherSyntax.singleMatcherFragment(float.class, item);
        return reportMatcher(isArrayContainingMatcher(itemMatcher), float[].class);
    }

    /**
     * <p>
     * Matches an array of <code>float</code> containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * Note that the order in which the values to be matched are specified is not significant; the array merely needs to
     * contain a match for each given parameter in any order.  For a matcher where ordering is significant, use {@link MoxieMatchers#array(float...) array()}.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public float[] floatArrayWithAll(float... items) {
        List<Matcher> itemMatchers = MatcherSyntax.matcherListFragment(float.class, items);
        List<Matcher> arrayMatchers = new ArrayList<Matcher>();
        for (Matcher itemMatcher : itemMatchers) {
            arrayMatchers.add(isArrayContainingMatcher(itemMatcher));
        }
        return reportMatcher(new AllOf(arrayMatchers), float[].class);
    }

    /**
     * Matches an array of <code>double</code> containing an element matching the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @return <code>null</code>
     */
    static public double[] doubleArrayWith(double item) {
        Matcher itemMatcher = MatcherSyntax.singleMatcherFragment(double.class, item);
        return reportMatcher(isArrayContainingMatcher(itemMatcher), double[].class);
    }

    /**
     * <p>
     * Matches an array of <code>double</code> containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * Note that the order in which the values to be matched are specified is not significant; the array merely needs to
     * contain a match for each given parameter in any order.  For a matcher where ordering is significant, use {@link MoxieMatchers#array(double...) array()}.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public double[] doubleArrayWithAll(double... items) {
        List<Matcher> itemMatchers = MatcherSyntax.matcherListFragment(double.class, items);
        List<Matcher> arrayMatchers = new ArrayList<Matcher>();
        for (Matcher itemMatcher : itemMatchers) {
            arrayMatchers.add(isArrayContainingMatcher(itemMatcher));
        }
        return reportMatcher(new AllOf(arrayMatchers), double[].class);
    }

    /**
     * Matches a {@link java.util.Collection Collection} containing an element matching the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @param item             Item to be found in the collection (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T extends Iterable> T collectionWith(Object item) {
        return (T) collectionWith(Iterable.class, item);
    }

    /**
     * <p>
     * Matches a {@link java.util.Collection Collection} containing an element matching the given parameter (which may be a {@link MoxieMatchers} invocation).
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param collectionClass  Type of the collection to be matched
     * @param item             Item to be found in the collection (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T extends Iterable> T collectionWith(Class<T> collectionClass, Object item) {
        Matcher itemMatcher = MatcherSyntax.singleMatcherFragment(Object.class, item);
        return reportMatcher(Matchers.hasItem(itemMatcher), collectionClass);
    }

    /**
     * <p>
     * Matches a {@link java.util.Collection Collection} containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * Note that the order in which the values to be matched are specified is not significant; the collection merely needs to
     * contain a match for each given parameter in any order.  For a matcher where ordering is significant, use {@link MoxieMatchers#collection(Object[]) collection()}.
     * </p>
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T extends Iterable> T collectionWithAll(Object... items) {
        return (T) collectionWithAll(Iterable.class, items);
    }

    /**
     * <p>
     * Matches a {@link java.util.Collection Collection} containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     * <p>
     * Note that the order in which the values to be matched are specified is not significant; the collection merely needs to
     * contain a match for each given parameter in any order.  For a matcher where ordering is significant, use {@link MoxieMatchers#collection(Object[]) collection()}.
     * </p>
     *
     * @param collectionClass  Type of the collection to be matched
     * @param items            Items to be found in the collection (raw values or {@link MoxieMatchers} invocations)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T extends Iterable> T collectionWithAll(Class<T> collectionClass, Object... items) {
        List<Matcher> itemMatchers = MatcherSyntax.matcherListFragment(Object.class, items);
        List<Matcher> arrayMatchers = new ArrayList<Matcher>();
        for (Matcher itemMatcher : itemMatchers) {
            arrayMatchers.add(Matchers.hasItem(itemMatcher));
        }
        return reportMatcher(new AllOf(arrayMatchers), collectionClass);
    }

    /**
     * <p>
     * Matches a {@link java.util.Collection Collection} or {@link java.lang.Iterable Iterable} containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations), in order.
     * </p>
     *
     * @param items            Values to be found in the collection (raw values or {@link MoxieMatchers} invocations)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T extends Iterable> T collection(Object... items) {
        return (T) collection(Iterable.class, items);
    }

    /**
     * <p>
     * Matches a {@link java.util.Collection Collection} or {@link java.lang.Iterable Iterable} containing elements matching the given parameters (which may be {@link MoxieMatchers} invocations), in order.
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param collectionClass  Type of the collection to be matched
     * @param items            Values to be found in the collection (raw values or {@link MoxieMatchers} invocations)
     * @return <code>null</code>
     */
    static public <T extends Iterable> T collection(Class<T> collectionClass, Object... items) {
        final List<Matcher> itemMatchers = MatcherSyntax.matcherListFragment(Object.class, items);
        return reportMatcher(new BaseMatcher() {
            public boolean matches(Object o) {
                @SuppressWarnings("unchecked")
                Iterator<T> itemsIter = ((Iterable<T>) o).iterator();
                Iterator<Matcher> matchersIter = itemMatchers.iterator();
                while (itemsIter.hasNext() && matchersIter.hasNext()) {
                    if (!matchersIter.next().matches(itemsIter.next())) {
                        return false;
                    }
                }
                return itemsIter.hasNext() == matchersIter.hasNext();
            }

            public void describeTo(Description description) {
                description.appendList("a collection with elements matching: [", ", ", "]", itemMatchers);
            }
        }, collectionClass);
    }

    /**
     * Matches a {@link java.util.Map Map} containing an entry whose key and value match the given parameters (which may be {@link MoxieMatchers} invocations).
     *
     * @param key      Key of the desired entry (raw value or {@link MoxieMatchers} invocation)
     * @param value    Value of the desired entry (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <M extends Map> M mapWithEntry(Object key, Object value) {
        return (M) mapWithEntry(Map.class, key, value);
    }

    /**
     * <p>
     * Matches a {@link java.util.Map Map} containing an entry whose key and value match the given parameters (which may be {@link MoxieMatchers} invocations).
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param mapClass Type of the map to be matched
     * @param key      Key of the desired entry (raw value or {@link MoxieMatchers} invocation)
     * @param value    Value of the desired entry (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <M extends Map> M mapWithEntry(Class<M> mapClass, Object key, Object value) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(Object.class, Arrays.asList(key, value));
        Matcher keyMatcher = matchers.remove(0);
        Matcher valueMatcher = matchers.remove(0);
        return reportMatcher(Matchers.hasEntry(keyMatcher, valueMatcher), mapClass);
    }

    /**
     * Matches a {@link java.util.Map Map} containing an entry whose key matches the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @param key      Key to be found in the map (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <M extends Map> M mapWithKey(Object key) {
        return (M) mapWithKey(Map.class, key);
    }

    /**
     * <p>
     * Matches a {@link java.util.Map Map} containing an entry whose key matches the given parameter (which may be a {@link MoxieMatchers} invocation).
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param mapClass Type of the map to be matched
     * @param key      Key to be found in the map (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <M extends Map> M mapWithKey(Class<M> mapClass, Object key) {
        Matcher keyMatcher = MatcherSyntax.singleMatcherFragment(Object.class, key);
        return reportMatcher(Matchers.hasKey(keyMatcher), mapClass);
    }

    /**
     * Matches a {@link java.util.Map Map} containing an entry whose value matches the given parameter (which may be a {@link MoxieMatchers} invocation).
     *
     * @param value    Value to be found in the map (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <M extends Map> M mapWithValue(Object value) {
        return (M) mapWithValue(Map.class, value);
    }

    /**
     * <p>
     * Matches a {@link java.util.Map Map} containing an entry whose value matches the given parameter (which may be a {@link MoxieMatchers} invocation).
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param mapClass Type of the map to be matched
     * @param value    Value to be found in the map (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <M extends Map> M mapWithValue(Class<M> mapClass, Object value) {
        Matcher valueMatcher = MatcherSyntax.singleMatcherFragment(Object.class, value);
        return reportMatcher(Matchers.hasValue(valueMatcher), mapClass);
    }


    /**
     * Matches an object having a JavaBeans-style getter method exposing a property with the given name, which returns the given value (which may be a {@link MoxieMatchers} invocation).
     *
     * @param propertyName  Name of the property - for example, a property name of "count" implies a getter named <code>getCount()</code>
     * @param value         Desired value of the property (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T hasProperty(String propertyName, Object value) {
        return (T) hasProperty(Object.class, propertyName, value);
    }

    /**
     * <p>
     * Matches an object having a JavaBeans-style getter method exposing a property with the given name, which returns the given value (which may be a {@link MoxieMatchers} invocation).
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param clazz         Type of the object on which to look for the property
     * @param propertyName  Name of the property to match - for example, a property name of "count" implies a getter named <code>getCount()</code>
     * @param value         Desired value of the property (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    static public <T> T hasProperty(Class<T> clazz, String propertyName, Object value) {
        Matcher valueMatcher = MatcherSyntax.singleMatcherFragment(Object.class, value);
        return reportMatcher(Matchers.hasProperty(propertyName, valueMatcher), clazz);
    }

    /**
     * Matches the given string, ignoring any white space.
     *
     * @return <code>null</code>
     */
    static public String eqIgnoreWhiteSpace(String value) {
        return reportMatcher(Matchers.equalToIgnoringWhiteSpace(value), String.class);
    }

    /**
     * Syntactic sugar matcher; used when matching varargs methods to specify that we don't care what parameters are received in the varargs list.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T[] anyVarargs() {
        return (T[]) anyVarargs(Object.class);
    }

    /**
     * Syntactic sugar matcher; used when matching varargs methods to specify that we don't care what parameters are received in the varargs list.
     *
     * @param clazz Constituent type of the varargs array
     * @return <code>null</code>
     */
    static public <T> T[] anyVarargs(Class<T> clazz) {
        return any(MoxieUtils.arrayClassFor(clazz));
    }

    /**
     * Syntactic sugar matcher; used when matching varargs methods to specify that we don't care what parameters are received in the varargs list.
     *
     * @return <code>null</code>
     */
    static public boolean[] anyBooleanVarargs() {
        return any(boolean[].class);
    }

    /**
     * Syntactic sugar matcher; used when matching varargs methods to specify that we don't care what parameters are received in the varargs list.
     *
     * @return <code>null</code>
     */
    static public byte[] anyByteVarargs() {
        return any(byte[].class);
    }

    /**
     * Syntactic sugar matcher; used when matching varargs methods to specify that we don't care what parameters are received in the varargs list.
     *
     * @return <code>null</code>
     */
    static public char[] anyCharVarargs() {
        return any(char[].class);
    }

    /**
     * Syntactic sugar matcher; used when matching varargs methods to specify that we don't care what parameters are received in the varargs list.
     *
     * @return <code>null</code>
     */
    static public short[] anyShortVarargs() {
        return any(short[].class);
    }

    /**
     * Syntactic sugar matcher; used when matching varargs methods to specify that we don't care what parameters are received in the varargs list.
     *
     * @return <code>null</code>
     */
    static public int[] anyIntVarargs() {
        return any(int[].class);
    }

    /**
     * Syntactic sugar matcher; used when matching varargs methods to specify that we don't care what parameters are received in the varargs list.
     *
     * @return <code>null</code>
     */
    static public long[] anyLongVarargs() {
        return any(long[].class);
    }

    /**
     * Syntactic sugar matcher; used when matching varargs methods to specify that we don't care what parameters are received in the varargs list.
     *
     * @return <code>null</code>
     */
    static public float[] anyFloatVarargs() {
        return any(float[].class);
    }

    /**
     * Syntactic sugar matcher; used when matching varargs methods to specify that we don't care what parameters are received in the varargs list.
     *
     * @return <code>null</code>
     */
    static public double[] anyDoubleVarargs() {
        return any(double[].class);
    }

    /**
     * Matches a {@link Collection} of the specified size.  (The length may be a {@link MoxieMatchers} invocation.)
     *
     * @param size             Desired size of the collection (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <C extends Collection> C collectionSize(int size) {
        return (C) collectionSize(Collection.class, size);
    }

    /**
     * <p>
     * Matches a {@link Collection} of the specified size.  (The length may be a {@link MoxieMatchers} invocation.)
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param collectionClass  Type of the expected collection
     * @param size             Desired size of the collection (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    static public <C extends Collection> C collectionSize(Class<C> collectionClass, int size) {
        final Matcher sizeMatcher = MatcherSyntax.singleMatcherFragment(Integer.TYPE, size);
        return reportMatcher(new BaseMatcher() {
            public boolean matches(Object o) {
                return o != null && sizeMatcher.matches(((Collection) o).size());
            }

            public void describeTo(Description description) {
                description.appendText("a collection with size ");
                sizeMatcher.describeTo(description);
            }
        }, collectionClass);
    }

    /**
     * Matches a {@link Map} of the specified size.  (The length may be a {@link MoxieMatchers} invocation.)
     *
     * @param size      Desired size of the map (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <M extends Map> M mapSize(int size) {
        return (M) mapSize(Map.class, size);
    }

    /**
     * <p>
     * Matches a {@link Map} of the specified size.  (The length may be a {@link MoxieMatchers} invocation.)
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param mapClass  Type of the expected map
     * @param size      Desired size of the map (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    static public <M extends Map> M mapSize(Class<M> mapClass, int size) {
        final Matcher sizeMatcher = MatcherSyntax.singleMatcherFragment(Integer.TYPE, size);
        return reportMatcher(new BaseMatcher() {
            public boolean matches(Object o) {
                return o != null && sizeMatcher.matches(((Map) o).size());
            }

            public void describeTo(Description description) {
                description.appendText("a map with size ");
                sizeMatcher.describeTo(description);
            }
        }, mapClass);
    }

    /**
     * Matches a non-primitive array of the specified length.  (The length may be a {@link MoxieMatchers} invocation.)
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T[] arrayLength(int size) {
        return (T[]) arrayLength(Object.class, size);
    }

    /**
     * <p>
     * Matches a non-primitive array of the specified length.  (The length may be a {@link MoxieMatchers} invocation.)
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @param clazz  Constituent type of the array
     * @param size   Desired size of the array (raw value or {@link MoxieMatchers} invocation)
     * @return <code>null</code>
     */
    static public <T> T[] arrayLength(Class<T> clazz, int size) {
        final Matcher sizeMatcher = MatcherSyntax.singleMatcherFragment(Integer.TYPE, size);
        return reportMatcher(arraySizeMatcher(sizeMatcher), MoxieUtils.arrayClassFor(clazz));
    }

    /**
     * Matches a <code>boolean</code> array of the specified length.  (The length may be a {@link MoxieMatchers} invocation.)
     *
     * @return <code>null</code>
     */
    static public boolean[] booleanArrayLength(int size) {
        final Matcher sizeMatcher = MatcherSyntax.singleMatcherFragment(Integer.TYPE, size);
        return reportMatcher(arraySizeMatcher(sizeMatcher), boolean[].class);
    }

    /**
     * Matches a <code>byte</code> array of the specified length.  (The length may be a {@link MoxieMatchers} invocation.)
     *
     * @return <code>null</code>
     */
    static public byte[] byteArrayLength(int size) {
        final Matcher sizeMatcher = MatcherSyntax.singleMatcherFragment(Integer.TYPE, size);
        return reportMatcher(arraySizeMatcher(sizeMatcher), byte[].class);
    }

    /**
     * Matches a <code>char</code> array of the specified length.  (The length may be a {@link MoxieMatchers} invocation.)
     *
     * @return <code>null</code>
     */
    static public char[] charArrayLength(int size) {
        final Matcher sizeMatcher = MatcherSyntax.singleMatcherFragment(Integer.TYPE, size);
        return reportMatcher(arraySizeMatcher(sizeMatcher), char[].class);
    }

    /**
     * Matches a <code>short</code> array of the specified length.  (The length may be a {@link MoxieMatchers} invocation.)
     *
     * @return <code>null</code>
     */
    static public short[] shortArrayLength(int size) {
        final Matcher sizeMatcher = MatcherSyntax.singleMatcherFragment(Integer.TYPE, size);
        return reportMatcher(arraySizeMatcher(sizeMatcher), short[].class);
    }

    /**
     * Matches an <code>int</code> array of the specified length.  (The length may be a {@link MoxieMatchers} invocation.)
     *
     * @return <code>null</code>
     */
    static public int[] intArrayLength(int size) {
        final Matcher sizeMatcher = MatcherSyntax.singleMatcherFragment(Integer.TYPE, size);
        return reportMatcher(arraySizeMatcher(sizeMatcher), int[].class);
    }

    /**
     * Matches a <code>long</code> array of the specified length.  (The length may be a {@link MoxieMatchers} invocation.)
     *
     * @return <code>null</code>
     */
    static public long[] longArrayLength(int size) {
        final Matcher sizeMatcher = MatcherSyntax.singleMatcherFragment(Integer.TYPE, size);
        return reportMatcher(arraySizeMatcher(sizeMatcher), long[].class);
    }

    /**
     * Matches a <code>float</code> array of the specified length.  (The length may be a {@link MoxieMatchers} invocation.)
     *
     * @return <code>null</code>
     */
    static public float[] floatArrayLength(int size) {
        final Matcher sizeMatcher = MatcherSyntax.singleMatcherFragment(Integer.TYPE, size);
        return reportMatcher(arraySizeMatcher(sizeMatcher), float[].class);
    }

    /**
     * Matches a <code>double</code> array of the specified length.  (The length may be a {@link MoxieMatchers} invocation.)
     *
     * @return <code>null</code>
     */
    static public double[] doubleArrayLength(int size) {
        final Matcher sizeMatcher = MatcherSyntax.singleMatcherFragment(Integer.TYPE, size);
        return reportMatcher(arraySizeMatcher(sizeMatcher), double[].class);
    }

    /**
     * Special matcher used to capture method parameters for later inspection in your tests.
     * When this matcher is run, the matcher will add the value encountered to the given collection, then exit successfully.
     *
     * @return <code>null</code>
     */
    @SuppressWarnings("unchecked")
    static public <T> T captureTo(Collection<T> destination) {
        return (T) captureTo(Object.class, (Collection) destination);
    }

    /**
     * <p>
     * Special matcher used to capture method parameters for later inspection in your tests.
     * When this matcher is run, the matcher will add the value encountered to the given collection, then exit successfully.
     * </p>
     * <p>
     * The class argument to this method is not matched against the value passed to the mocked method; it is provided
     * as a convenient way of specifying the type parameter for those who wish to statically import this method.
     * </p>
     *
     * @return <code>null</code>
     */
    static public <T> T captureTo(Class<T> clazz, Collection<? super T> destination) {
        return reportMatcher(captureMatcher(destination), clazz);
    }

    /**
     * Special matcher used to capture method parameters for later inspection in your tests.
     * When this matcher is run, the matcher will add the value encountered to the given collection, then exit successfully.
     *
     * @return <code>false</code>
     */
    static public boolean captureBooleanTo(Collection<? super Boolean> destination) {
        return captureTo(Boolean.TYPE, destination);
    }

    /**
     * Special matcher used to capture method parameters for later inspection in your tests.
     * When this matcher is run, the matcher will add the value encountered to the given collection, then exit successfully.
     *
     * @return <code>0</code>
     */
    static public byte captureByteTo(Collection<? super Byte> destination) {
        return captureTo(Byte.TYPE, destination);
    }

    /**
     * Special matcher used to capture method parameters for later inspection in your tests.
     * When this matcher is run, the matcher will add the value encountered to the given collection, then exit successfully.
     *
     * @return <code>'\0'</code>
     */
    static public char captureCharTo(Collection<? super Character> destination) {
        return captureTo(Character.TYPE, destination);
    }

    /**
     * Special matcher used to capture method parameters for later inspection in your tests.
     * When this matcher is run, the matcher will add the value encountered to the given collection, then exit successfully.
     *
     * @return <code>0</code>
     */
    static public double captureDoubleTo(Collection<? super Double> destination) {
        return captureTo(Double.TYPE, destination);
    }

    /**
     * Special matcher used to capture method parameters for later inspection in your tests.
     * When this matcher is run, the matcher will add the value encountered to the given collection, then exit successfully.
     *
     * @return <code>0</code>
     */
    static public float captureFloatTo(Collection<? super Float> destination) {
        return captureTo(Float.TYPE, destination);
    }

    /**
     * Special matcher used to capture method parameters for later inspection in your tests.
     * When this matcher is run, the matcher will add the value encountered to the given collection, then exit successfully.
     *
     * @return <code>0</code>
     */
    static public int captureIntTo(Collection<? super Integer> destination) {
        return captureTo(Integer.TYPE, destination);
    }

    /**
     * Special matcher used to capture method parameters for later inspection in your tests.
     * When this matcher is run, the matcher will add the value encountered to the given collection, then exit successfully.
     *
     * @return <code>0</code>
     */
    static public long captureLongTo(Collection<? super Long> destination) {
        return captureTo(Long.TYPE, destination);
    }

    /**
     * Special matcher used to capture method parameters for later inspection in your tests.
     * When this matcher is run, the matcher will add the value encountered to the given collection, then exit successfully.
     *
     * @return <code>0</code>
     */
    static public short captureShortTo(Collection<? super Short> destination) {
        return captureTo(Short.TYPE, destination);
    }

    ////////////////////////////////////////////////////////////////

    private static <T> Matcher captureMatcher(final Collection<T> captureTo) {
        return new BaseMatcher() {
            @SuppressWarnings("unchecked")
            public boolean matches(Object o) {
                captureTo.add((T) o);
                return true;
            }

            public void describeTo(Description description) {
                description.appendText("capture this argument");
            }
        };
    }

    private static BaseMatcher arraySizeMatcher(final Matcher sizeMatcher) {
        return new BaseMatcher() {
            public boolean matches(Object o) {
                return o != null && sizeMatcher.matches(Array.getLength(o));
            }

            public void describeTo(Description description) {
                description.appendText("an array with length ");
                sizeMatcher.describeTo(description);
            }
        };
    }

    // Hamcrest's IsArray matcher doesn't look as if it works on primitive arrays.
    static Matcher isArrayMatcher(final List<Matcher> elementMatchers) {
        return new BaseMatcher() {
            public boolean matches(Object o) {
                if (o == null || !o.getClass().isArray()) {
                    return false;
                }
                int arraySize = Array.getLength(o);
                if (elementMatchers.size() != arraySize) {
                    return false;
                }
                for (int i = 0; i < arraySize; i++) {
                    if (!elementMatchers.get(i).matches(Array.get(o, i))) {
                        return false;
                    }
                }
                return true;
            }

            public void describeTo(Description description) {
                description.appendList("[", ", ", "]", elementMatchers);
            }
        };
    }

    // Hamcrest's IsArrayContaining matcher doesn't look as if it works on primitive arrays.
    static Matcher isArrayContainingMatcher(final Matcher elementMatcher) {
        return new BaseMatcher() {
            public boolean matches(Object o) {
                if (o == null || !o.getClass().isArray()) {
                    return false;
                }
                int arraySize = Array.getLength(o);
                for (int i = 0; i < arraySize; i++) {
                    if (elementMatcher.matches(Array.get(o, i))) {
                        return true;
                    }
                }
                return false;
            }

            public void describeTo(Description description) {
                description.appendText("an array containing ");
                elementMatcher.describeTo(description);
            }
        };
    }

    static Matcher arrayEqualsMatcher(Object arrayValue) {
        final List elementsList = MoxieUtils.listFromArray(arrayValue);
        return new BaseMatcher() {
            public boolean matches(Object o) {
                if (o == null || !o.getClass().isArray()) {
                    return false;
                }
                List oList = MoxieUtils.listFromArray(o);
                return elementsList.equals(oList);
            }

            @SuppressWarnings("unchecked")
            public void describeTo(Description description) {
                description.appendValueList("[", ", ", "]", elementsList);
            }
        };
    }

    // Hamcrest's IsCloseTo matcher only works on Doubles.

    static Matcher isCloseMatcher(final double value, final double delta) {
        return new BaseMatcher() {
            public boolean matches(Object o) {
                if (o == null || !(o instanceof Number)) {
                    return false;
                }
                return Math.abs(((Number) o).doubleValue() - value) <= delta;
            }

            public void describeTo(Description description) {
                description.appendText("a Number within ").appendValue(delta).appendText(" of ").appendValue(value);
            }
        };
    }

    // Hamcrest's OrderingComparisons class appears broken.

    static Matcher comparisonMatcher(final Comparable value, final boolean lessThan, final boolean equalTo, final boolean greaterThan) {
        return new BaseMatcher() {

            public void describeTo(Description description) {
                description.appendText("a value ");
                if (lessThan) {
                    description.appendText("less than ");
                }
                if (equalTo) {
                    if (lessThan) {
                        description.appendText("or ");
                    }
                    description.appendText("equal to ");
                }
                if (greaterThan) {
                    if (lessThan || equalTo) {
                        description.appendText("or ");
                    }
                    description.appendText("greater than ");
                }
                description.appendValue(value);
            }

            public boolean matches(Object o) {
                if (o == null) {
                    return false;
                }
                @SuppressWarnings("unchecked")
                int result = value.compareTo(o);
                return (result > 0 && lessThan) ||
                        (result == 0 && equalTo) ||
                        (result < 0 && greaterThan);
            }
        };
    }

    static Matcher greaterThanOrEqualTo(Comparable value) {
        return comparisonMatcher(value, false, true, true);
    }

    static Matcher greaterThan(Comparable value) {
        return comparisonMatcher(value, false, false, true);
    }

    static Matcher lessThanOrEqualTo(Comparable value) {
        return comparisonMatcher(value, true, true, false);
    }

    static Matcher lessThan(Comparable value) {
        return comparisonMatcher(value, true, false, false);
    }

    /**
     * <p>
     * Registers a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} with Moxie's magical parameter-matching mechanism.
     * </p>
     * <p>
     * See the discussion in the summary javadoc for the {@link MoxieMatchers} class for more details.
     * </p>
     *
     * @param matcher      a Hamcrest {@link Matcher} to be registered
     * @param expectedType optional - type of the expected parameter (particularly important if matching a primitive parameter)
     * @param <T>          type of the expected parameter
     * @return <code>null</code> if <code>expectedType</code> is an object, or the primitive's default value if <code>expectedType</code> is a primitive
     */
    static public <T> T reportMatcher(Matcher matcher, Class<T> expectedType) {
        getMatcherReports().add(new MatcherReport(matcher, expectedType));
        return MoxieUtils.defaultValue(expectedType);
    }

    static LinkedList<MatcherReport> getMatcherReports() {
        LinkedList<MatcherReport> matcherList = matchers.get();
        if (matcherList == null) {
            matcherList = new LinkedList<MatcherReport>();
            matchers.set(matcherList);
        }
        return matcherList;
    }


}
