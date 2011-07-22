/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.api.extension.proxyframework;

import org.powermock.reflect.spi.ProxyFramework;

import java.lang.reflect.Method;

/**
 * <p>
 * Internal class used by PowerMock.
 * </p>
 * <p>
 * Implemented in an org.powermock package because the core PowerMock library leaves this class unimplemented -
 * it instists that you provide a separate implementation specific to your mock object library.  (This is mostly
 * a consequence of Mockito using their own fork of CGLIB.)  This implementation should work correctly for Moxie,
 * EasyMock and Mockito, and in theory should allow you to use PowerMock with all three in the same classpath/JVM
 * (I'm not daft enough to actually try this though).
 * </p>
 *
 */
public class ProxyFrameworkImpl implements ProxyFramework {

    private static final Method IS_CGLIB_ENHANCED = isEnhancedMethod("net.sf.cglib.proxy.Enhancer");
    private static final Method IS_MOCKITOCGLIB_ENHANCED = isEnhancedMethod("org.mockito.cglib.proxy.Enhancer");

    private static Method isEnhancedMethod(String className) {
        try {
            return Class.forName(className).getMethod("isEnhanced", Class.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getUnproxiedType(Class<?> type) {
        while (isProxy(type)) {
            for (Class<?> i : type.getInterfaces()) {
                if (!"net.sf.cglib.proxy.Factory".equals(i.getName()) &&
                        !"org.mockito.cglib.proxy.Factory".equals(i.getName())) {
                    return i;
                }
            }
            type = type.getSuperclass();
        }
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isProxy(Class<?> type) {
        return type != null &&
                (type.getName().contains("$$EnhancerByCGLIB$$") ||
                        type.getName().contains("$$EnhancerByMockitoWithCGLIB$$") ||
                        isEnhanced(type, IS_CGLIB_ENHANCED) ||
                        isEnhanced(type, IS_MOCKITOCGLIB_ENHANCED));
    }

    private boolean isEnhanced(Class<?> type, Method method) {
        try {
            return (method != null) && (Boolean) method.invoke(null, type);
        } catch (Exception e) {
            return false;
        }
    }
}
