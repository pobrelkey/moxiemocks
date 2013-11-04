package moxie.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class OrderingComparison<T extends Comparable<T>> extends BaseMatcher<T> {

    private final boolean lessThan;
    private final boolean equalTo;
    private final boolean greaterThan;
    private final T value;

    public OrderingComparison(boolean lessThan, boolean equalTo, boolean greaterThan, T value) {
        this.lessThan = lessThan;
        this.equalTo = equalTo;
        this.greaterThan = greaterThan;
        this.value = value;
    }

    public static <T extends Comparable<T>> OrderingComparison<T> greaterThanOrEqualTo(T value) {
        return new OrderingComparison<T>(false, true, true, value);
    }

    public static <T extends Comparable<T>> OrderingComparison<T> greaterThan(T value) {
        return new OrderingComparison<T>(false, false, true, value);
    }

    public static <T extends Comparable<T>> OrderingComparison<T> lessThanOrEqualTo(T value) {
        return new OrderingComparison<T>(true, true, false, value);
    }

    public static <T extends Comparable<T>> OrderingComparison<T> lessThan(T value) {
        return new OrderingComparison<T>(true, false, false, value);
    }

    public static <T extends Comparable<T>> OrderingComparison<T> comparesEqualTo(T value) {
        return new OrderingComparison<T>(false, true, false, value);
    }

    public void describeTo(Description description) {
        description.appendText("a value ");
        if (lessThan) {
            description.appendText("less than ");
        }
        if (equalTo) {
            if (lessThan) {
                description.appendText("or ");
            }
            description.appendText("equal to ");
        }
        if (greaterThan) {
            if (lessThan || equalTo) {
                description.appendText("or ");
            }
            description.appendText("greater than ");
        }
        description.appendValue(value);
    }

    public boolean matches(Object o) {
        if (o == null) {
            return false;
        }
        @SuppressWarnings("unchecked")
        int result = value.compareTo((T) o);
        return (result > 0 && lessThan) ||
                (result == 0 && equalTo) ||
                (result < 0 && greaterThan);
    }
}
