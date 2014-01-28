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
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;

import java.util.Collection;

/**
 * Matches any {@link Collection} whose size satisfies a nested {@link Matcher}.
 * @param <T> type of the <code>Collection</code> to be matched
 */
public class IsCollectionWithSize<T extends Collection> extends TypeSafeMatcher<T> {
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

    @Override
    protected boolean matchesSafely(T item) {
        return sizeMatcher.matches(item.size());
    }

    public void describeTo(Description description) {
        description.appendText("a collection with size ");
        sizeMatcher.describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText("collection size ");
        sizeMatcher.describeMismatch(item.size(), mismatchDescription);
    }
}
