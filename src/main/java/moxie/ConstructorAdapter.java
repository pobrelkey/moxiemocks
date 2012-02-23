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

import java.lang.reflect.Constructor;

class ConstructorAdapter implements InvocableAdapter {
    private final Constructor constructor;

    public ConstructorAdapter(Constructor constructor) {
        this.constructor = constructor;
    }

    public Class<?>[] getParameterTypes() {
        return constructor.getParameterTypes();
    }

    public boolean isVarArgs() {
        return constructor.isVarArgs();
    }

    public void zombify() {
        CGLIBProxyFactory.zombify(constructor);
    }

    public Class getReturnType() {
        return constructor.getDeclaringClass();
    }

    public Class[] getExceptionTypes() {
        return constructor.getExceptionTypes();
    }

    public String getName() {
        return constructor.getName();
    }

    public Constructor getConstructor() {
        return constructor;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ConstructorAdapter && constructor.equals(((ConstructorAdapter) o).constructor);
    }

    @Override
    public int hashCode() {
        return constructor.hashCode();
    }

    @Override
    public String toString() {
        return constructor.toString();
    }
}
