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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hamcrest.SelfDescribing;

abstract class MoxieUtils {

    private static final Character ZERO_CHAR = Character.valueOf('\0');
    private static final Byte ZERO_BYTE = Byte.valueOf((byte) 0);
    private static final Short ZERO_SHORT = Short.valueOf((short) 0);
    private static final Integer ZERO_INT = Integer.valueOf(0);
    private static final Long ZERO_LONG = Long.valueOf((long) 0);
    private static final Double ZERO_DOUBLE = Double.valueOf((double) 0);
    private static final Float ZERO_FLOAT = Float.valueOf((float) 0);

    @SuppressWarnings("unchecked")
    static <T> T defaultValue(Class<T> clazz) {
        if (clazz == Boolean.TYPE || clazz == Boolean.class) {
            return (T) Boolean.FALSE;
        } else if (clazz == Character.TYPE || clazz == Character.class) {
            return (T) ZERO_CHAR;
        } else if (clazz == Byte.TYPE || clazz == Byte.class) {
            return (T) ZERO_BYTE;
        } else if (clazz == Short.TYPE || clazz == Short.class) {
            return (T) ZERO_SHORT;
        } else if (clazz == Integer.TYPE || clazz == Integer.class) {
            return (T) ZERO_INT;
        } else if (clazz == Long.TYPE || clazz == Long.class) {
            return (T) ZERO_LONG;
        } else if (clazz == Float.TYPE || clazz == Float.class) {
            return (T) ZERO_FLOAT;
        } else if (clazz == Double.TYPE || clazz == Double.class) {
            return (T) ZERO_DOUBLE;
        } else {
            return null;
        }
    }

    static boolean isDefaultValue(Object value, Class clazz) {
        if (clazz == Boolean.TYPE) {
            return Boolean.FALSE.equals(value);
        } else if (clazz == Character.TYPE) {
            return ZERO_CHAR.equals(value);
        } else if (clazz == Byte.TYPE) {
            return ZERO_BYTE.equals(value);
        } else if (clazz == Short.TYPE) {
            return ZERO_SHORT.equals(value);
        } else if (clazz == Integer.TYPE) {
            return ZERO_INT.equals(value);
        } else if (clazz == Long.TYPE) {
            return ZERO_LONG.equals(value);
        } else if (clazz == Float.TYPE) {
            return ZERO_FLOAT.equals(value);
        } else if (clazz == Double.TYPE) {
            return ZERO_DOUBLE.equals(value);
        } else if (clazz == Boolean.class) {
            return value == null || Boolean.FALSE.equals(value);
        } else if (clazz == Character.class) {
            return value == null || ZERO_CHAR.equals(value);
        } else if (clazz == Byte.class) {
            return value == null || ZERO_BYTE.equals(value);
        } else if (clazz == Short.class) {
            return value == null || ZERO_SHORT.equals(value);
        } else if (clazz == Integer.class) {
            return value == null || ZERO_INT.equals(value);
        } else if (clazz == Long.class) {
            return value == null || ZERO_LONG.equals(value);
        } else if (clazz == Float.class) {
            return value == null || ZERO_FLOAT.equals(value);
        } else if (clazz == Double.class) {
            return value == null || ZERO_DOUBLE.equals(value);
        } else {
            return value == null;
        }
    }

    static Class toNonPrimitive(Class clazz) {
        if (clazz == Boolean.TYPE) {
            return Boolean.class;
        } else if (clazz == Character.TYPE) {
            return Character.class;
        } else if (clazz == Byte.TYPE) {
            return Byte.class;
        } else if (clazz == Short.TYPE) {
            return Short.class;
        } else if (clazz == Integer.TYPE) {
            return Integer.class;
        } else if (clazz == Long.TYPE) {
            return Long.class;
        } else if (clazz == Float.TYPE) {
            return Float.class;
        } else if (clazz == Double.TYPE) {
            return Double.class;
        } else {
            return clazz;
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T createThreadLocalProxy(final Class<T> clazz, final Factory<T> factory) {
        final ThreadLocal<T> threadLocal = new ThreadLocal<T>();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            public Object invoke(Object proxyUnused, Method method, Object[] params) throws Throwable {
                T delegate = threadLocal.get();
                if (delegate == null) {
                    delegate = factory.create();
                    threadLocal.set(delegate);
                }
                try {
                    return method.invoke(delegate, params);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        });
    }

    static StackTraceElement getExternalCaller() {
        for (StackTraceElement element : new Exception().getStackTrace()) {
            if (!element.getClassName().startsWith("$") &&
                    !element.getClassName().startsWith("java.") &&
                    !element.getClassName().startsWith("javax.") &&
                    !element.getClassName().startsWith("moxie.")) {
                return element;
            }
        }
        return null;
    }

    static String getExternalCallerString() {
        StackTraceElement caller = getExternalCaller();
        StringBuffer result = new StringBuffer();
        if (caller != null) {
            if (caller.getClassName() != null) {
                result.append(caller.getClassName());
                if (caller.getMethodName() != null) {
                    result.append('.');
                    result.append(caller.getMethodName());
                }
            }
        }
        if (caller.getFileName() != null) {
            result.append('(');
            result.append(caller.getFileName());
            if (caller.getLineNumber() > 0) {
                result.append(':');
                result.append(caller.getLineNumber());
            }
            result.append(')');
        } else if (caller.getMethodName() != null) {
            result.append("()");
        }
        return (result.length() > 0) ? result.toString() : null;
    }

    /**
     * Performs the equivalent of {@link java.util.Arrays#asList(Object[])}, except it also works on primitive arrays.
     * (This is why the parameter type is <code>Object</code> and not <code>Object[]</code> - arrays of primitive
     * aren't castable to <code>Object[]</code>.)
     *
     * @param srcArray the array to be copied to a list
     * @param <T>      presumed boxed type of the items in the list
     * @return an {@link java.util.ArrayList} containing the elements of the array
     */
    @SuppressWarnings("unchecked")
    static <T> List<T> listFromArray(Object srcArray) {
        int srcLength = Array.getLength(srcArray);
        List<T> dest = new ArrayList<T>();
        for (int i = 0; i < srcLength; i++) {
            dest.add((T) Array.get(srcArray, i));
        }
        return dest;
    }

    static void describeExpectations(SimpleDescription desc, Collection<ExpectationImpl> unorderedExpectations, Collection<ExpectationImpl> orderedExpectations) {
        describeIfNonEmpty(desc, "Expected (not in order):\n", unorderedExpectations);
        describeIfNonEmpty(desc, "Expected (in order):\n", orderedExpectations);
    }

    static <T extends SelfDescribing> void describeIfNonEmpty(SimpleDescription desc, String message, Collection<T> selfDescribing) {
        if (selfDescribing != null && !selfDescribing.isEmpty()) {
            desc.appendText(message);
            desc.appendList("    ", "\n    ", "\n", new ArrayList<T>(selfDescribing));
        }
    }

    @SuppressWarnings("unchecked")
    static MethodAdapter guessMethod(Class interceptedClass, String methodName, boolean isStatic, Class[] paramSignature, Object[] params) {
        ArrayList<MethodAdapter> candidates = new ArrayList<MethodAdapter>();
        for (Class clazz = interceptedClass; clazz != null; clazz = clazz.getSuperclass()) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(methodName) && Modifier.isStatic(m.getModifiers()) == isStatic) {
                    candidates.add(new MethodAdapter(m));
                }
            }
        }

        List<MethodAdapter> likelyMatches = guessInvocable(paramSignature, params, candidates);

        if (likelyMatches.size() > 1) {
            throw new MultipleMethodsFoundException("Multiple methods named \"" + methodName + "\" found on class " + interceptedClass.getName() + " matching specified parameters/signature");
        } else if (likelyMatches.isEmpty()) {
            throw new NoMethodFoundException("No method \"" + methodName + "\" found on class " + interceptedClass.getName() + " matching specified parameters/signature");
        }

        return likelyMatches.get(0);
    }

    @SuppressWarnings("unchecked")
    private static <T extends InvocableAdapter> List<T> guessInvocable(Class[] paramSignature, Object[] params, Iterable<T> candidates) {
        if (paramSignature == null) {
            paramSignature = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                paramSignature[i] = (params[i] != null) ? params[i].getClass() : null;
            }
        }

        List<T> exactMatches = new ArrayList<T>(), varargsMatches = new ArrayList<T>();
candidateLoop:
        for (T adapter : candidates) {
            Class[] mSignature = adapter.getParameterTypes();
            if (!adapter.isVarArgs()) {
                if (paramSignature.length != mSignature.length) {
                    continue;
                }
            } else if (paramSignature.length-1 < mSignature.length) {
                continue;
            } else if (paramSignature.length > mSignature.length) {
                Class[] newSignature = new Class[paramSignature.length];
                System.arraycopy(mSignature, 0, newSignature, 0, mSignature.length-1);
                for (int i = mSignature.length-1; i < newSignature.length; i++) {
                    newSignature[i] = mSignature[mSignature.length-1].getComponentType();
                }
                mSignature = newSignature;
            } else if (paramSignature[paramSignature.length-1] != null && !mSignature[mSignature.length-1].isAssignableFrom(paramSignature[paramSignature.length-1])) {
                mSignature[mSignature.length-1] = mSignature[mSignature.length-1].getComponentType();
            }
            for (int i = 0; i < paramSignature.length; i++) {
                Class paramClass = toNonPrimitive(paramSignature[i]);
                Class mClass = (i >= mSignature.length ? mSignature[mSignature.length-1] : mSignature[i]);
                if (paramClass != null && !toNonPrimitive(mClass).isAssignableFrom(paramClass)) {
                    continue candidateLoop;
                }
            }
            (adapter.isVarArgs() ? varargsMatches : exactMatches).add(adapter);
        }
        return !exactMatches.isEmpty() ? exactMatches : varargsMatches;
    }

    @SuppressWarnings("unchecked")
    static <T> Class<T[]> arrayClassFor(Class<T> clazz) {
        return (Class<T[]>) Array.newInstance(clazz, 0).getClass();
    }

    static boolean unbox(Boolean bool, boolean defaultIfBoolIsNull) {
        return bool != null ? bool : defaultIfBoolIsNull;
    }

    static ConstructorAdapter guessConstructor(Class interceptedClass, Class[] paramSignature, Object[] params) {
        ArrayList<ConstructorAdapter> candidates = new ArrayList<ConstructorAdapter>();
        for (Constructor constructor : interceptedClass.getConstructors()) {
            candidates.add(new ConstructorAdapter(constructor));
        }

        List<ConstructorAdapter> likelyMatches = guessInvocable(paramSignature, params, candidates);

        if (likelyMatches.size() > 1) {
            throw new MultipleMethodsFoundException("Multiple plausible constructors found on class " + interceptedClass.getName() + " matching specified parameters/signature");
        } else if (likelyMatches.isEmpty()) {
            throw new NoMethodFoundException("No plausible constructor found on class " + interceptedClass.getName() + " matching specified parameters/signature");
        }

        return likelyMatches.get(0);
    }

    static interface Factory<F> {
        F create();
    }

    static class MultipleMethodsFoundException extends IllegalArgumentException {
        MultipleMethodsFoundException(String message) {
            super(message);
        }
    }

    static class NoMethodFoundException extends IllegalArgumentException {
        NoMethodFoundException(String message) {
            super(message);
        }
    }
}
