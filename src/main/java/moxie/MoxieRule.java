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

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * <p>
 * JUnit 4 {@link org.junit.rules.MethodRule MethodRule} which automatically sets up mocks prior to each test method, and verifies them afterwards.
 * </p>
 * <p>
 * Use <code>MoxieRule</code> by adding an annotated field to your test, as follows - this will cause <code>MoxieRule</code>
 * to get picked up and used by the JUnit 4 test runner.
 * </p>
 * <blockquote><pre>
 * @org.junit.Rule
 * public MoxieRule moxie = new MoxieRule();
 * </pre></blockquote>
 * <p>
 * <code>MoxieRule</code> will use {@link Moxie#autoMock(Object...) Moxie.autoMock()} before each test method
 * to populate annotated fields on your test instance with mock/spy objects.  After the method completes, it will
 * {@link Moxie#verify(Object...) verify} your mocks if the test was otherwise successful, then
 * {@link Moxie#autoUnMock(Object...) Moxie.autoUnmock()} your test instance.
 * </p>

 */
public class MoxieRule implements MethodRule {
    private final MoxieControl control;

    /**
     * <p>Creates a new {@link MoxieRule} using the {@link Moxie#threadLocalControl() thread-local instance}
     * of {@link MoxieControl}.</p>
     */
    public MoxieRule() {
        this(Moxie.threadLocalControl());
    }


    /**
     * <p>Creates a new {@link MoxieRule} using a user-specified instance of {@link MoxieControl}.</p>
     *
     * @param control the {@link MoxieControl} instance this thread should use
     */
    public MoxieRule(MoxieControl control) {
        this.control = control;
    }

    public Statement apply(final Statement statement, FrameworkMethod method, final Object testInstance) {
        return new Statement(){
            @Override
            public void evaluate() throws Throwable {
                control.deactivate();
                MoxieMatchers.getMatcherReports().clear();
                control.autoMock(testInstance);
                try {
                    statement.evaluate();
                    control.verify();
                } finally {
                    control.autoUnMock(testInstance);
                    control.deactivate();
                    MoxieMatchers.getMatcherReports().clear();
                }
            }
        };
    }

    /**
     * Returns the {@link MoxieControl} instance this rule uses to auto-populate and verify mocks.
     *
     * @return this rule's {@link MoxieControl} instance
     */
    public MoxieControl getControl() {
        return control;
    }
}
