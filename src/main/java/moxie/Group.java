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

/**
 * <p>
 * Represents an ordered list of expectations that should occur in a strict order across mocks; or,
 * an unordered set of expectations which should be separately verifiable as complete.
 * </p>
 * <p>&nbsp;</p>
 * <h2>Creation</h2>
 * <p>
 * You can create a <code>Group</code> by calling {@link MoxieControl#group(MoxieOptions...) MoxieControl.group()}.
 * </p>
 * <p>
 * By passing {@link MoxieOptions options} to this method, you can create <code>Group</code>s of two different types:
 * </p>
 * <ul>
 * <li>
 * {@link MoxieOptions#ORDERED} <code>Group</code>s can be used to tie together expectations across mocks that
 * should be fulfilled in a certain sequence.
 * </li>
 * <li>
 * {@link MoxieOptions#UNORDERED} <code>Group</code>s can be used to identify a set of expectations that one would
 * like to verify as having been fulfilled at some intermediate stage of the test.
 * </li>
 * </ul>
 * <p>
 * The default is to create {@link MoxieOptions#ORDERED} groups.
 * </p>
 * <p>&nbsp;</p>
 * <h2>Usage in expectation-driven tests</h2>
 * <p>
 * Use the {@link Expectation#inGroup(Group...) Expectation.inGroup()} method to associate an expectation
 * with a <code>Group</code>.
 * </p>
 * <p>
 * Example using {@link MoxieOptions#ORDERED} <code>Group</code>s:
 * </p>
 * <pre>
 * // Given these expectations...
 * List&lt;String&gt; presidents = Moxie.mock(List.class);
 * List&lt;String&gt; stooges = Moxie.mock(List.class);
 * Group sequence = Moxie.group();
 * Moxie.expect(presidents).inGroup(sequence).willReturn("Washington").on().get(0);
 * Moxie.expect(stooges).inGroup(sequence).willReturn("Moe").on().get(0);
 * Moxie.expect(presidents).inGroup(sequence).willReturn("Kennedy").on().get(34);
 * &nbsp;
 * // ...this will pass...
 * presidents.get(0);
 * stooges.get(0);
 * presidents.get(34);
 * &nbsp;
 * // ...but any other sequence of these calls will fail, e.g.:
 * stooges.get(0);
 * presidents.get(34);
 * presidents.get(0);
 * </pre>
 * <p>
 * Note that you can set a number of times you expect an {@link MoxieOptions#ORDERED} <code>Group</code>
 * to execute beginning-to-end using the {@link #willBeCalled()} method:
 * </p>
 * <pre>
 * List&lt;String&gt; presidents = Moxie.mock(List.class);
 * List&lt;String&gt; stooges = Moxie.mock(List.class);
 * Group sequence = Moxie.group().willBeCalled().times(3);
 * Moxie.expect(presidents).inGroup(sequence).willReturn("Lincoln").on().get(15);
 * Moxie.expect(stooges).inGroup(sequence).willReturn("Larry").on().get(1);
 * Moxie.expect(stooges).inGroup(sequence).willReturn("Curly").on().get(2);
 * &nbsp;
 * // ...this will pass...
 * for (int i = 0; i &lt; 3; i++) {
 *     presidents.get(15);
 *     stooges.get(1);
 *     stooges.get(2);
 * }
 * &nbsp;
 * // ...but another time through the loop will cause a failure:
 * presidents.get(15);
 * stooges.get(1);
 * stooges.get(2);
 * </pre>
 * <p>
 * Example using {@link MoxieOptions#UNORDERED} <code>Group</code>s:
 * </p>
 * <pre>
 * List&lt;String&gt; presidents = Moxie.mock(List.class);
 * Group checkpoint = Moxie.group(MoxieOptions.UNORDERED);
 * Moxie.expect(presidents).inGroup(checkpoint).willReturn("Reagan").on().get(39);
 * Moxie.expect(presidents).inGroup(checkpoint).willReturn("GHW Bush").on().get(40);
 * Moxie.expect(presidents).willReturn("Clinton").on().get(41);
 * Moxie.expect(presidents).willReturn("GW Bush").on().get(42);
 * &nbsp;
 * presidents.get(40);
 * presidents.get(39);
 * &nbsp;
 * // Here we can check that just the expectations in "checkpoint" have been fulfilled
 * Moxie.verify(checkpoint);
 * &nbsp;
 * presidents.get(41);
 * presidents.get(42);
 * &nbsp;
 * // verify all expectations
 * Moxie.verify();
 * </pre>
 * <p>&nbsp;</p>
 * <h2>Usage in check-driven tests</h2>
 * <p>
 * Use the {@link Check#inGroup(Group...) Check.inGroup()} method to associate a check
 * with an {@link MoxieOptions#ORDERED} <code>Group</code>.  The check will then only match
 * invocations that occurred after the most recently matched invocation whose check was
 * associated with any of the specified <code>Group</code>s.
 * </p>
 * <p>
 * Example:
 * </p>
 * <pre>
 * List&lt;String&gt; realPresidentsList = Arrays.asList(
 *     "Washington", "J Adams", "Jefferson", "Madison", "Monroe", "JQ Adams", "Jackson");
 * List&lt;String&gt; presidents = Moxie.spy(realPresidentsList, MoxieOptions.PERMISSIVE);
 * &nbsp;
 * presidents.get(1);  // returns "J Adams"
 * presidents.get(2);  // returns "Jefferson"
 * presidents.get(5);  // returns "JQ Adams"
 * presidents.get(6);  // returns "Jackson"
 * &nbsp;
 * // This will pass...
 * Group sequence1 = Moxie.group();
 * Moxie.check(presidents).inGroup(sequence1).returned(Moxie.endsWith("Adams")).times(2).on().get(Moxie.anyInt());
 * Moxie.check(presidents).inGroup(sequence1).returned("Jackson").on().get(Moxie.anyInt());
 * &nbsp;
 * // ...but this will fail:
 * Group sequence2 = Moxie.group();
 * Moxie.check(presidents).inGroup(sequence2).returned(Moxie.endsWith("Adams")).times(2).on().get(Moxie.anyInt());
 * Moxie.check(presidents).inGroup(sequence2).returned("Jefferson").on().get(Moxie.anyInt());
 * </pre>
 * <p>&nbsp;</p>
 * <h2>Caveat</h2>
 * <p>
 * Note that the behaviors of <code>Group</code>s with respect to expectations and checks
 * are distinct and unrelated; expectations associated with a <code>Group</code> will have
 * no effect on its behavior towards checks, and vice versa.
 * </p>
 * <p>
 * Of course, if you're intermixing expectations and checks, you may already be
 * well on your way to an unreadable and unmaintainable test - simplify, simplify.
 * </p>
 */
public interface Group {
    /**
     * <p>
     * Domain-specific language method - used to specify how many times this group should be fulfilled from beginning to end.
     * </p>
     * <p>
     * Only meaningful for {@link MoxieOptions#ORDERED ORDERED} groups, and with regard to expectations rather than checks.
     * </p>
     *
     * @return a {@link Cardinality} whose methods will return this group
     */
    Cardinality<Group> willBeCalled();
}
