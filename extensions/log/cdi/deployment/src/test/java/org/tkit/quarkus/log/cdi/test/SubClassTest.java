package org.tkit.quarkus.log.cdi.test;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.cdi.test.app.AbstractService;
import org.tkit.quarkus.log.cdi.test.app.SubClassService;

import io.quarkus.test.QuarkusUnitTest;

public class SubClassTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(AbstractService.class, SubClassService.class)
                    .addAsResource("default.properties", "application.properties"));

    @Inject
    SubClassService service;

    @Test
    public void superTestAnnotationTest() {
        service.superTestAnnotation();
        assertLogs().assertLines(1)
                .assertContains(0, "INFO  [org.tki.qua.log.cdi.tes.app.SubClassService] (main) superTestAnnotation():void");
    }

    @Test
    public void noAnnotationTest() {
        service.testNoAnnotation();
        assertLogs().assertNoLogs();
    }

    @Test
    public void annotationTest() {
        service.testAnnotation();
        assertLogs().assertNoLogs();
    }
}
