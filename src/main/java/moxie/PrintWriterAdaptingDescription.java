/*
 * Copyright (c) 2010 Moxie contributors
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

package moxie;

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;

public class PrintWriterAdaptingDescription implements Description {

    private final PrintWriter pw;

    public PrintWriterAdaptingDescription(PrintWriter pw) {
        this.pw = pw;
    }

    public Description appendText(String s) {
        pw.print(s);
        return this;
    }

    public Description appendDescriptionOf(SelfDescribing selfDescribing) {
        selfDescribing.describeTo(this);
        return this;
    }

    public Description appendValue(Object o) {
        pw.print(o);
        return this;
    }

    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        return appendValueList(start, separator, end, Arrays.asList(values));
    }

    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
        pw.print(start);
        boolean first = true;
        for (T value : values) {
            if (!first) {
                pw.print(separator);
            }
            first = false;
            pw.print(value);
        }
        pw.print(end);
        return this;
    }

    public Description appendList(String start, String separator, String end, Iterable<? extends SelfDescribing> values) {
        pw.print(start);
        boolean first = true;
        for (SelfDescribing value : values) {
            if (!first) {
                pw.print(separator);
            }
            first = false;
            value.describeTo(this);
        }
        pw.print(end);
        return this;
    }
}
