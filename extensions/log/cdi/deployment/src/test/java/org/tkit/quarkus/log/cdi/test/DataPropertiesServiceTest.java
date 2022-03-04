package org.tkit.quarkus.log.cdi.test;

import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.cdi.test.app.DataPropertiesService;

import javax.inject.Inject;

public class DataPropertiesServiceTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(DataPropertiesService.class)
                    .addAsResource("data-properties.properties", "application.properties"));

    @Inject
    DataPropertiesService service;

    @Test
    public void noAnnotationTest() {
       service.noAnnotationMethod("no-annotation");
       assertLogs().assertNoLogs();
    }

    @Test
    public void annotationTest() {
        service.annotationMethod("annotation");
        assertLogs().assertNoLogs();
    }

}
