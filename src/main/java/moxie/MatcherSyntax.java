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

import moxie.hamcrest.IsArray;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Static methods performing Moxie's magickal process of turning {@link MoxieMatchers} calls into Hamcrest {@link Matcher}s.
 */
class MatcherSyntax {

    static private <T> T shouldFullyConsumeMatcherReports(T someCall) {
        LinkedList<MatcherReport> matcherReports = MoxieMatchers.getMatcherReports();
        if (!matcherReports.isEmpty()) {
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            pw.println("Too many MoxieMatchers calls were made for this expression.  Excess calls were as follows:");
//            for (MatcherReport matcherReport : matcherReports) {
//                matcherReport.getWhereInstantiated().printStackTrace(pw);
//            }
//            matcherReports.clear();
//            throw new MoxieSyntaxError(sw.toString());
            throw new MoxieSyntaxError("Too many MoxieMatchers calls were made for this expression");
        }
        return someCall;
    }

    static <T> Matcher singleMatcherExpression(Class<T> expectedParameterType, T matchValue) {
        return shouldFullyConsumeMatcherReports(singleMatcherFragment(expectedParameterType, matchValue));
    }

    static List<Matcher> matcherListExpression(List<Class<?>> expectedParameterTypes, List matchValues) {
        return shouldFullyConsumeMatcherReports(matcherListFragment(expectedParameterTypes, matchValues));
    }

    @SuppressWarnings("unchecked")
    static <T> Matcher<T> singleMatcherFragment(Class<T> expectedParameterType, T matchValue) {
        return matcherListFragment(Collections.<Class<?>>singletonList(expectedParameterType), Collections.singletonList(matchValue)).get(0);
    }

    static List<Matcher> matcherListFragment(Class expectedParameterType, Object matchValuesArray) {
        List matchValues = MoxieUtils.listFromArray(matchValuesArray);
        return matcherListFragment(expectedParameterType, matchValues);
    }

    @SuppressWarnings("unchecked")
    static List<Matcher> matcherListFragment(Class expectedParameterType, List matchValues) {
        List expectedParameterTypes = Collections.nCopies(matchValues.size(), expectedParameterType);
        return matcherListFragment(expectedParameterTypes, matchValues);
    }

    @SuppressWarnings("unchecked")
    static List<Matcher> matcherListFragment(List<Class<?>> expectedParameterTypes, List matchValues) {
        LinkedList<MatcherReport> matcherReports = MoxieMatchers.getMatcherReports();
        List<Matcher> matchers = new ArrayList<Matcher>();

        if (matcherReports.isEmpty()) {
            for (Object matchValue : matchValues) {
                matchers.add(new IsEqual(matchValue));
            }
            return matchers;
        }

        for (int i = matchValues.size() - 1; i >= 0; i--) {
            Object matchValue = matchValues.get(i);
            Class<?> paramType = expectedParameterTypes.get(i);
            if (!MoxieUtils.isDefaultValue(matchValue, paramType) && (matcherReports.isEmpty() || !MoxieUtils.isDefaultValue(matchValue, matcherReports.getLast().getExpectedType()))) {
                matchers.add(0, new IsEqual(matchValue));
            } else if (matcherReports.isEmpty()) {
                // TODO nicer exception - matcher syntax error
                throw new IllegalArgumentException("ambiguous partially-specified matcher invocation - please use matchers for all method parameters having default value");
            } else {
                Class expectedType = matcherReports.getLast().getExpectedType();
                Class expectedTypeBoxed = MoxieUtils.toNonPrimitive(expectedType);
                Class paramTypeBoxed = MoxieUtils.toNonPrimitive(paramType);

                if (expectedType == null || expectedType.isInterface() || paramType == null || paramType.isInterface()) {
                    // can't directly compare arg types, so presume matcher is what we want
                    matchers.add(0, matcherReports.removeLast().getMatcher());
                } else if (expectedTypeBoxed.isAssignableFrom(paramTypeBoxed) || paramTypeBoxed.isAssignableFrom(expectedTypeBoxed)) {
                    // types are compatible, so matcher should be OK
                    matchers.add(0, matcherReports.removeLast().getMatcher());
                } else {
                    // does user actually want to match on a default value?
                    // TODO nicer exception - matcher syntax error
                    throw new IllegalArgumentException("matcher type mismatch in matcher invocation");
                }
            }
        }
        return matchers;
    }

    @SuppressWarnings("unchecked")
    static List<Matcher> methodCall(InvocableAdapter invocable, Object[] params) throws IllegalArgumentException {
        List paramsList = params == null ? Collections.emptyList() : new ArrayList(Arrays.asList(params));
        LinkedList<MatcherReport> matcherReports = MoxieMatchers.getMatcherReports();
        List<Class<?>> paramTypes = new ArrayList<Class<?>>(Arrays.asList(invocable.getParameterTypes()));
        Matcher varargMatcher = null;
        if (invocable.isVarArgs()) {
            Class<?> paramType = paramTypes.remove(paramTypes.size() - 1);
            Object varParamsArray = paramsList.remove(paramsList.size() - 1);
            if (varParamsArray == null && !matcherReports.isEmpty() && matcherReports.getLast().getExpectedType() != null && matcherReports.getLast().getExpectedType().isArray()) {
                // If varargs array is null and there's an array matcher on top of the stack, then it applies to the varargs array.
                varargMatcher = matcherReports.removeLast().getMatcher();
            } else {
                // construct array matcher to match elements of varargs array
                List<Matcher> varArgMatchers = MatcherSyntax.matcherListFragment(paramType.getComponentType(), varParamsArray);
                varargMatcher = new IsArray(varArgMatchers);
            }
        }
        List<Matcher> argMatchers = MatcherSyntax.matcherListExpression(paramTypes, paramsList);
        if (varargMatcher != null) {
            argMatchers.add(varargMatcher);
        }
        return argMatchers;
    }
}
