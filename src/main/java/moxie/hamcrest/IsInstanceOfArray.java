package moxie.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class IsInstanceOfArray extends BaseMatcher {
    private static final BaseMatcher INSTANCE = new IsInstanceOfArray();

    private IsInstanceOfArray() { }

    public static BaseMatcher instanceOfArray() {
        return INSTANCE;
    }

    public boolean matches(Object o) {
        return o != null && o.getClass().isArray();
    }

    public void describeTo(Description description) {
        description.appendText("any array");
    }
}
