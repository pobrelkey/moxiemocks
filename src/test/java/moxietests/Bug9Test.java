package moxietests;

import moxie.Moxie;
import moxie.MoxieFailedVerificationError;
import moxie.MoxieOptions;
import moxie.MoxieUnexpectedInvocationError;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class Bug9Test {

    private interface Bug9TestInterface {
        void death();
        void taxes();
        void spanishInquisition();
    }

    @Test
    public void unexpectedMethodCallInAnotherThreadFailsTheTest() throws InterruptedException {
        Moxie.reset();

        final Bug9TestInterface mock = Moxie.mock(Bug9TestInterface.class);

        Moxie.expect(mock).will().death();
        Moxie.expect(mock).will().taxes();
        Moxie.expect(mock).never().will().spanishInquisition();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    mock.spanishInquisition();
                } catch (MoxieUnexpectedInvocationError e){
                    // swallow throwable to keep build logs tidy
                }
            }
        });
        thread.start();

        mock.death();
        mock.taxes();

        thread.join();

        // No, using @Test(expected = MoxieUnexpectedInvocationError.class) doesn't work - see issue 7.
        // At any rate, end-user tests shouldn't use JUnit annotations to expect a Moxie error!
        try {
            Moxie.verify();
            Assert.fail("last line should have thrown a MoxieUnexpectedInvocationError");
        } catch (MoxieUnexpectedInvocationError e) {
            Assert.assertTrue(e.getMessage().indexOf("spanishInquisition()") != -1);
        }
    }


    @Test
    public void unexpectedMethodCallInAnotherThreadDOESNOTFailTheTestIfThereIsAnotherFail() throws InterruptedException {
        Moxie.reset();

        final Bug9TestInterface mock = Moxie.mock(Bug9TestInterface.class);

        Moxie.expect(mock).times(2).will().death();
        Moxie.expect(mock).will().taxes();
        Moxie.expect(mock).never().will().spanishInquisition();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    mock.spanishInquisition();
                } catch (MoxieUnexpectedInvocationError e){
                    // swallow throwable to keep build logs tidy
                }
            }
        });
        thread.start();

        mock.death();
        mock.taxes();

        thread.join();

        try {
            Moxie.verify();
            Assert.fail("last line should have thrown a MoxieFailedVerificationError");
        } catch (MoxieFailedVerificationError e) {
            // good, not a MoxieUnexpectedInvocationError
        }
    }

    @Test
    public void unexpectedMethodCallInAnotherThreadDOESNOTFailIfIgnoreOptionSpecified() throws InterruptedException {
        Moxie.reset();

        @SuppressWarnings("deprecation")
        final Bug9TestInterface mock = Moxie.mock(Bug9TestInterface.class, MoxieOptions.IGNORE_BACKGROUND_FAILURES);

        Moxie.expect(mock).will().death();
        Moxie.expect(mock).will().taxes();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    mock.spanishInquisition();
                } catch (MoxieUnexpectedInvocationError e){
                    // swallow throwable to keep build logs tidy
                }
            }
        });
        thread.start();

        mock.death();
        mock.taxes();

        thread.join();

        Moxie.verify();
    }
}
