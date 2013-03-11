/*
 * Copyright (c) 2011-2013 Moxie contributors
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

import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

abstract class ConcreteTypeProxyFactory<T> extends ProxyFactory<T> {

    protected static boolean haveObjenesis = false;
    protected static ObjenesisStd objenesis;
    protected final Class<? extends T> enhancedClass;
    protected ObjectInstantiator objenesisInstantiator;
    protected final Class<T> originalClass;

    static {
        try {
            objenesis = new ObjenesisStd(true);
            haveObjenesis = true;
        } catch (NoClassDefFoundError e) {
            // oh well, no objenesis then.
        }
    }

    protected ConcreteTypeProxyFactory(Class<T> clazz, Class[] ancillaryTypes) {
        originalClass = clazz;
        if (!haveObjenesis && clazz.getDeclaringClass() != null && !Modifier.isStatic(clazz.getModifiers())) {
            throw new IllegalArgumentException("Cannot mock a non-static inner class (" + clazz.getName() + ") - add Objenesis to the classpath to get around this");
        }
        enhancedClass = createEnhancedClass(clazz, ancillaryTypes);
        if (haveObjenesis) {
            objenesisInstantiator = objenesis.getInstantiatorOf(enhancedClass);
        }
    }

    @SuppressWarnings("unchecked")
    T createProxy(final MethodIntercept methodIntercept, Class[] constructorArgTypes, Object[] constructorArgs) {
        T result;
        if (!haveObjenesis || constructorArgTypes != null || constructorArgs != null) {
            try {
                Constructor constructor;
                if (constructorArgTypes == null && constructorArgs != null && constructorArgs.length > 0) {
                    constructor = MoxieUtils.guessConstructor(enhancedClass, constructorArgTypes, constructorArgs).getConstructor();
                } else {
                    constructor = enhancedClass.getConstructor(constructorArgTypes);
                }
                constructor.setAccessible(true);
                result = (T) constructor.newInstance(constructorArgs);
            } catch (InstantiationException e) {
                throw new MoxieUnexpectedError(e);
            } catch (IllegalAccessException e) {
                throw new MoxieUnexpectedError(e);
            } catch (InvocationTargetException e) {
                throw new MoxieUnexpectedError(e.getTargetException());
            } catch (NoSuchMethodException e) {
                if (!haveObjenesis && (constructorArgTypes == null || constructorArgTypes.length == 0) && (constructorArgs == null || constructorArgs.length == 0)) {
                    throw new IllegalArgumentException("To mock concrete types that don't have no-arg constructors, either pass constructor arguments or add Objenesis to the classpath");
                }
                throw new MoxieUnexpectedError(e);
            } catch (MoxieUtils.NoMethodFoundException e) {
                if (!haveObjenesis && (constructorArgTypes == null || constructorArgTypes.length == 0) && (constructorArgs == null || constructorArgs.length == 0)) {
                    throw new IllegalArgumentException("To mock concrete types that don't have no-arg constructors, either pass constructor arguments or add Objenesis to the classpath");
                }
                throw new MoxieUnexpectedError(e);
            }
        } else {
            // no specific constructor requested and have Objenesis, so use that instead.
            result = (T) objenesisInstantiator.newInstance();
        }

        decorateInstance(result, methodIntercept);

        ProxyIntercepts.registerInterception(result, methodIntercept);
        return result;
    }

    @SuppressWarnings("unchecked")
    protected abstract Class<? extends T> createEnhancedClass(Class<T> clazz, Class[] ancillaryTypes);

    protected abstract void decorateInstance(T result, MethodIntercept methodIntercept);
}
