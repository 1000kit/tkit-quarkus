package org.tkit.quarkus.log.cdi.test;

import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.cdi.test.app.AbstractService;
import org.tkit.quarkus.log.cdi.test.app.SubClassService;

import javax.inject.Inject;

public class SubClassTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(AbstractService.class, SubClassService.class)
                    .addAsResource("application.properties", "application.properties"));

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
       assertLogs().assertLines(1)
               .assertContains(0, "INFO  [org.tki.qua.log.cdi.tes.app.SubClassService] (main) testNoAnnotation():void");
    }

    @Test
    public void annotationTest() {
        service.testAnnotation();
        assertLogs().assertNoLogs();
    }
}
