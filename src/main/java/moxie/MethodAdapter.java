/*
 * Copyright (c) 2012 Moxie contributors
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class MethodAdapter implements InvocableAdapter {
    private final Method method;

    public MethodAdapter(Method method) {
        this.method = method;
    }

    public boolean isVarArgs() {
        return method.isVarArgs();
    }

    public void zombify() {
        if (Modifier.isPrivate(method.getModifiers()) || Modifier.isFinal(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
            PowermockUtil.zombify(method);
        }
    }

    public Class getReturnType() {
        return method.getReturnType();
    }

    public Class[] getExceptionTypes() {
        return method.getExceptionTypes();
    }

    public String getName() {
        return method.getName();
    }

    public Class getDeclaringClass() {
        return method.getDeclaringClass();
    }

    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof MethodAdapter) &&  method.equals(((MethodAdapter) o).method);
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }

    @Override
    public String toString() {
        return method.toString();
    }
}
