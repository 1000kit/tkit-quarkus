package org.tkit.quarkus.log.cdi.test;

import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class LogProcessorTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addAsResource("deprecated.properties", "application.properties"))
            .setExpectedException(IllegalArgumentException.class, true);

    @Test
    public void testPersistenceAndConfigTest() {
        // should not be called, validation of deprecated configuration must fail
        Assertions.fail();
    }

}
