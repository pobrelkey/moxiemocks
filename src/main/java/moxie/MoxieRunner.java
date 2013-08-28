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
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * JUnit 4 unit test runner which automatically sets up mocks prior to each test method, and verifies them afterwards.
 * </p>
 * <p>
 * Under the covers, this class just tacks a {@link MoxieRule} onto your test instance - see documentation
 * for that class for more details of usage.  This class is provided mainly for backwards compatibility,
 * and to mollify people who think JUnit 4.7+ {@link org.junit.Rule Rule}s are ugly.
 * Provided you have a recent enough version of JUnit, you really ought to use {@link MoxieRule} instead -
 * especially if you want to use PowerMock for static/constructor mocking (in which case you'd need to use
 * PowerMock's <code>PowerMockRunner</code> instead of this class).
 * </p>
 * <p>
 * Use <code>MoxieRunner</code> by adding the <code>@RunWith(MoxieRunner.class)</code> annotation to your test classes.
 * </p>
 */
public class MoxieRunner extends BlockJUnit4ClassRunner {

    public MoxieRunner(Class<?> testClass) throws org.junit.runners.model.InitializationError {
        super(testClass);
    }

    /**
     * Internal JUnit method - overridden to add a {@link MoxieRule} to all tests.
     */
    @Override
    protected List<MethodRule> rules(Object test) {
        ArrayList<MethodRule> result = new ArrayList<MethodRule>(super.rules(test));
        result.add(new MoxieRule());
        return result;
    }
}
