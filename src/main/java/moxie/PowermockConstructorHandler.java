/*
 * Copyright (c) 2010-2013 Moxie contributors
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

import org.powermock.core.spi.NewInvocationControl;

import java.util.Map;

class PowermockConstructorHandler implements NewInvocationControl {
    private static final MethodIntercept.SuperInvoker ZOMBIE_CONSTRUCTOR_SUPER_INVOKER = new ZombieSuperInvoker("cannot partially mock a constructor");
    private final Map<Object, MethodIntercept> proxyIntercepts;

    PowermockConstructorHandler(Map<Object, MethodIntercept> proxyIntercepts) {
        this.proxyIntercepts = proxyIntercepts;
    }

    public Object invoke(Class type, Object[] args, Class[] sig) throws Exception {
        try {
            return proxyIntercepts.get(type).intercept(type, new ConstructorAdapter(type.getDeclaredConstructor(sig)), args, ZOMBIE_CONSTRUCTOR_SUPER_INVOKER);
        } catch (Exception e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            throw new MoxieUnexpectedError(e);
        }
    }

    public Object expectSubstitutionLogic(Object... arguments) throws Exception {
        throw new UnsupportedOperationException("EasyMock/Mockito operations on mocks are not supported by the Moxie invocation handler");
    }

    public Object replay(Object... mocks) {
        throw new UnsupportedOperationException("EasyMock/Mockito operations on mocks are not supported by the Moxie invocation handler");
    }

    public Object verify(Object... mocks) {
        throw new UnsupportedOperationException("EasyMock/Mockito operations on mocks are not supported by the Moxie invocation handler");
    }

    public Object reset(Object... mocks) {
        throw new UnsupportedOperationException("EasyMock/Mockito operations on mocks are not supported by the Moxie invocation handler");
    }
}
