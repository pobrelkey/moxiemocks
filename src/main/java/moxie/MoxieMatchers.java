/*
 * Copyright (c) 2010 Moxie contributors
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
import org.hamcrest.core.AllOf;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.IsAnything;
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
import java.util.Arrays;
import java.util.List;
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

    MoxieMatchers() {
    }

    /**
     * Matches any array.
     *
     * @param <T> element type of the array
     * @return <code>null</code>
     */
    static public <T> T[] anyArray() {
        return (T[]) Moxie.reportMatcher(new AnyOf(Arrays.asList(new IsNull(), ANY_ARRAY_MATCHER)), Object[].class);
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
    static public <T> T anything() {
        return (T) any(Object.class);
    }

    /**
     * Synonym for {@link #anything}.
     *
     * @return <code>null</code>
     */
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
    static public <T> T any(Class<T> clazz) {
        return Moxie.reportMatcher(new AnyOf(Arrays.asList(new IsNull(), new IsInstanceOf(MoxieUtils.toNonPrimitive(clazz)))), clazz);
    }

    /**
     * Matches an object parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>null</code>
     */
    static public <T> T argThat(Matcher<T> matcher) {
        return (T) Moxie.reportMatcher(matcher, null);
    }

    /**
     * Matches an array parameter or varargs array using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>null</code>
     */
    static public <T> T arrayThat(Matcher<T> matcher) {
        return (T) Moxie.reportMatcher(matcher, Object[].class);
    }

    /**
     * Matches a <code>boolean</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>false</code>
     */
    static public boolean booleanThat(Matcher<Boolean> matcher) {
        return Moxie.reportMatcher(matcher, Boolean.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public byte byteThat(Matcher<Byte> matcher) {
        return Moxie.reportMatcher(matcher, Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>'\0'</code>
     */
    static public char charThat(Matcher<Character> matcher) {
        return Moxie.reportMatcher(matcher, Character.TYPE);
    }

    /**
     * Matches a <code>double</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public double doubleThat(Matcher<Double> matcher) {
        return Moxie.reportMatcher(matcher, Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public float floatThat(Matcher<Float> matcher) {
        return Moxie.reportMatcher(matcher, Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public int intThat(Matcher<Integer> matcher) {
        return Moxie.reportMatcher(matcher, Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public long longThat(Matcher<Long> matcher) {
        return Moxie.reportMatcher(matcher, Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter using the given custom matcher.
     *
     * @param matcher a <a href="http://code.google.com/p/hamcrest/">Hamcrest</a> {@link Matcher} to which the argument will be passed
     * @return <code>0</code>
     */
    static public short shortThat(Matcher<Short> matcher) {
        return Moxie.reportMatcher(matcher, Short.TYPE);
    }

    /**
     * Matches a <code>boolean</code> parameter equal to the given value.
     *
     * @return <code>false</code>
     */
    static public boolean eq(boolean value) {
        return Moxie.reportMatcher(new IsEqual(value), Boolean.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    static public byte eq(byte value) {
        return Moxie.reportMatcher(new IsEqual(value), Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter equal to the given value.
     *
     * @return <code>'\0'</code>
     */
    static public char eq(char value) {
        return Moxie.reportMatcher(new IsEqual(value), Character.TYPE);
    }

    /**
     * Matches a <code>double</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    static public double eq(double value) {
        return Moxie.reportMatcher(new IsEqual(value), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    static public float eq(float value) {
        return Moxie.reportMatcher(new IsEqual(value), Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    static public int eq(int value) {
        return Moxie.reportMatcher(new IsEqual(value), Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    static public long eq(long value) {
        return Moxie.reportMatcher(new IsEqual(value), Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter equal to the given value.
     *
     * @return <code>0</code>
     */
    static public short eq(short value) {
        return Moxie.reportMatcher(new IsEqual(value), Short.TYPE);
    }

    /**
     * Matches a parameter equal to the given value.
     *
     * @return <code>null</code>
     */
    static public <T> T eq(T value) {
        return (T) Moxie.reportMatcher(new IsEqual(value), null);
    }

    /**
     * Matches a non-<code>null</code> parameter assignable to the given class.
     *
     * @return <code>null</code>
     */
    static public <T> T isA(Class<T> clazz) {
        return (T) Moxie.reportMatcher(new IsInstanceOf(clazz), null);
    }

    /**
     * Matches any non-<code>null</code> parameter.
     *
     * @return <code>null</code>
     */
    static public Object isNotNull() {
        return Moxie.reportMatcher(new IsNot(new IsNull()), null);
    }

    /**
     * Matches only when the parameter is <code>null</code>.
     *
     * @return <code>null</code>
     */
    static public Object isNull() {
        return Moxie.reportMatcher(new IsNull(), null);
    }

    /**
     * Synonym for {@link #isNotNull}.
     *
     * @return <code>null</code>
     */
    static public Object notNull() {
        return isNotNull();
    }

    /**
     * Matches when the parameter is referentially equal to the given value.
     *
     * @return <code>null</code>
     */
    static public <T> T same(T value) {
        return (T) Moxie.reportMatcher(new IsSame(value), null);
    }

    /**
     * Matches a {@link String} parameter containing the given substring.
     *
     * @return <code>null</code>
     */
    static public String hasSubstring(String substring) {
        return Moxie.reportMatcher(new StringContains(substring), String.class);
    }

    /**
     * Matches a {@link String} parameter ending with the given suffix.
     *
     * @return <code>null</code>
     */
    static public String endsWith(String suffix) {
        return Moxie.reportMatcher(new StringEndsWith(suffix), String.class);
    }

    /**
     * Matches a {@link String} parameter starting with the given prefix.
     *
     * @return <code>null</code>
     */
    static public String startsWith(String prefix) {
        return Moxie.reportMatcher(new StringStartsWith(prefix), String.class);
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
    static public <T> T or(T... matchers) {
        return (T) reportOr(matchers, null);
    }

    private static <T> T reportAnd(Object matchValuesArray, Class<T> clazz) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(clazz, matchValuesArray);
        return Moxie.reportMatcher(new AllOf(matchers), clazz);
    }

    private static <T> T reportOr(Object matchValuesArray, Class<T> clazz) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(clazz, matchValuesArray);
        return Moxie.reportMatcher(new AnyOf(matchers), clazz);
    }

    private static <T> T reportNot(T matchValue, Class<T> clazz) {
        Matcher matcher = MatcherSyntax.singleMatcherFragment(clazz, matchValue);
        return Moxie.reportMatcher(new IsNot(matcher), clazz);
    }

    /**
     * Matches a {@link String} parameter equal to the given value ignoring case.
     *
     * @return <code>null</code>
     */
    static public String eqIgnoreCase(String value) {
        return Moxie.reportMatcher(new IsEqualIgnoringCase(value), String.class);
    }

    /**
     * Matches a parameter whose {@link String} representation matches the given regular expression pattern.
     *
     * @return <code>null</code>
     */
    static public <T> T matchesRegexp(String pattern) {
        return MoxieMatchers.<T>matchesRegexp(Pattern.compile(pattern));
    }

    /**
     * Matches a parameter whose {@link String} representation matches the given {@link Pattern}.
     *
     * @return <code>null</code>
     */
    static public <T> T matchesRegexp(final Pattern pattern) {
        return Moxie.<T>reportMatcher(new BaseMatcher() {
            public boolean matches(Object o) {
                return (o != null) && pattern.matcher(o.toString()).matches();
            }

            public void describeTo(Description description) {
                description.appendText("matches /" + pattern.pattern() + '/');
            }
        }, null);
    }

    /**
     * Matches a <code>double</code> parameter equal to <code>value</code> plus or minus <code>delta</code>.
     *
     * @return <code>0</code>
     */
    static public double eq(double value, double delta) {
        return Moxie.reportMatcher(isCloseMatcher(value, delta), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter equal to <code>value</code> plus or minus <code>delta</code>.
     *
     * @return <code>0</code>
     */
    static public float eq(float value, float delta) {
        return Moxie.reportMatcher(isCloseMatcher(value, delta), Float.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public byte geq(byte value) {
        return Moxie.reportMatcher(greaterThanOrEqualTo(value), Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter greater than or equal to the given value.
     *
     * @return <code>'\0'</code>
     */
    static public char geq(char value) {
        return Moxie.reportMatcher(greaterThanOrEqualTo(value), Character.TYPE);
    }

    /**
     * Matches a {@link Comparable} parameter greater than or equal to the given value.
     *
     * @return <code>null</code>
     */
    static public <T extends Comparable<T>> T geq(T value) {
        return (T) Moxie.reportMatcher(greaterThanOrEqualTo(value), Comparable.class);
    }

    /**
     * Matches a <code>double</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public double geq(double value) {
        return Moxie.reportMatcher(greaterThanOrEqualTo(value), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public float geq(float value) {
        return Moxie.reportMatcher(greaterThanOrEqualTo(value), Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public int geq(int value) {
        return Moxie.reportMatcher(greaterThanOrEqualTo(value), Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public long geq(long value) {
        return Moxie.reportMatcher(greaterThanOrEqualTo(value), Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter greater than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public short geq(short value) {
        return Moxie.reportMatcher(greaterThanOrEqualTo(value), Short.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public byte gt(byte value) {
        return Moxie.reportMatcher(greaterThan(value), Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter greater than the given value.
     *
     * @return <code>'\0'</code>
     */
    static public char gt(char value) {
        return Moxie.reportMatcher(greaterThan(value), Character.TYPE);
    }

    /**
     * Matches a {@link Comparable} parameter greater than the given value.
     *
     * @return <code>null</code>
     */
    static public <T extends Comparable<T>> T gt(T value) {
        return (T) Moxie.reportMatcher(greaterThan(value), Comparable.class);
    }

    /**
     * Matches a <code>double</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public double gt(double value) {
        return Moxie.reportMatcher(greaterThan(value), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public float gt(float value) {
        return Moxie.reportMatcher(greaterThan(value), Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public int gt(int value) {
        return Moxie.reportMatcher(greaterThan(value), Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public long gt(long value) {
        return Moxie.reportMatcher(greaterThan(value), Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter greater than the given value.
     *
     * @return <code>0</code>
     */
    static public short gt(short value) {
        return Moxie.reportMatcher(greaterThan(value), Short.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public byte leq(byte value) {
        return Moxie.reportMatcher(lessThanOrEqualTo(value), Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter less than or equal to the given value.
     *
     * @return <code>'\0'</code>
     */
    static public char leq(char value) {
        return Moxie.reportMatcher(lessThanOrEqualTo(value), Character.TYPE);
    }

    /**
     * Matches a {@link Comparable} parameter less than or equal to the given value.
     *
     * @return <code>null</code>
     */
    static public <T extends Comparable<T>> T leq(T value) {
        return (T) Moxie.reportMatcher(lessThanOrEqualTo(value), Comparable.class);
    }

    /**
     * Matches a <code>double</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public double leq(double value) {
        return Moxie.reportMatcher(lessThanOrEqualTo(value), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public float leq(float value) {
        return Moxie.reportMatcher(lessThanOrEqualTo(value), Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public int leq(int value) {
        return Moxie.reportMatcher(lessThanOrEqualTo(value), Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public long leq(long value) {
        return Moxie.reportMatcher(lessThanOrEqualTo(value), Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter less than or equal to the given value.
     *
     * @return <code>0</code>
     */
    static public short leq(short value) {
        return Moxie.reportMatcher(lessThanOrEqualTo(value), Short.TYPE);
    }

    /**
     * Matches a <code>byte</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public byte lt(byte value) {
        return Moxie.reportMatcher(lessThan(value), Byte.TYPE);
    }

    /**
     * Matches a <code>char</code> parameter less than the given value.
     *
     * @return <code>'\0'</code>
     */
    static public char lt(char value) {
        return Moxie.reportMatcher(lessThan(value), Character.TYPE);
    }

    /**
     * Matches a {@link Comparable} parameter less than the given value.
     *
     * @return <code>null</code>
     */
    static public <T extends Comparable<T>> T lt(T value) {
        return (T) Moxie.reportMatcher(lessThan(value), Comparable.class);
    }

    /**
     * Matches a <code>double</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public double lt(double value) {
        return Moxie.reportMatcher(lessThan(value), Double.TYPE);
    }

    /**
     * Matches a <code>float</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public float lt(float value) {
        return Moxie.reportMatcher(lessThan(value), Float.TYPE);
    }

    /**
     * Matches an <code>int</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public int lt(int value) {
        return Moxie.reportMatcher(lessThan(value), Integer.TYPE);
    }

    /**
     * Matches a <code>long</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public long lt(long value) {
        return Moxie.reportMatcher(lessThan(value), Long.TYPE);
    }

    /**
     * Matches a <code>short</code> parameter less than the given value.
     *
     * @return <code>0</code>
     */
    static public short lt(short value) {
        return Moxie.reportMatcher(lessThan(value), Short.TYPE);
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
        return Moxie.reportMatcher(isArrayMatcher(matchers), boolean[].class);
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
        return Moxie.reportMatcher(isArrayMatcher(matchers), byte[].class);
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
        return Moxie.reportMatcher(isArrayMatcher(matchers), char[].class);
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
        return Moxie.reportMatcher(isArrayMatcher(matchers), double[].class);
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
        return Moxie.reportMatcher(isArrayMatcher(matchers), float[].class);
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
        return Moxie.reportMatcher(isArrayMatcher(matchers), int[].class);
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
        return Moxie.reportMatcher(isArrayMatcher(matchers), long[].class);
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
        return Moxie.reportMatcher(isArrayMatcher(matchers), short[].class);
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
    static public <T> T[] array(T... value) {
        List<Matcher> matchers = MatcherSyntax.matcherListFragment(null, value);
        return (T[]) Moxie.reportMatcher(isArrayMatcher(matchers), Object[].class);
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
        return Moxie.reportMatcher(arrayEqualsMatcher(value), boolean[].class);
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
        return Moxie.reportMatcher(arrayEqualsMatcher(value), byte[].class);
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
        return Moxie.reportMatcher(arrayEqualsMatcher(value), char[].class);
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
        return Moxie.reportMatcher(arrayEqualsMatcher(value), double[].class);
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
        return Moxie.reportMatcher(arrayEqualsMatcher(value), float[].class);
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
        return Moxie.reportMatcher(arrayEqualsMatcher(value), int[].class);
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
        return Moxie.reportMatcher(arrayEqualsMatcher(value), long[].class);
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
        return Moxie.reportMatcher(arrayEqualsMatcher(value), short[].class);
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
    static public <T> T[] aryEq(T... value) {
        return (T[]) Moxie.reportMatcher(arrayEqualsMatcher(value), Object[].class);
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

}
