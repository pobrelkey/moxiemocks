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
 * Interface for a no-arg method object that returns a value.
 * </p>
 * <p>
 * This is largely the same interface as
 * <code></code><a href="http://download.java.net/jdk8/docs/api/java/util/function/Supplier.html">java.util.function.Supplier</a></code>
 * in Java 8, modified to throw {@link Exception} to make mocking methods that throw checked exceptions easier.
 * </p>
 *
 * @param <T> the type of the value to be returned
 */
public interface ThrowingSupplier<T> {
    /**
     * Returns a value.
     * @return the value
     */
    T get() throws Exception;
}
