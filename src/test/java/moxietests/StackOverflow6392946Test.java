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

    @Mock
    public XMLStreamWriter xmlStreamWriter;

    // xmlStreamWriter gets invoked with strings which add up to "blah blah", so the test passes.
    @Test
    public void happyPathTest() throws XMLStreamException{
        PiecewiseStringMatcher addsUpToBlahBlah = new PiecewiseStringMatcher("blah blah");
        Moxie.expect(xmlStreamWriter).anyTimes().on().writeCharacters(Moxie.argThat(addsUpToBlahBlah));

        xmlStreamWriter.writeCharacters("blah ");
        xmlStreamWriter.writeCharacters("blah");

        Assert.assertTrue(addsUpToBlahBlah.hasMatchedEntirely());
    }

    // xmlStreamWriter's parameters don't add up to "blah blah", so the test would fail without the catch clause.
    // Also note that the final assert is false.
    @Test
    public void sadPathTest1() throws XMLStreamException{
        // We've specified the deprecated IGNORE_BACKGROUND_FAILURES option as otherwise Moxie works very hard
        // to ensure that unexpected invocations can't get silently swallowed (so this test will fail).
        Moxie.reset(xmlStreamWriter, MoxieOptions.IGNORE_BACKGROUND_FAILURES);

        PiecewiseStringMatcher addsUpToBlahBlah = new PiecewiseStringMatcher("blah blah");
        Moxie.expect(xmlStreamWriter).anyTimes().on().writeCharacters(Moxie.argThat(addsUpToBlahBlah));

        xmlStreamWriter.writeCharacters("blah ");
        try {
            xmlStreamWriter.writeCharacters("boink");
            Assert.fail("above line should have thrown a MoxieUnexpectedInvocationError");
        } catch (MoxieUnexpectedInvocationError e) {
            // as expected
        }

        // In a normal test we'd assert true here.
        // Here we assert false to verify that the behavior we're looking for has NOT occurred.
        Assert.assertFalse(addsUpToBlahBlah.hasMatchedEntirely());
    }

    // xmlStreamWriter's parameters add up to "blah bl", so the mock itself doesn't fail.
    // However the final assertion fails, as the matcher didn't see the entire string "blah blah".
    @Test
    public void sadPathTest2() throws XMLStreamException{
        PiecewiseStringMatcher addsUpToBlahBlah = new PiecewiseStringMatcher("blah blah");
        Moxie.expect(xmlStreamWriter).anyTimes().on().writeCharacters(Moxie.argThat(addsUpToBlahBlah));

        xmlStreamWriter.writeCharacters("blah ");
        xmlStreamWriter.writeCharacters("bl");

        // In a normal test we'd assert true here.
        // Here we assert false to verify that the behavior we're looking for has NOT occurred.
        Assert.assertFalse(addsUpToBlahBlah.hasMatchedEntirely());
    }
}
