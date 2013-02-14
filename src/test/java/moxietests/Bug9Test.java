package moxietests;

import moxie.Mock;
import moxie.Moxie;
import moxie.MoxieFailedVerificationError;
import moxie.MoxieRule;
import moxie.MoxieUnexpectedInvocationError;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class Bug9Test {

    private interface Bug9TestInterface {
        void death();
        void taxes();
        void spanishInquisition();
    }

    @Test
    public void unexpectedMethodCallInAnotherThreadFailsTheTest() throws InterruptedException {
        final Bug9TestInterface mock = Moxie.mock(Bug9TestInterface.class);

        Moxie.expect(mock).will().death();
        Moxie.expect(mock).will().taxes();
        Moxie.expect(mock).never().will().spanishInquisition();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                mock.spanishInquisition();
            }
        });
        thread.start();

        mock.death();
        mock.taxes();

        thread.join();

        // No, using @Test(expected = MoxieUnexpectedInvocationError.class) doesn't work;
        // not about to spend hours figuring out how to contort MoxieRule to fix this.
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
        final Bug9TestInterface mock = Moxie.mock(Bug9TestInterface.class);

        Moxie.expect(mock).times(2).will().death();
        Moxie.expect(mock).will().taxes();
        Moxie.expect(mock).never().will().spanishInquisition();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                mock.spanishInquisition();
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

}
