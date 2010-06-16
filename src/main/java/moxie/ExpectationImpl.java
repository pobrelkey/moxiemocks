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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ExpectationImpl<T> implements Expectation<T>, SelfDescribing {

    private final Interception<T> interception;
    private CardinalityImpl cardinality = new CardinalityImpl<CardinalityImpl>().once();

    private Set<GroupImpl> groups = null;
    private InvocationHandler handler = null;
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
        if (handler != null) {
            throw new IllegalStateException("handler already specified for this invocation");
        }
        handler = new ReturnHandler(result);
        return this;
    }

    public Expectation<T> willThrow(Throwable throwable) {
        if (handler != null) {
            throw new IllegalStateException("handler already specified for this invocation");
        }
        handler = new ThrowHandler(throwable);
        return this;
    }

    public Expectation<T> willDelegateTo(T delegate) {
        if (handler != null) {
            throw new IllegalStateException("handler already specified for this invocation");
        }
        handler = new DelegateHandler(delegate);
        return this;
    }

    public Expectation<T> willHandleWith(InvocationHandler handler) {
        if (this.handler != null) {
            throw new IllegalStateException("handler already specified for this invocation");
        }
        this.handler = handler;
        return this;
    }

    public T on() {
        if (this.method != null) {
            throw new IllegalStateException("method to match already specified");
        }
        return MoxieUtils.newProxyInstance(interception.getInterceptedClass(), new InvocationHandler() {
            public Object invoke(Object unused, Method method, Object[] params) throws Throwable {
                if (ExpectationImpl.this.method != null) {
                    throw new IllegalStateException("method to match already specified");
                }
                ExpectationImpl.this.method = method;
                argMatchers = MatcherSyntax.methodCall(method, params);
                return MoxieUtils.defaultValue(method.getReturnType());
            }
        });
    }

    public T will() {
        return on();
    }

    public T when() {
        return on();
    }

    public Expectation<T> andReturn(Object result) {
        return willReturn(result);
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
            group.die("unexpected method invocation", method, args);
        }
        cardinality.incrementCount();
        return true;
    }

    InvocationHandler getHandler() {
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
        cardinality.describeTo(description);
        description.appendText(": ");
        description.appendText(method.getName());
        description.appendList("(", ", ", ")", argMatchers);
        if (exceptionMatcher != null) {
            description.appendText(", throws ");
            exceptionMatcher.describeTo(description);
        } else if (returnValueMatcher != null) {
            description.appendText(", returns ");
            returnValueMatcher.describeTo(description);
        }
    }

    static private class ReturnHandler implements InvocationHandler {
        private final Object result;

        public ReturnHandler(Object result) {
            this.result = result;
        }

        public Object invoke(Object mockObject, Method method, Object[] parameters) throws Throwable {
            return result;
        }
    }

    static private class ThrowHandler implements InvocationHandler {
        private final Throwable throwable;

        public ThrowHandler(Throwable throwable) {
            this.throwable = throwable;
        }

        public Object invoke(Object mockObject, Method method, Object[] parameters) throws Throwable {
            throw throwable;
        }
    }

    static private class DelegateHandler implements InvocationHandler {
        private final Object delegate;

        public DelegateHandler(Object delegate) {
            this.delegate = delegate;
        }

        public Object invoke(Object mockObject, Method method, Object[] parameters) throws Throwable {
            return method.invoke(delegate, parameters);
        }
    }
}
