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
 * Interface for a method object that returns <code>true</code> or
 * <code>false</code> depending on whether an input value matches some
 * criteria.
 * </p>
 * <p>
 * Note that this is essentially the same interface as
 * <code><a href="http://download.java.net/jdk8/docs/api/java/util/function/Predicate.html">java.util.function.Predicate</a></code>
 * in Java 8; Moxie uses its own version of this interface to make APIs
 * using this interface available on pre-1.8 JVMs where the above interface
 * is not available.
 * </p>
 *
 * @param <T> the type of the value to be matched
 */
public interface Predicate<T> {
    /**
     * Returns <code>true</code> if the parameter matches the criteria represented by this object.
     *
     * @param value the value to be tested
     * @return <code>true</code> if <code>value</code> matches the criteria represented by this object; <code>false</code> otherwise.
     */
    boolean test(T value);
}
