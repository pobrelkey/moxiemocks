package moxie.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import java.lang.reflect.Array;

// Hamcrest's IsArrayContaining matcher doesn't look as if it works on primitive arrays.
public class IsArrayContaining<T> extends BaseMatcher<T[]> {
    private final Matcher elementMatcher;

    public IsArrayContaining(Matcher<? super T> elementMatcher) {
        this.elementMatcher = elementMatcher;
    }

    public static <T> IsArrayContaining<T> hasItemInArray(Matcher<? super T> elementMatcher) {
        return new IsArrayContaining<T>(elementMatcher);
    }

    public static <T> IsArrayContaining<T> hasItemInArray(T element) {
        return new IsArrayContaining<T>(IsEqual.equalTo(element));
    }

    public boolean matches(Object o) {
        if (o == null || !o.getClass().isArray()) {
            return false;
        }
        int arraySize = Array.getLength(o);
        for (int i = 0; i < arraySize; i++) {
            if (elementMatcher.matches(Array.get(o, i))) {
                return true;
            }
        }
        return false;
    }

    public void describeTo(Description description) {
        description.appendText("an array containing ");
        elementMatcher.describeTo(description);
    }
}
