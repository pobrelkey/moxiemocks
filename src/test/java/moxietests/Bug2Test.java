package moxietests;

import moxie.Moxie;
import moxie.MoxieOptions;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertNotNull;


public class Bug2Test {

    @Test
    public void testPerformStubTask() {
        Moxie.mock(AutogenStub.class);
    }

    @Test
    public void testPerformStubTaskPermissive() {
        AutogenStub stub = Moxie.mock(AutogenStub.class, MoxieOptions.PERMISSIVE);
        assertNotNull(stub);
    }

    @Test
    public void testPerformStubTaskPermissiveWithVoidBehaviourRecorded() {
        AutogenStub stub = Moxie.mock(AutogenStub.class, MoxieOptions.PERMISSIVE);
        assertNotNull(stub);
        Moxie.expect(stub).once().on().performStubTask();
    }

    @Test
    public void testPerformStubTaskPermissiveWithReturnBehaviourRecorded() {
        AutogenStub stub = Moxie.mock(AutogenStub.class, MoxieOptions.PERMISSIVE);
        assertNotNull(stub);
        Moxie.expect(stub).andReturn(true).once().on().performStubTask();
    }

    public static class AutogenStub {
        public AutogenStub() {
            initialiseStub();
        }

        protected void initialiseStub() {
            System.out.println("yeah, yeah, doin' some initialising");
        }

        public boolean performStubTask() {
            return new Random().nextBoolean();
        }
    }
}
