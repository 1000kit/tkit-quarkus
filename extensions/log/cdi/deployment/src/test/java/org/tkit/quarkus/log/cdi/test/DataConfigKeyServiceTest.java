package org.tkit.quarkus.log.cdi.test;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.cdi.test.app.DataConfigKeyService;

import io.quarkus.test.QuarkusUnitTest;

public class DataConfigKeyServiceTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(DataConfigKeyService.class)
                    .addAsResource("config-key.properties", "application.properties"));

    @Inject
    DataConfigKeyService service;

    @Test
    public void noAnnotationTest() {
        service.noAnnotationMethod("no-annotation");
        assertLogs()
                .assertNoEmpty()
                .assertContains(0,
                        "INFO  [org.tki.qua.log.cdi.tes.app.DataConfigKeyService] (main) noAnnotationMethod(no-annotation):out no-annotation");
    }

    @Test
    public void annotationTest() {
        service.annotationMethod("annotation");
        assertLogs().assertNoLogs();
    }
}
