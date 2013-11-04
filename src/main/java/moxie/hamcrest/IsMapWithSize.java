package moxie.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import java.util.Map;

public class IsMapWithSize<T extends Map> extends BaseMatcher<T> {
    private final Matcher<? super Integer> sizeMatcher;

    public IsMapWithSize(Matcher<? super Integer> sizeMatcher) {
        this.sizeMatcher = sizeMatcher;
    }

    static public <T extends Map> IsMapWithSize<T> mapWithSize(Matcher<? super Integer> sizeMatcher) {
        return new IsMapWithSize<T>(sizeMatcher);
    }

    static public <T extends Map> IsMapWithSize<T> mapWithSize(int size) {
        return mapWithSize(IsEqual.equalTo(size));
    }

    public boolean matches(Object o) {
        return o != null && sizeMatcher.matches(((Map) o).size());
    }

    public void describeTo(Description description) {
        description.appendText("a map with size ");
        sizeMatcher.describeTo(description);
    }
}
