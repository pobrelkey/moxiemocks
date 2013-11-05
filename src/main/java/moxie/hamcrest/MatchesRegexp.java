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
