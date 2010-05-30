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

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * <p>
 * JUnit 4 unit test runner which automatically sets up mocks prior to each test method, and verifies them afterwards.
 * </p>
 * <p>
 * Use <code>MoxieRunner</code> by adding the <code>@RunWith(MoxieRunner.class)</code> annotation to your test classes.
 * <code>MoxieRunner</code> will use {@link Moxie#autoMock(Object...) Moxie.autoMock()} before each test method
 * to populate annotated fields on your test instance with mock/spy objects.  After the method completes, it will
 * {@link Moxie#verify(Object...) verify} your mocks if the test was otherwise successful, then
 * {@link Moxie#autoUnMock(Object...) Moxie.autoUnmock()} your test instance.
 * </p>
 */
public class MoxieRunner extends BlockJUnit4ClassRunner {

    public MoxieRunner(Class<?> testClass) throws org.junit.runners.model.InitializationError {
        super(testClass);
    }

    protected Statement methodInvoker(final FrameworkMethod frameworkMethod, final Object test) {
        final Statement invoker = MoxieRunner.super.methodInvoker(frameworkMethod, test);
        return new Statement() {
            public void evaluate() throws Throwable {
                Moxie.deactivate();
                Moxie.getMatcherReports().clear();
                Moxie.autoMock(test);
                try {
                    invoker.evaluate();
                    Moxie.verify();
                } finally {
                    Moxie.autoUnMock(test);
                    Moxie.deactivate();
                    Moxie.getMatcherReports().clear();
                }
            }
        };
    }
}
