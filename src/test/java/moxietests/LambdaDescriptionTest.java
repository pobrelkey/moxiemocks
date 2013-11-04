package moxietests;

import junit.framework.Assert;
import moxie.Predicate;
import moxie.hamcrest.LambdaMatcher;
import moxie.hamcrest.SimpleDescription;
import org.junit.Test;

public class LambdaDescriptionTest {
    @Test
    @SuppressWarnings("unchecked")
    public void describesSelfCorrectly() {
        Predicate someLambda = new Predicate() {
            public boolean test(Object value) {
                return true;
            }
        };

        Assert.assertEquals("a value matching the Predicate defined at LambdaDescriptionTest.java:15", SimpleDescription.asString(new LambdaMatcher(someLambda)));
    }
}
