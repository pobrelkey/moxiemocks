package moxietests;

import moxie.Mock;
import moxie.Moxie;
import moxie.MoxieRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

public class Bug4Test {

    @Rule
    public MoxieRule moxie = new MoxieRule();

    @Mock
    List<String> mockList;

    @Test
    public void blah() {
        Moxie.expect(mockList).andReturn(null).on().get(Moxie.isA(Integer.TYPE));

    }

}
