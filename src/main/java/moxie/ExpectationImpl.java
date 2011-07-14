/*
 * Copyright (c) 2010-2011 Moxie contributors
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class ExpectationImpl<T> implements Expectation<T>, SelfDescribing {

    private final Interception<T> interception;
    private CardinalityImpl cardinality = new CardinalityImpl<CardinalityImpl>().once();

    private Set<GroupImpl> groups = null;
    private MethodIntercept handler = null;
    private Method method;
    private List<Matcher> argMatchers = new ArrayList<Matcher>();
    private boolean defaultCardinality = true;
    private boolean unordered = false;
    private Matcher returnValueMatcher;
    private Matcher exceptionMatcher;

    ExpectationImpl(Interception<T> interception) {
        this.interception = interception;
        cardinality = new CardinalityImpl<Expectation<T>>(this);
        cardinality.atLeastOnce();
    }

    private CardinalityImpl<Expectation<T>> newCardinality() {
        if (!defaultCardinality) {
            throw new IllegalStateException("already specified number of times");
        }
        defaultCardinality = false;
        CardinalityImpl<Expectation<T>> result = new CardinalityImpl<Expectation<T>>(this);
        cardinality = result;
        return result;
    }

    public Expectation<T> times(int minTimes, int maxTimes) {
        return newCardinality().times(minTimes, maxTimes);
    }

    public Expectation<T> times(int times) {
        return newCardinality().times(times);
    }

    public Expectation<T> once() {
        return newCardinality().once();
    }

    public Expectation<T> never() {
        return newCardinality().never();
    }

    public Expectation<T> atMostOnce() {
        return newCardinality().atMostOnce();
    }

    public Expectation<T> atMost(int times) {
        return newCardinality().atMost(times);
    }

    public Expectation<T> atLeastOnce() {
        return newCardinality().atLeastOnce();
    }

    public Expectation<T> atLeast(int times) {
        return newCardinality().atLeast(times);
    }

    public Expectation<T> anyTimes() {
        return newCardinality().anyTimes();
    }

    public Expectation<T> inGroup(Group... groups) {
        if (this.groups == null) {
            this.groups = new HashSet<GroupImpl>();
        }
        for (Group group : groups) {
            GroupImpl groupImpl = (GroupImpl) group;
            this.groups.add(groupImpl);
            groupImpl.add(this);
        }
        return this;
    }

    public Expectation<T> atAnyTime() {
        this.unordered = true;
        return this;
    }

    public Expectation<T> willReturn(Object result) {
        return doWillHandleWith(new ReturnHandler(result));
    }

    public Expectation<T> willThrow(Throwable throwable) {
        return doWillHandleWith(new ThrowHandler(throwable));
    }

    public Expectation<T> willDelegateTo(T delegate) {
        return doWillHandleWith(new DelegateHandler(delegate));
    }

    private Expectation<T> doWillHandleWith(MethodIntercept handler) {
        if (this.handler instanceof ConsecutiveHandler) {
            ((ConsecutiveHandler) this.handler).add(handler);
        } else if (this.handler != null) {
            this.handler = new ConsecutiveHandler(this.handler).add(handler);
        } else {
            this.handler = handler;
        }
        return this;
    }

    public Expectation<T> willHandleWith(final InvocationHandler handler) {
        return doWillHandleWith(new MethodIntercept() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperInvoker superInvoker) throws Throwable {
                return handler.invoke(proxy, method, args);
            }
        });
    }

    public T on() {
        if (this.method != null) {
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
        return interception.getProxyFactory().createProxy(new MethodIntercept() {
            public Object intercept(Object proxy, Method method, Object[] params, SuperInvoker superInvoker) throws Throwable {
                handleInvocation(method, params);
                return MoxieUtils.defaultValue(method.getReturnType());
            }
        }, interception.getConstructorArgTypes(), interception.getConstructorArgs());
    }

    private void handleInvocation(Method method, Object[] params) {
        if (this.method != null) {
            throw new IllegalStateException("method to match already specified");
        }

        if (handler instanceof ReturnHandler) {
            Class returnType = MoxieUtils.toNonPrimitive(method.getReturnType());
            Object result = ((ReturnHandler) handler).getResult();
            if (returnType == Void.TYPE && result != null) {
                throw new IllegalArgumentException("return value specified for void method");
            } else if (result != null && !returnType.isAssignableFrom(result.getClass())) {
                throw new IllegalArgumentException("incompatible result type (" + result.getClass().getName() + ") for method which returns " + method.getReturnType().getName());
            }
        }

        if (handler instanceof ThrowHandler) {
            Throwable throwable = ((ThrowHandler) handler).getThrowable();
            if (throwable instanceof Exception && !(throwable instanceof RuntimeException)) {
                boolean foundCompatibleException = false;
                for (Class<?> exceptionType : method.getExceptionTypes()) {
                    if (exceptionType.isAssignableFrom(throwable.getClass())) {
                        foundCompatibleException = true;
                        break;
                    }
                }
                if (!foundCompatibleException) {
                    throw new IllegalArgumentException("exception is of type not thrown by the method (" + throwable.getClass().getName() + ")");
                }
            }
        }

        this.method = method;
        argMatchers = MatcherSyntax.methodCall(method, params);
        interception.addExpectation(this);
    }

    public T will() {
        return on();
    }

    public void on(String methodName, Object... params) {
        handleInvocation(MoxieUtils.guessMethod(this.interception.getInterceptedClass(), methodName, null, params), params);
    }

    public void when(String methodName, Object... params) {
        on(methodName, params);
    }

    public void will(String methodName, Object... params) {
        on(methodName, params);
    }

    public void on(String methodName, Class[] paramSignature, Object... params) {
        handleInvocation(MoxieUtils.guessMethod(this.interception.getInterceptedClass(), methodName, paramSignature, params), params);
    }

    public void when(String methodName, Class[] paramSignature, Object... params) {
        on(methodName, paramSignature, params);
    }

    public void will(String methodName, Class[] paramSignature, Object... params) {
        on(methodName, paramSignature, params);
    }

    public T when() {
        return on();
    }

    public Expectation<T> andReturn(Object result) {
        return willReturn(result);
    }

    public Expectation<T> willConsecutivelyReturn(Object... results) {
        return willConsecutivelyReturn(Arrays.asList(results));
    }

    public Expectation<T> andConsecutivelyReturn(Object... results) {
        return willConsecutivelyReturn(results);
    }

    public Expectation<T> willConsecutivelyReturn(Iterable results) {
        for (Object result : results) {
            willReturn(result);
        }
        return this;
    }

    public Expectation<T> andConsecutivelyReturn(Iterable results) {
        return willConsecutivelyReturn(results);
    }

    public Expectation<T> willReturnVerified(Object result) {
        Matcher matcher = MatcherSyntax.singleMatcherExpression(null, result);
        if (!(interception instanceof SpyImpl)) {
            throw new IllegalStateException("this method is only for expectations on spy objects");
        }
        returnValueMatcher = matcher;
        return this;
    }

    public Expectation<T> andVerifyReturn(Object result) {
        return willReturnVerified(result);
    }

    public Expectation<T> andThrow(Throwable throwable) {
        return willThrow(throwable);
    }

    public Expectation<T> willConsecutivelyThrow(Throwable... throwables) {
        for (Throwable throwable : throwables) {
            willThrow(throwable);
        }
        return this;
    }

    public Expectation<T> andConsecutivelyThrow(Throwable... throwables) {
        return willConsecutivelyThrow(throwables);
    }

    public Expectation<T> willThrowVerified(Throwable throwable) {
        Matcher matcher = MatcherSyntax.singleMatcherExpression(Throwable.class, throwable);
        if (!(interception instanceof SpyImpl)) {
            throw new IllegalStateException("this method is only for expectations on spy objects");
        }
        exceptionMatcher = matcher;
        return this;
    }

    public Expectation<T> andVerifyThrow(Throwable throwable) {
        return willThrowVerified(throwable);
    }

    public Expectation<T> andDelegateTo(T delegate) {
        return willDelegateTo(delegate);
    }

    public Expectation<T> andHandleWith(InvocationHandler handler) {
        return willHandleWith(handler);
    }

    public Expectation<T> andCallOriginal() {
        return willCallOriginal();
    }

    public Expectation<T> willCallOriginal() {
        return doWillHandleWith(new OriginalHandler());
    }

    boolean match(Method method, Object[] args, MethodBehavior behavior, GroupImpl group) {
        if (!this.method.equals(method)) {
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
        description.appendText(method.getName());
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

    static private class ReturnHandler implements MethodIntercept, SelfDescribing {
        private final Object result;

        public ReturnHandler(Object result) {
            this.result = result;
        }

        public Object intercept(Object mockObject, Method method, Object[] parameters, SuperInvoker superInvoker) throws Throwable {
            return result;
        }

        public Object getResult() {
            return result;
        }

        public void describeTo(Description description) {
            description.appendText("return ");
            description.appendValue(result);
        }
    }

    static private class ThrowHandler implements MethodIntercept, SelfDescribing {
        private final Throwable throwable;

        public ThrowHandler(Throwable throwable) {
            this.throwable = throwable;
        }

        public Object intercept(Object mockObject, Method method, Object[] parameters, SuperInvoker superInvoker) throws Throwable {
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
    }

    static private class DelegateHandler implements MethodIntercept, SelfDescribing {
        private final Object delegate;

        public DelegateHandler(Object delegate) {
            this.delegate = delegate;
        }

        public Object intercept(Object mockObject, Method method, Object[] parameters, SuperInvoker superInvoker) throws Throwable {
            return method.invoke(delegate, parameters);
        }

        public void describeTo(Description description) {
            description.appendText("delegate to ");
            description.appendValue(delegate);
        }
    }

    static private class ConsecutiveHandler implements MethodIntercept, SelfDescribing {
        private final List<MethodIntercept> handlers = new ArrayList<MethodIntercept>();
        private Iterator<MethodIntercept> iterator = null;

        public ConsecutiveHandler(MethodIntercept handler) {
            handlers.add(handler);
        }

        public ConsecutiveHandler add(MethodIntercept invocationHandler) {
            handlers.add(invocationHandler);
            return this;
        }

        public Object intercept(Object mockObject, Method method, Object[] parameters, SuperInvoker superInvoker) throws Throwable {
            if (iterator == null) {
                iterator = handlers.iterator();
            }
            if (!iterator.hasNext()) {
                throw new MoxieUnexpectedError("not enough consecutive-call handlers", null);
            }
            return iterator.next().intercept(mockObject, method, parameters, superInvoker);
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
    }

    static private class OriginalHandler implements MethodIntercept, SelfDescribing {
        public Object intercept(Object mockObject, Method method, Object[] parameters, SuperInvoker superInvoker) throws Throwable {
            return superInvoker.invokeSuper(parameters);
        }

        public void describeTo(Description description) {
            description.appendText("call original method implementation");
        }
    }

}
