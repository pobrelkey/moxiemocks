/*
 * Copyright (c) 2011-2013 Moxie contributors
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

package moxietests;

import org.junit.Assert;

public class PartiallyMocked {
    public PartiallyMocked(String blah) {
        // constructor that we can mock with PowerMock
        Assert.fail("original constructor got called");
    }

    public static String aStaticMethod(String arg) {
        return "hi there, " + arg;
    }

    public String partsAandB(String arg) {
        return partA("{" + arg + "}") + " " + partB("[" + arg + "]");
    }

    public String partA(String arg) {
        return "second(" + arg + ")";
    }

    public String partB(String arg) {
        return "third(" + arg + ")";
    }

    public String partsBCandD(String arg) {
        return partB("[" + arg + "]") + " " + partC("<" + arg + ">") + " " + partD(arg);
    }

    protected String partC(String arg) {
        return "cinquieme(" + arg + ")";
    }

    /* package private */ String partD(String arg) {
        return "sixieme(" + arg + ")";
    }

    public String partsDEandF(String arg) {
        return partD(arg) + " " + partE("_" + arg + "_") + " " + partF("*" + arg + "*");
    }

    private String partE(String s) {
        return "EIGHT("+s+")";
    }

    public final String partF(String s) {
        return "NINE("+s+")";
    }
}
