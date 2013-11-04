package moxie.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

// Hamcrest's IsCloseTo matcher only works on Doubles.
public class IsCloseTo<T extends Number> extends BaseMatcher<T> {
    private final double value;
    private final double delta;

    public IsCloseTo(double value, double delta) {
        this.value = value;
        this.delta = delta;
    }

    public static <T extends Number> IsCloseTo<T> closeTo(final double value, final double delta) {
        return new IsCloseTo<T>(value, delta);
    }

    public boolean matches(Object o) {
        if (o == null || !(o instanceof Number)) {
            return false;
        }
        return Math.abs(((Number) o).doubleValue() - value) <= delta;
    }

    public void describeTo(Description description) {
        description.appendText("a Number within ").appendValue(delta).appendText(" of ").appendValue(value);
    }
}
