package org.tkit.quarkus.log.cdi.test;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.cdi.test.app.DataService;

import io.quarkus.test.QuarkusUnitTest;

public class StartLogDataServiceTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(DataService.class)
                    .addAsResource("start-log.properties", "application.properties"));

    @Inject
    DataService service;

    @Test
    public void startLogTest() {
        service.getData1("input1");
        assertLogs().assertLines(2)
                .assertContains(0, "INFO  [org.tki.qua.log.cdi.tes.app.DataService] (main) getData1(input1) started.")
                .assertContains(1, "INFO  [org.tki.qua.log.cdi.tes.app.DataService] (main) getData1(input1):data1 input1");
    }

}
