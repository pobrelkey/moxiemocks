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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

abstract class Interception<T> implements InvocationHandler, Verifiable {

    protected static Method OBJECT_TO_STRING;
    protected static Method OBJECT_EQUALS;
    protected static Method OBJECT_HASH_CODE;
    static {
        try {
            OBJECT_TO_STRING = Object.class.getDeclaredMethod("toString");
            OBJECT_EQUALS    = Object.class.getDeclaredMethod("equals", Object.class);
            OBJECT_HASH_CODE = Object.class.getDeclaredMethod("hashCode");
        } catch (NoSuchMethodException e) {
            throw new MoxieError("WTF!", e);
        }
    }

    private final Class<T> clazz;
    protected final String name;
    private final Throwable whereInstantiated;
    private final List<Invocation> allInvocations;
    private MoxieFlags flags;
    private GroupImpl methods;
    protected T proxy;

    protected Interception(Class<T> clazz, String name, MoxieFlags flags, List<Invocation> invocations, InstantiationStackTrace instantiationStackTrace) {
        this.clazz = clazz;
        this.name = name;
        this.flags = MoxieOptions.MOCK_DEFAULTS;
        this.whereInstantiated = instantiationStackTrace;
        this.methods = new GroupImpl(name, flags);
        this.allInvocations = invocations;
        reset(flags);
    }

    public void reset(MoxieFlags flags) {
        if (flags != null) {
            this.flags = flags;
        }
        this.methods.reset(flags);
    }

    T proxy() {
        if (proxy == null) {
            proxy = MoxieUtils.newProxyInstance(clazz, this);
        }
        return proxy;
    }

    public Object invoke(Object unusedProxy, Method method, Object[] args) throws Throwable {
        final Invocation invocation = new Invocation(this, method, args);
        allInvocations.add(invocation);

        MethodBehavior methodBehavior = defaultBehavior(method, args);
        final ExpectationImpl expectation = methods.match(method, args, methodBehavior);
        if (expectation != null) {
            expectation.whenCardinalitySatisfied(new Runnable() {
                public void run() {
                    invocation.setExpectationSatisfied(expectation);
                }
            });
            if (expectation.getHandler() != null) {
                try {
                    Object result = expectation.getHandler().invoke(unusedProxy, method, args);
                    invocation.setValueReturned(result);
                    return result;
                } catch (Throwable t) {
                    invocation.setExceptionThrown(t);
                    throw t;
                }
            }
        } else if (!flags.isAutoStubbing()
                && !OBJECT_EQUALS.equals(method)
                && !OBJECT_HASH_CODE.equals(method)
                && !OBJECT_TO_STRING.equals(method)) {
            // TODO nicer exception
            throw new MoxieError("unexpected method invocation");
        }

        try {
            Object result = methodBehavior.invoke();
            invocation.setValueReturned(result);
            return result;
        } catch (Throwable t) {
            invocation.setExceptionThrown(t);
            throw t;
        }
    }

    abstract protected MethodBehavior defaultBehavior(Method method, Object[] args);

    Class<T> getInterceptedClass() {
        return clazz;
    }

    Expectation<T> expect() {
        final ExpectationImpl result = new ExpectationImpl(this);
        methods.add(result);
        return result;
    }

    public void verify() {
        methods.verify();
    }

    public Throwable getWhereInstantiated() {
        return whereInstantiated;
    }

    public Check<T> check() {
        return new CheckImpl(this, allInvocations);
    }

}
