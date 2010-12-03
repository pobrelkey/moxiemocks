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

import net.sf.cglib.core.ClassGenerator;
import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.core.VisibilityPredicate;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import org.hamcrest.SelfDescribing;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class MoxieUtils {

    private static final Character ZERO_CHAR = new Character('\0');
    private static final Byte ZERO_BYTE = new Byte((byte) 0);
    private static final Short ZERO_SHORT = new Short((short) 0);
    private static final Integer ZERO_INT = new Integer(0);
    private static final Long ZERO_LONG = new Long((long) 0);
    private static final Double ZERO_DOUBLE = new Double((double) 0);
    private static final Float ZERO_FLOAT = new Float((float) 0);

    static <T> T defaultValue(Class<T> clazz) {
        if (clazz == Boolean.TYPE) {
            return (T) Boolean.FALSE;
        } else if (clazz == Character.TYPE) {
            return (T) ZERO_CHAR;
        } else if (clazz == Byte.TYPE) {
            return (T) ZERO_BYTE;
        } else if (clazz == Short.TYPE) {
            return (T) ZERO_SHORT;
        } else if (clazz == Integer.TYPE) {
            return (T) ZERO_INT;
        } else if (clazz == Long.TYPE) {
            return (T) ZERO_LONG;
        } else if (clazz == Float.TYPE) {
            return (T) ZERO_FLOAT;
        } else if (clazz == Double.TYPE) {
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

    static public <T> T createThreadLocalProxy(final Class<T> clazz, final Factory<T> factory) {
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
                } catch (IllegalAccessException e) {
                    throw e;
                } catch (IllegalArgumentException e) {
                    throw e;
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        });
    }

    static public <T> T newProxyInstance(Class<T> clazz, final InvocationHandler invocationHandler) {
        return newProxyInstance(clazz, invocationHandler, new Class[0], new Object[0]);
    }

    static public <T> T newProxyInstance(Class<T> clazz, final InvocationHandler invocationHandler, Class[] constructorArgTypes, Object[] constructorArgs) {
        if (clazz.isInterface()) {
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, invocationHandler);
        } else {
            Enhancer e = new Enhancer();
            e.setSuperclass(clazz);
            e.setCallback(new net.sf.cglib.proxy.InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return invocationHandler.invoke(proxy, method, args);
                }
            });
            if (constructorArgTypes != null && constructorArgTypes.length > 0) {
                // TODO: doesn't work! WTF cglib!
                return (T) e.create(constructorArgTypes, constructorArgs);
            } else {
                return (T) e.create();
            }
        }
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

    static interface Factory<F> {
        F create();
    }
}
