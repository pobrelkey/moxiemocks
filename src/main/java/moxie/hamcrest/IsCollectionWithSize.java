package moxie.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import java.util.Collection;

public class IsCollectionWithSize<T extends Collection> extends BaseMatcher<T> {
    private final Matcher<? super Integer> sizeMatcher;

    public IsCollectionWithSize(Matcher<? super Integer> sizeMatcher) {
        this.sizeMatcher = sizeMatcher;
    }

    static public <T extends Collection> IsCollectionWithSize<T> collectionWithSize(Matcher<? super Integer> sizeMatcher) {
        return new IsCollectionWithSize<T>(sizeMatcher);
    }

    static public <T extends Collection> IsCollectionWithSize<T> collectionWithSize(int size) {
        return collectionWithSize(IsEqual.equalTo(size));
    }

    public boolean matches(Object o) {
        return o != null && sizeMatcher.matches(((Collection) o).size());
    }

    public void describeTo(Description description) {
        description.appendText("a collection with size ");
        sizeMatcher.describeTo(description);
    }
}
