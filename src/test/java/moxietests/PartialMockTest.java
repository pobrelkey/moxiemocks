/*
 * Copyright (c) 2011 Moxie contributors
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

import moxie.Moxie;
import moxie.MoxieMatchers;
import moxie.MoxieOptions;
import moxie.MoxieRule;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class PartialMockTest {

    @Rule
    public MoxieRule moxie = new MoxieRule();

    @Test
    public void partialMock_happyPath1() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL);
        Moxie.expect(mock).on().partsAandB("foo");
        Moxie.expect(mock).on().partA("{foo}");
        Moxie.expect(mock).on().partB("[foo]");
        Assert.assertEquals("second({foo}) third([foo])", mock.partsAandB("foo"));
    }

    @Test
    public void partialMock_happyPath2() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Assert.assertEquals("second({bar}) third([bar])", mock.partsAandB("bar"));
    }

    @Test
    public void partialMock_happyPath3() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Moxie.expect(mock).andReturn("SOMETHING_ELSE").on().partB("[baz]");
        Assert.assertEquals("second({baz}) SOMETHING_ELSE", mock.partsAandB("baz"));
    }

    @Test
    public void callOriginal_happyPath() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class);
        Moxie.expect(mock).andCallOriginal().on().partsAandB("moe");
        Moxie.expect(mock).andReturn("lenny").on().partA("{moe}");
        Moxie.expect(mock).andReturn("carl").on().partB("[moe]");
        Assert.assertEquals("lenny carl", mock.partsAandB("moe"));
    }

    @Test
    public void reflectiveExpect_on2_happyPath() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Moxie.expect(mock).andReturn("kumquat").on("partC", MoxieMatchers.hasSubstring("boo!"));
        Assert.assertEquals("third([boo!]) kumquat sixieme(boo!)", mock.partsBCandD("boo!"));
        Assert.assertEquals("third([booga!]) cinquieme(<booga!>) sixieme(booga!)", mock.partsBCandD("booga!"));
    }

    @Test
    public void reflectiveExpect_when2_happyPath() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Moxie.expect(mock).andReturn("[REDACTED]").when("partD", "seekrit");
        Assert.assertEquals("third([seekrit]) cinquieme(<seekrit>) [REDACTED]", mock.partsBCandD("seekrit"));
        Assert.assertEquals("third([not so secret]) cinquieme(<not so secret>) sixieme(not so secret)", mock.partsBCandD("not so secret"));
    }

    @Test
    public void reflectiveExpect_will2_happyPath() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Moxie.expect(mock).andReturn("foobar").will("partB", "[baz]");
        Assert.assertEquals("foobar cinquieme(<baz>) sixieme(baz)", mock.partsBCandD("baz"));
        Assert.assertEquals("third([blah]) cinquieme(<blah>) sixieme(blah)", mock.partsBCandD("blah"));
    }

    @Test
    public void reflectiveExpect_on3_happyPath() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Moxie.expect(mock).andReturn("kumquat").on("partC", new Class[]{String.class}, MoxieMatchers.hasSubstring("boo!"));
        Assert.assertEquals("third([boo!]) kumquat sixieme(boo!)", mock.partsBCandD("boo!"));
        Assert.assertEquals("third([booga!]) cinquieme(<booga!>) sixieme(booga!)", mock.partsBCandD("booga!"));
    }

    @Test
    public void reflectiveExpect_when3_happyPath() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Moxie.expect(mock).andReturn("[REDACTED]").when("partD", new Class[]{String.class}, "seekrit");
        Assert.assertEquals("third([seekrit]) cinquieme(<seekrit>) [REDACTED]", mock.partsBCandD("seekrit"));
        Assert.assertEquals("third([not so secret]) cinquieme(<not so secret>) sixieme(not so secret)", mock.partsBCandD("not so secret"));
    }

    @Test
    public void reflectiveExpect_will3_happyPath() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Moxie.expect(mock).andReturn("foobar").will("partB", new Class[]{String.class}, "[baz]");
        Assert.assertEquals("foobar cinquieme(<baz>) sixieme(baz)", mock.partsBCandD("baz"));
        Assert.assertEquals("third([blah]) cinquieme(<blah>) sixieme(blah)", mock.partsBCandD("blah"));
    }

    @Test
    public void reflectiveChecks2_happyPath() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Assert.assertEquals("third([baz]) cinquieme(<baz>) sixieme(baz)", mock.partsBCandD("baz"));
        Moxie.check(mock).got("partsBCandD","baz");
        Moxie.check(mock).on("partB", Moxie.hasSubstring("baz"));
        Moxie.check(mock).when("partC", "<baz>");
        Moxie.check(mock).get("partD", Moxie.not(Moxie.eq("chuck")));
        Moxie.checkNothingElseHappened(mock);
    }

    @Test
    public void reflectiveChecks3_happyPath() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Assert.assertEquals("third([baz]) cinquieme(<baz>) sixieme(baz)", mock.partsBCandD("baz"));
        Moxie.check(mock).got("partsBCandD", new Class[]{String.class},"baz");
        Moxie.check(mock).on("partB", new Class[]{String.class}, Moxie.hasSubstring("baz"));
        Moxie.check(mock).when("partC", new Class[]{String.class}, "<baz>");
        Moxie.check(mock).get("partD", new Class[]{String.class}, Moxie.not(Moxie.eq("chuck")));
        Moxie.checkNothingElseHappened(mock);
    }

    @Test
    @Ignore("PowerMock integration not ready yet")
    public void powerMock_happyPath() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Moxie.expect(mock).andReturn("orange").on("partF", MoxieMatchers.hasSubstring("x"));
        Assert.assertEquals("sixieme(xxx) EIGHT(_xxx_) orange", mock.partsDEandF("xxx"));
    }
}
