package moxietests;

import moxie.Mock;
import moxie.MoxieRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static moxie.Moxie.stub;

public class Bug1Test {

    @Rule
    public MoxieRule moxie = new MoxieRule();

    @Mock
    Runnable someBehaviour;

    Runnable underTest;

    @Before
    public void init() {
        underTest = new SimpleRunnable(someBehaviour);
    }

    @Test
    public void reproduceBug1() throws Throwable {
        stub(someBehaviour).when().run();
        underTest.run();
    }


    private static class SimpleRunnable implements Runnable {

        private final Runnable delegate;

        public SimpleRunnable(Runnable delegate) {
            this.delegate = delegate;
        }

        public void run() {
            delegate.run();
        }
    }
}
