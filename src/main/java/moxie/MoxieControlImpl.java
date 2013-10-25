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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class MoxieControlImpl implements MoxieControl {

    private final IdentityHashMap<Object, Verifiable> mocksAndGroups = new IdentityHashMap<Object, Verifiable>();
    private final IdentityHashMap<Object, Map<String, Object>> valuesOverwrittenByAutoMock = new IdentityHashMap<Object, Map<String, Object>>();
    private final List<Invocation> invocations = Collections.synchronizedList(new ArrayList<Invocation>());
    private int groupNameCounter = 0;


    public <T> T mock(Class<T> clazz) {
        return mock(clazz, null, (Class[]) null, (Object[]) null, MoxieOptions.MOCK_DEFAULTS);
    }

    public <T> T mock(Class<T> clazz, String name) {
        return mock(clazz, name, (Class[]) null, (Object[]) null, MoxieOptions.MOCK_DEFAULTS);
    }

    public <T> T mock(Class<T> clazz, MoxieOptions... options) {
        return mock(clazz, null, (Class[]) null, (Object[]) null, options);
    }

    public <T> T mock(Class<T> clazz, String name, MoxieOptions... options) {
        return mock(clazz, name, (Class[]) null, (Object[]) null, options);
    }

    public <T> T mock(Class<T> clazz, Object... constructorArgs) {
        return mock(clazz, null, (Class[]) null, constructorArgs, MoxieOptions.MOCK_DEFAULTS);
    }

    public <T> T mock(Class<T> clazz, Class[] constructorArgTypes, Object[] constructorArgs) {
        return mock(clazz, null, constructorArgTypes, constructorArgs, MoxieOptions.MOCK_DEFAULTS);
    }

    public <T> T mock(Class<T> clazz, String name, Object[] constructorArgs) {
        return mock(clazz, name, (Class[]) null, constructorArgs, MoxieOptions.MOCK_DEFAULTS);
    }

    public <T> T mock(Class<T> clazz, String name, Class[] constructorArgTypes, Object[] constructorArgs) {
        return mock(clazz, name, constructorArgTypes, constructorArgs, MoxieOptions.MOCK_DEFAULTS);
    }

    public <T> T mock(Class<T> clazz, Object[] constructorArgs, MoxieOptions... options) {
        return mock(clazz, null, (Class[]) null, constructorArgs, options);
    }

    public <T> T mock(Class<T> clazz, Class[] constructorArgTypes, Object[] constructorArgs, MoxieOptions... options) {
        return mock(clazz, null, constructorArgTypes, constructorArgs, options);
    }

    public <T> T mock(Class<T> clazz, String name, Object[] constructorArgs, MoxieOptions... options) {
        return mock(clazz, name, (Class[]) null, constructorArgs, options);
    }

    public <T> T mock(Class<T> clazz, String name, Class[] constructorArgTypes, Object[] constructorArgs, MoxieOptions... options) {
        MoxieFlags flags = MoxieOptions.mergeWithDefaults(MoxieOptions.MOCK_DEFAULTS, options);
        if (name == null || name.length() == 0) {
            name = clazz.getSimpleName();
        }
        if (MoxieUtils.unbox(flags.isPartial(), false) && clazz.isInterface()) {
            throw new MoxieSyntaxError("Cannot partially mock an interface");
        }
        @SuppressWarnings("unchecked")
        MockImpl<T> mock = new MockImpl(clazz, name, flags, invocations, constructorArgTypes, constructorArgs);
        T result = mock.getProxy();
        mocksAndGroups.put(result, mock);
        return result;
    }

    public <T> T spy(T realObject, MoxieOptions... options) {
        return spy(realObject, null, options);
    }

    public <T> T spy(T realObject, String name, MoxieOptions... options) {
        if (name == null || name.length() == 0) {
            name = realObject.getClass().getSimpleName();
        }
        @SuppressWarnings("unchecked")
        SpyImpl<T> spy = new SpyImpl(realObject, name, MoxieOptions.mergeWithDefaults(MoxieOptions.MOCK_DEFAULTS, options), invocations);
        T result = spy.getProxy();
        mocksAndGroups.put(result, spy);
        return result;
    }

    public Group group(MoxieOptions... options) {
        return group(null, options);
    }

    public Group group(String name, MoxieOptions... options) {
        if (name == null) {
            name = "[unnamed group " + (groupNameCounter++);
            String externalCallerString = MoxieUtils.getExternalCallerString();
            if (externalCallerString != null) {
                name += " (" + externalCallerString + ") ";
            }
            name += ']';
        }
        GroupImpl group = new GroupImpl(name, MoxieOptions.mergeWithDefaults(MoxieOptions.GROUP_DEFAULTS, options));
        mocksAndGroups.put(group, group);
        return group;
    }

    @SuppressWarnings("unchecked")
    public <T> ObjectExpectation<T> expect(T mockObject) {
        return ((ObjectInterception<T>) getInterceptionFromProxy(mockObject)).expect();
    }

    @SuppressWarnings("unchecked")
    public <T> ClassExpectation<T> expect(Class<T> clazz) {
        return getInterceptionFromClass(clazz).expect();
    }

    @SuppressWarnings("unchecked")
    public <T> ClassExpectation<T> expect(Class<T> clazz, MoxieOptions... options) {
        return getInterceptionFromClass(clazz, options).expect();
    }

    public LambdaExpectation<Object> expect() {
        return new LambdaExpectationImpl<Object>(this);
    }

    public LambdaExpectation<Void> expect(ThrowingRunnable lambda) {
        return expect().that(lambda);
    }

    public <R> LambdaExpectation<R> expect(ThrowingSupplier<R> lambda) {
        return expect().that(lambda);
    }

    public <T> ObjectExpectation<T> stub(T mockObject) {
        return expect(mockObject).anyTimes().atAnyTime();
    }

    public <T> ClassExpectation<T> stub(Class<T> clazz) {
        return expect(clazz).anyTimes().atAnyTime();
    }

    public LambdaExpectation<Object> stub() {
        return expect().anyTimes().atAnyTime();
    }

    public LambdaExpectation<Void> stub(ThrowingRunnable lambda) {
        return stub().that(lambda);
    }

    public <R> LambdaExpectation<R> stub(ThrowingSupplier<R> lambda) {
        return stub().that(lambda);
    }

    @SuppressWarnings("unchecked")
    public <T> ObjectCheck<T> check(T mockObject) {
        return ((ObjectInterception<T>) getInterceptionFromProxy(mockObject)).check();
    }

    @SuppressWarnings("unchecked")
    public ClassCheck check(Class clazz) {
        return getInterceptionFromClass(clazz).check();
    }

    public LambdaCheck<Object> check() {
        return new LambdaCheckImpl<Object>(this, invocations);
    }

    public LambdaCheck<Void> check(ThrowingRunnable lambda) {
        return check().that(lambda);
    }

    public <R> LambdaCheck<R> check(ThrowingSupplier<R> lambda) {
        return check().that(lambda);
    }

    public void checkNothingElseHappened(Object... mockObjects) {
        ArrayList<Invocation> uncheckedInvocations = new ArrayList<Invocation>();
        for (Object mockProxy : mocksFor(mockObjects)) {
            Interception interception = getInterceptionFromProxy(mockProxy);
            for (Invocation invocation : (List<Invocation>) interception.getInvocations()) {
                if (invocation.getCheckSatisfied() == null) {
                    uncheckedInvocations.add(invocation);
                }
            }
        }
        if (!uncheckedInvocations.isEmpty()) {
            throw new MoxieUncheckedInvocationError("unchecked invocation(s) detected", uncheckedInvocations);
        }
    }

    public void checkNothingElseUnexpectedHappened(Object... mockObjects) {
        ArrayList<Invocation> uncheckedInvocations = new ArrayList<Invocation>();
        for (Object mockProxy : mocksFor(mockObjects)) {
            Interception interception = getInterceptionFromProxy(mockProxy);
            for (Invocation invocation : (List<Invocation>) interception.getInvocations()) {
                if (invocation.getCheckSatisfied() == null && invocation.getExpectationSatisfied() == null) {
                    uncheckedInvocations.add(invocation);
                }
            }
        }
        if (!uncheckedInvocations.isEmpty()) {
            throw new MoxieUncheckedInvocationError("unchecked and unexpected invocation(s) detected", uncheckedInvocations);
        }
    }

    private Verifiable getVerifiableFromProxy(Object mockProxy) {
        Verifiable verifiable = mocksAndGroups.get(mockProxy);
        if (verifiable == null) {
            throw new IllegalArgumentException("object is not a mock object or group, or is no longer active: " + mockProxy);
        }
        return verifiable;
    }

    Interception getInterceptionFromProxy(Object mockProxy) {
        Verifiable interception = mocksAndGroups.get(mockProxy);
        if (interception == null || !(interception instanceof Interception)) {
            throw new IllegalArgumentException("object is not a mock object or has already been verified: " + interception);
        }
        return ((Interception) interception);
    }

    <T> ClassInterception<T> getInterceptionFromClass(Class<T> clazz, MoxieOptions... options) {
        @SuppressWarnings("unchecked")
        ClassInterception<T> result = (ClassInterception<T>) mocksAndGroups.get(clazz);
        if (result == null) {
            MoxieFlags flags = MoxieOptions.mergeWithDefaults(MoxieOptions.MOCK_DEFAULTS, options);
            InstantiationStackTrace instantiationStackTrace = MoxieUtils.unbox(flags.isTracing(), false) ? new InstantiationStackTrace("class mock \"" + clazz.getSimpleName() + "\" was instantiated here") : null;
            mocksAndGroups.put(clazz, result = new ClassInterception<T>(clazz, clazz.getSimpleName(), flags, instantiationStackTrace));
        }
        return result;
    }

    public void verify(Object... mockObjects) {
        for (Object mockProxy : mocksAndGroupsFor(mockObjects)) {
            getVerifiableFromProxy(mockProxy).verify();
        }
        for (Object mockProxy : mocksAndGroupsFor(mockObjects)) {
            getVerifiableFromProxy(mockProxy).verifyNoBackgroundErrors();
            mocksAndGroups.remove(mockProxy);
        }
    }

    public void verifySoFar(Object... mockObjects) {
        for (Object mockProxy : mocksAndGroupsFor(mockObjects)) {
            getVerifiableFromProxy(mockProxy).verify();
        }
        for (Object mockProxy : mocksAndGroupsFor(mockObjects)) {
            getVerifiableFromProxy(mockProxy).verifyNoBackgroundErrors();
        }
    }

    public void verifyAndReset(Object... mockObjects) {
        for (Object mockProxy : mocksAndGroupsFor(mockObjects)) {
            Verifiable verifiable = getVerifiableFromProxy(mockProxy);
            verifiable.verify();
        }
        for (Object mockProxy : mocksAndGroupsFor(mockObjects)) {
            Verifiable verifiable = getVerifiableFromProxy(mockProxy);
            verifiable.verifyNoBackgroundErrors();
            verifiable.reset(null);
        }
    }

    public void reset(Object... mockObjects) {
        for (Object mockProxy : mocksAndGroupsFor(mockObjects)) {
            Verifiable verifiable = getVerifiableFromProxy(mockProxy);
            verifiable.reset(null);
        }
    }

    public void deactivate(Object... mockObjects) {
        for (Object mockProxy : mocksAndGroupsFor(mockObjects)) {
            Verifiable verifiable = getVerifiableFromProxy(mockProxy);
            verifiable.reset(MoxieOptions.PRESCRIPTIVE);
            mocksAndGroups.remove(mockProxy);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection mocksAndGroupsFor(Object... mockObjects) {
        if (mockObjects != null && mockObjects.length > 0) {
            return Arrays.asList(mockObjects);
        } else {
            return new ArrayList(mocksAndGroups.keySet());
        }
    }

    @SuppressWarnings("unchecked")
    private Collection mocksFor(Object... mockObjects) {
        if (mockObjects != null && mockObjects.length > 0) {
            return Arrays.asList(mockObjects);
        } else {
            ArrayList result = new ArrayList();
            for (Map.Entry<Object, Verifiable> entry : mocksAndGroups.entrySet()) {
                if (entry.getValue() instanceof Interception) {
                    result.add(entry.getKey());
                }
            }
            return result;
        }
    }

    public void verifyAndReset(Object mockObject, MoxieOptions firstOption, MoxieOptions... otherOptions) {
        Verifiable verifiable = getVerifiableFromProxy(mockObject);
        verifiable.verify();
        verifiable.verifyNoBackgroundErrors();
        verifiable.reset(MoxieOptions.merge(firstOption, MoxieOptions.merge(otherOptions)));
    }

    public void reset(Object mockObject, MoxieOptions firstOption, MoxieOptions... otherOptions) {
        Verifiable verifiable = getVerifiableFromProxy(mockObject);
        verifiable.reset(MoxieOptions.merge(firstOption, MoxieOptions.merge(otherOptions)));
    }

    public void checkNoActiveMocks() {
        if (!mocksAndGroups.isEmpty()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.append("The following mocks/sequences were not verified:\n");
            for (Verifiable v : mocksAndGroups.values()) {
                @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                Throwable whereInstantiated = v.getWhereInstantiated();
                if (whereInstantiated != null) {
                    whereInstantiated.printStackTrace(pw);
                } else {
                    pw.println(v.getName());
                }
            }
            pw.flush();
            throw new IllegalStateException(sw.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public Object[] autoMock(Object... testComponents) {
        for (Object testInstance : testComponents) {
            if (valuesOverwrittenByAutoMock.containsKey(testInstance)) {
                throw new IllegalArgumentException("object has already been autoMock()'ed: " + testInstance);
            }
        }
        ArrayList result = new ArrayList();
        for (Object testInstance : testComponents) {
            Map<String, Object> oldValues = new HashMap<String, Object>();
            valuesOverwrittenByAutoMock.put(testInstance, oldValues);
            for (Class c = testInstance.getClass(); c != null; c = c.getSuperclass()) {
                for (Field f : c.getDeclaredFields()) {
                    Mock mockAnnotation = f.getAnnotation(Mock.class);
                    Spy spyAnnotation = f.getAnnotation(Spy.class);
                    AutoMock autoMockAnnotation = f.getAnnotation(AutoMock.class);
                    boolean isGroup = Group.class.equals(f.getType());
                    if (mockAnnotation != null || spyAnnotation != null || autoMockAnnotation != null || isGroup) {
                        if (!f.isAccessible()) {
                            f.setAccessible(true);
                        }
                        try {
                            Object testObject;
                            if (autoMockAnnotation != null) {
                                autoMock(f.get(testInstance));
                                continue;
                            } else if (isGroup) {
                                GroupOptions optionsAnnotation = f.getAnnotation(GroupOptions.class);
                                testObject = Moxie.group(f.getName(), optionsAnnotation != null ? optionsAnnotation.value() : new MoxieOptions[0]);
                            } else if (spyAnnotation != null) {
                                testObject = Moxie.spy(f.get(testInstance), f.getName(), spyAnnotation.value());
                            } else {
                                testObject = Moxie.mock(f.getType(), f.getName(), mockAnnotation.value());
                            }
                            oldValues.put(f.getName(), f.get(testInstance));
                            f.set(testInstance, testObject);
                            result.add(testObject);
                        } catch (Exception ex) {
                            throw new MoxieUnexpectedError("Reflection error when auto-mocking field " + f.getName() + " on object " + testInstance, ex);
                        }
                    }
                }
            }
        }
        return result.toArray(new Object[result.size()]);
    }

    @SuppressWarnings("unchecked")
    public void autoUnMock(Object... testComponents) {
        Collection components;
        if (testComponents != null && testComponents.length > 0) {
            for (Object testInstance : testComponents) {
                if (!valuesOverwrittenByAutoMock.containsKey(testInstance)) {
                    throw new IllegalArgumentException("object was not autoMock()'ed: " + testInstance);
                }
            }
            components = Arrays.asList(testComponents);
        } else {
            components = new ArrayList(valuesOverwrittenByAutoMock.keySet());
        }

        for (Object testInstance : components) {
            Map<String, Object> oldValues = valuesOverwrittenByAutoMock.remove(testInstance);
            for (Class c = testInstance.getClass(); c != null; c = c.getSuperclass()) {
                for (Field f : c.getDeclaredFields()) {
                    if (f.getAnnotation(Mock.class) != null || f.getAnnotation(Spy.class) != null || f.getAnnotation(AutoMock.class) != null || Group.class.equals(f.getType())) {
                        if (!f.isAccessible()) {
                            f.setAccessible(true);
                        }
                        try {
                            if (f.getAnnotation(AutoMock.class) != null) {
                                autoUnMock(f.get(testInstance));
                                continue;
                            }
                            f.set(testInstance, oldValues.get(f.getName()));
                        } catch (Exception ex) {
                            throw new MoxieUnexpectedError("Reflection error when auto-unmocking field " + f.getName() + " on object " + testInstance, ex);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    <T> List<T> getProxiesForClass(Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        for (Object o : mocksAndGroups.keySet()) {
            if (clazz.isAssignableFrom(o.getClass())) {
                result.add((T) o);
            }
        }
        return result;
    }

    Set<Object> getAllProxies() {
        return mocksAndGroups.keySet();
    }
}
