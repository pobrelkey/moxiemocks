/*
 * Copyright (c) 2010-2012 Moxie contributors
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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

abstract class ExpectationImpl<E extends ExpectationImpl<E, I>, I extends Interception> implements SelfDescribing {

    protected final I interception;

    @SuppressWarnings("unchecked")
    private CardinalityImpl<E> cardinality = new CardinalityImpl<CardinalityImpl>().once();

    private Set<GroupImpl> groups = null;
    private MethodIntercept handler = null;
    private InvocableAdapter invocable;
    private List<Matcher> argMatchers = new ArrayList<Matcher>();
    private boolean defaultCardinality = true;
    private boolean unordered = false;
    protected Matcher returnValueMatcher;
    protected Matcher exceptionMatcher;

    @SuppressWarnings("unchecked")
    protected ExpectationImpl(I interception) {
        this.interception = interception;
        cardinality = new CardinalityImpl<E>((E) this);
        cardinality.atLeastOnce();
    }

    private CardinalityImpl<E> newCardinality() {
        if (!defaultCardinality) {
            throw new IllegalStateException("already specified number of times");
        }
        defaultCardinality = false;
        @SuppressWarnings("unchecked")
        CardinalityImpl<E> result = new CardinalityImpl<E>((E) this);
        cardinality = result;
        return result;
    }

    public E times(int minTimes, int maxTimes) {
        return newCardinality().times(minTimes, maxTimes);
    }

    public E times(int times) {
        return newCardinality().times(times);
    }

    public E once() {
        return newCardinality().once();
    }

    public E never() {
        return newCardinality().never();
    }

    public E atMostOnce() {
        return newCardinality().atMostOnce();
    }

    public E atMost(int times) {
        return newCardinality().atMost(times);
    }

    public E atLeastOnce() {
        return newCardinality().atLeastOnce();
    }

    public E atLeast(int times) {
        return newCardinality().atLeast(times);
    }

    public E anyTimes() {
        return newCardinality().anyTimes();
    }

    @SuppressWarnings("unchecked")
    public E inGroup(Group... groups) {
        if (this.groups == null) {
            this.groups = new HashSet<GroupImpl>();
        }
        for (Group group : groups) {
            GroupImpl groupImpl = (GroupImpl) group;
            this.groups.add(groupImpl);
            groupImpl.add((E) this);
        }
        return (E) this;
    }

    @SuppressWarnings("unchecked")
    public E atAnyTime() {
        this.unordered = true;
        return (E) this;
    }

    public E willReturn(Object result) {
        return doWillHandleWith(new ReturnHandler(result));
    }

    public E willThrow(Throwable throwable) {
        return doWillHandleWith(new ThrowHandler(throwable));
    }

    public E willDelegateTo(Object delegate) {
        return doWillHandleWith(new DelegateHandler(delegate));
    }

    @SuppressWarnings("unchecked")
    protected E doWillHandleWith(MethodIntercept handler) {
        if (this.handler instanceof ConsecutiveHandler) {
            ((ConsecutiveHandler) this.handler).add(handler);
        } else if (this.handler != null) {
            this.handler = new ConsecutiveHandler(this.handler).add(handler);
        } else {
            this.handler = handler;
        }
        return (E) this;
    }

    public E willHandleWith(final InvocationHandler handler) {
        return doWillHandleWith(new MethodIntercept() {
            public Object intercept(Object proxy, InvocableAdapter invocable, Object[] args, SuperInvoker superInvoker) throws Throwable {
                Method method = (invocable instanceof MethodAdapter) ? ((MethodAdapter) invocable).getMethod() : null;
                return handler.invoke(proxy, method, args);
            }
        });
    }

    protected void checkMethodAndCardinality() {
        if (this.invocable != null) {
            throw new IllegalStateException("method to match already specified");
        }
        if (this.handler instanceof ConsecutiveHandler) {
            ConsecutiveHandler consecutiveHandler = (ConsecutiveHandler) this.handler;
            if (this.cardinality.getMinTimes() != null && this.cardinality.getMinTimes() > consecutiveHandler.size()) {
                throw new IllegalStateException("not enough consecutive-call handlers ("+consecutiveHandler.size()+") defined to handle minimal number of calls (" + this.cardinality.getMinTimes() + ")");
            }
            if (this.cardinality.getMaxTimes() != null && this.cardinality.getMaxTimes() < consecutiveHandler.size()) {
                throw new IllegalStateException("more consecutive-call handlers ("+consecutiveHandler.size()+") defined than can handle maximum number of calls (" + this.cardinality.getMaxTimes() + ")");
            }
        }
    }

    protected Object handleInvocation(InvocableAdapter invocable, Object[] params) {
        if (this.invocable != null) {
            throw new IllegalStateException("method to match already specified");
        }
        invocable.zombify();

//        boolean deepMockable = !invocable.getReturnType().equals(Void.TYPE) &&
//                !invocable.getReturnType().isPrimitive() &&
//                !Modifier.isFinal(invocable.getReturnType().getModifiers());
//
//        // If not deep-mockable, check return/throw types now.
//        if (!deepMockable) {
            if (handler instanceof TypeCompatibilityVerifable) {
                ((TypeCompatibilityVerifable) handler).verifyTypeCompatible(invocable);
            }
//        }
//
//        // TODO: handle deep mocks differently
        this.invocable = invocable;
        argMatchers = MatcherSyntax.methodCall(invocable, params);
        interception.addExpectation(this);

//        if (deepMockable) {
//            // TODO: return deep mocking stub instead!
//        }
        return MoxieUtils.defaultValue(invocable.getReturnType());
    }

    public Object on(String methodName, Object... params) {
        return on(methodName, null, params);
    }

    abstract protected boolean isStatic();

    public Object when(String methodName, Object... params) {
        return on(methodName, params);
    }

    public Object will(String methodName, Object... params) {
        return on(methodName, params);
    }

    public Object on(String methodName, Class[] paramSignature, Object... params) {
        checkMethodAndCardinality();
        return handleInvocation(MoxieUtils.guessMethod(this.interception.getInterceptedClass(), methodName, isStatic(), paramSignature, params), params);
    }

    public Object when(String methodName, Class[] paramSignature, Object... params) {
        return on(methodName, paramSignature, params);
    }

    public Object will(String methodName, Class[] paramSignature, Object... params) {
        return on(methodName, paramSignature, params);
    }

    public E andReturn(Object result) {
        return willReturn(result);
    }

    public E willConsecutivelyReturn(Object... results) {
        return willConsecutivelyReturn(Arrays.asList(results));
    }

    public E andConsecutivelyReturn(Object... results) {
        return willConsecutivelyReturn(results);
    }

    @SuppressWarnings("unchecked")
    public E willConsecutivelyReturn(Iterable results) {
        for (Object result : results) {
            willReturn(result);
        }
        return (E) this;
    }

    public E andConsecutivelyReturn(Iterable results) {
        return willConsecutivelyReturn(results);
    }

    public E andThrow(Throwable throwable) {
        return willThrow(throwable);
    }

    @SuppressWarnings("unchecked")
    public E willConsecutivelyThrow(Throwable... throwables) {
        for (Throwable throwable : throwables) {
            willThrow(throwable);
        }
        return (E) this;
    }

    public E andConsecutivelyThrow(Throwable... throwables) {
        return willConsecutivelyThrow(throwables);
    }

    public E andDelegateTo(Object delegate) {
        return willDelegateTo(delegate);
    }

    public E andHandleWith(InvocationHandler handler) {
        return willHandleWith(handler);
    }

    boolean match(InvocableAdapter invocableAdapter, Object[] args, MethodBehavior behavior, GroupImpl group) {
        if (!this.invocable.equals(invocableAdapter)) {
            return false;
        }
        Matcher argsMatcher = MoxieMatchers.isArrayMatcher(argMatchers);
        if (args == null) {
            args = new Object[0];
        }
        if (!argsMatcher.matches(args)) {
            return false;
        }
        if (returnValueMatcher != null) {
            try {
                if (!returnValueMatcher.matches(behavior.invoke())) {
                    return false;
                }
            } catch (Throwable t) {
                return false;
            }
        } else if (exceptionMatcher != null) {
            try {
                behavior.invoke();
                return false;
            } catch (Throwable t) {
                if (!exceptionMatcher.matches(t)) {
                    return false;
                }
            }
        }
        if (!cardinality.isViable()) {
            return false;
        }
        cardinality.incrementCount();
        return true;
    }

    MethodIntercept getHandler() {
        return handler;
    }

    @SuppressWarnings("unchecked")
    Set<GroupImpl> getGroups() {
        return (groups != null) ? groups : Collections.EMPTY_SET;
    }

    boolean isUnordered() {
        return unordered;
    }

    void whenCardinalitySatisfied(Runnable runnable) {
        cardinality.whenCardinalitySatisfied(runnable);
    }

    boolean isSatisfied() {
        return cardinality.isSatisfied();
    }

    public void describeTo(Description description) {
        description.appendText("expected ");
        cardinality.describeExpected(description);
        description.appendText(", invoked ");
        cardinality.describeCount(description);
        description.appendText(": ");
        description.appendText(invocable.getName());
        description.appendList("(", ", ", ")", argMatchers);
        if (exceptionMatcher != null) {
            description.appendText(", expected to throw ");
            exceptionMatcher.describeTo(description);
        } else if (returnValueMatcher != null) {
            description.appendText(", expected to return ");
            returnValueMatcher.describeTo(description);
        }
        if (handler instanceof SelfDescribing) {
            description.appendText(" (will ");
            ((SelfDescribing) handler).describeTo(description);
            description.appendText(")");
        } else if (handler != null) {
            description.appendText(" (handled by ");
            description.appendValue(handler);
            description.appendText(")");
        }
    }

    static private interface TypeCompatibilityVerifable {
        void verifyTypeCompatible(InvocableAdapter invocable);
    }

    static private class ReturnHandler implements MethodIntercept, TypeCompatibilityVerifable, SelfDescribing {
        private final Object result;
        private boolean verified = false;

        public ReturnHandler(Object result) {
            this.result = result;
        }

        public Object intercept(Object mockObject, InvocableAdapter invocable, Object[] parameters, SuperInvoker superInvoker) throws Throwable {
            verifyTypeCompatible(invocable);
            return result;
        }

        public Object getResult() {
            return result;
        }

        public void describeTo(Description description) {
            description.appendText("return ");
            description.appendValue(result);
        }

        public void verifyTypeCompatible(InvocableAdapter invocable) {
            if (!this.verified) {
                Class returnType = MoxieUtils.toNonPrimitive(invocable.getReturnType());
                if (returnType == Void.TYPE && result != null) {
                    throw new IllegalArgumentException("return value specified for void method");
                } else if (result != null && !returnType.isAssignableFrom(result.getClass())) {
                    throw new IllegalArgumentException("incompatible result type (" + result.getClass().getName() + ") for method which returns " + invocable.getReturnType().getName());
                }
                this.verified = true;
            }
        }
    }

    static private class ThrowHandler implements MethodIntercept, TypeCompatibilityVerifable, SelfDescribing {
        private final Throwable throwable;
        private boolean verified = false;

        public ThrowHandler(Throwable throwable) {
            this.throwable = throwable;
        }

        public Object intercept(Object mockObject, InvocableAdapter invocable, Object[] parameters, SuperInvoker superInvoker) throws Throwable {
            verifyTypeCompatible(invocable);
            throwable.fillInStackTrace();
            throw throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public void describeTo(Description description) {
            description.appendText("throw ");
            description.appendValue(throwable);
        }

        public void verifyTypeCompatible(InvocableAdapter invocable) {
            if (!this.verified) {
                if (throwable instanceof Exception && !(throwable instanceof RuntimeException)) {
                    boolean foundCompatibleException = false;
                    for (Class<?> exceptionType : invocable.getExceptionTypes()) {
                        if (exceptionType.isAssignableFrom(throwable.getClass())) {
                            foundCompatibleException = true;
                            break;
                        }
                    }
                    if (!foundCompatibleException) {
                        throw new IllegalArgumentException("exception is of type not thrown by the method (" + throwable.getClass().getName() + ")");
                    }
                }
                this.verified = true;
            }
        }
    }

    static private class DelegateHandler implements MethodIntercept, SelfDescribing {
        private final Object delegate;

        public DelegateHandler(Object delegate) {
            this.delegate = delegate;
        }

        public Object intercept(Object mockObject, InvocableAdapter invocable, Object[] parameters, SuperInvoker superInvoker) throws Throwable {
            Method method = ((MethodAdapter) invocable).getMethod();
            try {
                method = delegate.getClass().getMethod(invocable.getName(), invocable.getParameterTypes());
                method.setAccessible(true);
            } catch (NoSuchMethodException e) {
                // oh well, try with original method
            }
            return method.invoke(delegate, parameters);
        }

        public void describeTo(Description description) {
            description.appendText("delegate to ");
            description.appendValue(delegate);
        }
    }

    protected static class ConsecutiveHandler implements MethodIntercept, TypeCompatibilityVerifable, SelfDescribing {
        private final List<MethodIntercept> handlers = new ArrayList<MethodIntercept>();
        private Iterator<MethodIntercept> iterator = null;

        public ConsecutiveHandler(MethodIntercept handler) {
            handlers.add(handler);
        }

        public ConsecutiveHandler add(MethodIntercept invocationHandler) {
            handlers.add(invocationHandler);
            return this;
        }

        public Object intercept(Object mockObject, InvocableAdapter invocable, Object[] parameters, SuperInvoker superInvoker) throws Throwable {
            if (iterator == null) {
                iterator = handlers.iterator();
            }
            if (!iterator.hasNext()) {
                throw new MoxieUnexpectedError("not enough consecutive-call handlers", null);
            }
            return iterator.next().intercept(mockObject, invocable, parameters, superInvoker);
        }

        public void describeTo(Description description) {
            description.appendText("consecutively ");

            for (Iterator<MethodIntercept> it = handlers.iterator(); it.hasNext(); ) {
                MethodIntercept handler = it.next();
                if (handler instanceof SelfDescribing) {
                    ((SelfDescribing) handler).describeTo(description);
                } else {
                    description.appendValue(handler);
                }
                if (it.hasNext()) {
                    description.appendText(", ");
                }
            }
        }

        public int size() {
            return handlers.size();
        }

        public void verifyTypeCompatible(InvocableAdapter invocable) {
            for (MethodIntercept handler : handlers) {
                if (handler instanceof TypeCompatibilityVerifable) {
                    ((TypeCompatibilityVerifable) handler).verifyTypeCompatible(invocable);
                }
            }
        }
    }

}
