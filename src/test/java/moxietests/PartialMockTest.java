package moxietests;

import moxie.Moxie;
import moxie.MoxieOptions;
import moxie.MoxieRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class PartialMockTest {
    public static class PartiallyMocked {
        public String methodTheFirst(String arg) {
            return methodTheSecond("{" + arg + "}") + " " + methodTheThird("[" + arg + "]");
        }

        public String methodTheSecond(String arg) {
            return "second(" + arg + ")";
        }

        public String methodTheThird(String arg) {
            return "third(" + arg + ")";
        }
    }

    @Rule
    public MoxieRule moxie = new MoxieRule();

    @Test
    public void partialMock_happyPath1() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL);
        Moxie.expect(mock).on().methodTheFirst("foo");
        Moxie.expect(mock).on().methodTheSecond("{foo}");
        Moxie.expect(mock).on().methodTheThird("[foo]");
        Assert.assertEquals("second({foo}) third([foo])", mock.methodTheFirst("foo"));
    }

    @Test
    public void partialMock_happyPath2() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Assert.assertEquals("second({bar}) third([bar])", mock.methodTheFirst("bar"));
    }

    @Test
    public void partialMock_happyPath3() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class, MoxieOptions.PARTIAL, MoxieOptions.PERMISSIVE);
        Moxie.expect(mock).andReturn("SOMETHING_ELSE").on().methodTheThird("[baz]");
        Assert.assertEquals("second({baz}) SOMETHING_ELSE", mock.methodTheFirst("baz"));
    }

    @Test
    public void callOriginal_happyPath() {
        PartiallyMocked mock = Moxie.mock(PartiallyMocked.class);
        Moxie.expect(mock).andCallOriginal().on().methodTheFirst("moe");
        Moxie.expect(mock).andReturn("lenny").on().methodTheSecond("{moe}");
        Moxie.expect(mock).andReturn("carl").on().methodTheThird("[moe]");
        Assert.assertEquals("lenny carl", mock.methodTheFirst("moe"));
    }

}
