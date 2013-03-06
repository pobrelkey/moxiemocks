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

import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.NewInvocationControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class PowermockUtil {
    private static boolean havePowermock = false;
    private static Object powermockInvocationHandler = null;
    private static Object powermockConstructorHandler = null;

    static {
        try {
            new MemberModifier();
            powermockInvocationHandler = new PowermockInvocationHandler(ProxyIntercepts.proxyIntercepts);
            powermockConstructorHandler = new PowermockConstructorHandler(ProxyIntercepts.proxyIntercepts);
            havePowermock = true;
        } catch (NoClassDefFoundError e) {
            // oh well, no powermock then.
        }
    }
    static void zombify(Constructor constructor) {
        if (!havePowermock) {
            throw new UnsupportedOperationException("add powermock-api-support to the classpath to enable mocking of constructors");
        }
        MockRepository.putNewInstanceControl(constructor.getDeclaringClass(), (NewInvocationControl<?>) powermockConstructorHandler);
    }

    static void zombify(Method method) {
        if (!havePowermock) {
            throw new UnsupportedOperationException("add powermock-api-support to the classpath to enable mocking of static/final methods");
        }
        MemberModifier.replace(method).with((InvocationHandler) powermockInvocationHandler);
    }
}
