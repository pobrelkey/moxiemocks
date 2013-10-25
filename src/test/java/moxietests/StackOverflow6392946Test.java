package moxietests;

import moxie.Mock;
import moxie.Moxie;
import moxie.MoxieOptions;
import moxie.MoxieRule;
import moxie.MoxieUnexpectedInvocationError;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

// Written in response to... http://stackoverflow.com/questions/6392946/
public class StackOverflow6392946Test {

    private static class PiecewiseStringMatcher extends BaseMatcher<String> {
        private final String toMatch;
        private int pos = 0;

        private PiecewiseStringMatcher(String toMatch) {
            this.toMatch = toMatch;
        }

        public boolean matches(Object item) {
            String itemAsString = (item == null) ? "" : item.toString();
            if (!toMatch.substring(pos).startsWith(itemAsString)) {
                return false;
            }
            pos += itemAsString.length();
            return true;
        }

        public void describeTo(Description description) {
            description.appendText("a series of strings which when concatenated form the string \"" + toMatch + '"');
        }

        public boolean hasMatchedEntirely() {
            return pos == toMatch.length();
        }
    }

    @Rule
    public MoxieRule moxie = new MoxieRule();

    // We've specified the deprecated IGNORE_BACKGROUND_FAILURES option as otherwise Moxie works very hard to ensure
    // that unexpected invocations can't get silently swallowed (so in particular sadPathTest1 will fail).
    @Mock(MoxieOptions.IGNORE_BACKGROUND_FAILURES)
    public XMLStreamWriter xmlStreamWriter;

    @Test
    public void happyPathTest() throws XMLStreamException{
        PiecewiseStringMatcher matcher = new PiecewiseStringMatcher("blah blah");
        Moxie.expect(xmlStreamWriter).anyTimes().on().writeCharacters(Moxie.reportMatcher(matcher, String.class));

        xmlStreamWriter.writeCharacters("blah ");
        xmlStreamWriter.writeCharacters("blah");

        Assert.assertTrue(matcher.hasMatchedEntirely());
    }

    @Test
    public void sadPathTest1() throws XMLStreamException{
        PiecewiseStringMatcher matcher = new PiecewiseStringMatcher("blah blah");
        Moxie.expect(xmlStreamWriter).anyTimes().on().writeCharacters(Moxie.reportMatcher(matcher, String.class));

        xmlStreamWriter.writeCharacters("blah ");
        try {
            xmlStreamWriter.writeCharacters("boink");
            Assert.fail("above line should have thrown a MoxieUnexpectedInvocationError");
        } catch (MoxieUnexpectedInvocationError e) {
            // as expected
        }

        Assert.assertFalse(matcher.hasMatchedEntirely());
    }

    @Test
    public void sadPathTest2() throws XMLStreamException{
        PiecewiseStringMatcher matcher = new PiecewiseStringMatcher("blah blah");
        Moxie.expect(xmlStreamWriter).anyTimes().on().writeCharacters(Moxie.reportMatcher(matcher, String.class));

        xmlStreamWriter.writeCharacters("blah ");
        xmlStreamWriter.writeCharacters("bl");

        Assert.assertFalse(matcher.hasMatchedEntirely());
    }
}
