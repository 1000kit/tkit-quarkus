package org.tkit.quarkus.log.cdi.test;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.cdi.test.app.NoAnnotationService;

import io.quarkus.test.QuarkusUnitTest;

public class AutoDiscoveryTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(NoAnnotationService.class)
                    .addAsResource("autodiscovery.properties", "application.properties"));

    @Inject
    NoAnnotationService service;

    @Test
    public void noAnnotationMethodTest() {
        service.noAnnotationMethod("input1");
        assertLogs().assertLines(1)
                .assertContains(0,
                        "INFO  [org.tki.qua.log.cdi.tes.app.NoAnnotationService] (main) noAnnotationMethod(arg0):out input1");
    }

    @Test
    public void disableLogMethodTest() {
        service.disableLog("input1");
        assertLogs().assertNoLogs();
    }
}
