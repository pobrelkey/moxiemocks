package moxie.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.regex.Pattern;

public class MatchesRegexp<T> extends BaseMatcher<T> {
    private final Pattern pattern;

    public MatchesRegexp(Pattern pattern) {
        this.pattern = pattern;
    }

    public boolean matches(Object o) {
        return (o != null) && pattern.matcher(o.toString()).matches();
    }

    public void describeTo(Description description) {
        description.appendText("matches /" + pattern.pattern().replace("/", "\\/") + '/');
    }

    public static <T> MatchesRegexp<T> matchesRegexp(Pattern pattern) {
        return new MatchesRegexp<T>(pattern);
    }

    public static <T> MatchesRegexp<T> matchesRegexp(String pattern) {
        return new MatchesRegexp<T>(Pattern.compile(pattern));
    }

}
