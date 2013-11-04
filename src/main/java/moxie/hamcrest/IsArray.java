package moxie.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Hamcrest's IsArray matcher doesn't look as if it works on primitive arrays.
public class IsArray<T> extends BaseMatcher<T> {
    private final List<Matcher> elementMatchers;

    @SuppressWarnings("unchecked")
    public IsArray(Matcher... elementMatchers) {
        this((List) Arrays.asList(elementMatchers));
    }

    public IsArray(List<Matcher> elementMatchers) {
        this.elementMatchers = elementMatchers;
    }

    static public <T> IsArray<T[]> array(Matcher<? super T>... elementMatchers) {
        return new IsArray<T[]>(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public <T> IsArray<T[]> array(List<Matcher<? super T>> elementMatchers) {
        return new IsArray<T[]>((List) elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<boolean[]> booleanArray(Matcher<? super Boolean>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<byte[]> byteArray(Matcher<? super Byte>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<char[]> charArray(Matcher<? super Character>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<short[]> shortArray(Matcher<? super Short>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<int[]> intArray(Matcher<? super Integer>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<long[]> longArray(Matcher<? super Long>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<float[]> floatArray(Matcher<? super Float>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<double[]> doubleArray(Matcher<? super Double>... elementMatchers) {
        return new IsArray(elementMatchers);
    }

    @SuppressWarnings("unchecked")
    static private IsArray arrayEquals(final Object arrayValue) {
        List<Matcher> matchers = new ArrayList<Matcher>();
        int arraySize = Array.getLength(arrayValue);
        for (int i = 0; i < arraySize; i++) {
            Object element = Array.get(arrayValue, i);
            matchers.add(IsEqual.equalTo(element));
        }
        return new IsArray(matchers);
    }

    @SuppressWarnings("unchecked")
    static public <T> IsArray<T[]> arrayEqualTo(T... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<boolean[]> booleanArrayEqualTo(boolean... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<byte[]> byteArrayEqualTo(byte... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<char[]> charArrayEqualTo(char... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<short[]> shortArrayEqualTo(short... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<int[]> intArrayEqualTo(int... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<long[]> longArrayEqualTo(long... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<float[]> floatArrayEqualTo(float... elements) {
        return arrayEquals(elements);
    }

    @SuppressWarnings("unchecked")
    static public IsArray<double[]> doubleArrayEqualTo(double... elements) {
        return arrayEquals(elements);
    }


    public boolean matches(Object o) {
        if (o == null || !o.getClass().isArray()) {
            return false;
        }
        int arraySize = Array.getLength(o);
        if (elementMatchers.size() != arraySize) {
            return false;
        }
        for (int i = 0; i < arraySize; i++) {
            if (!elementMatchers.get(i).matches(Array.get(o, i))) {
                return false;
            }
        }
        return true;
    }

    public void describeTo(Description description) {
        description.appendList("[", ", ", "]", elementMatchers);
    }
}
