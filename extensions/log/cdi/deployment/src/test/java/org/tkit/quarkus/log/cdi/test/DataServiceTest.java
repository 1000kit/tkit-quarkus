package org.tkit.quarkus.log.cdi.test;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.cdi.test.app.DataService;

import io.quarkus.test.QuarkusUnitTest;

public class DataServiceTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(DataService.class)
                    .addAsResource("default.properties", "application.properties"));

    @Inject
    DataService service;

    @Test
    public void data1Test() {
        service.getData1("input1");
        assertLogs().assertLines(1)
                .assertContains(0, "INFO  [org.tki.qua.log.cdi.tes.app.DataService] (main) getData1(input1):data1 input1");
    }

    @Test
    public void excludeParamTest() {
        service.excludeParam("input1");
        assertLogs().assertLines(1)
                .assertContains(0, "INFO  [org.tki.qua.log.cdi.tes.app.DataService] (main) excludeParam(input):data1 input1");
    }

    @Test
    public void maskParamTest() {
        service.maskParam("input1");
        assertLogs().assertLines(1)
                .assertContains(0, "INFO  [org.tki.qua.log.cdi.tes.app.DataService] (main) maskParam(*****):data1 input1");
    }

    @Test
    public void excludeReturnTest() {
        service.excludeReturn("input1");
        assertLogs().assertLines(1)
                .assertContains(0, "INFO  [org.tki.qua.log.cdi.tes.app.DataService] (main) excludeReturn(input1): [");
    }

    @Test
    public void maskReturnTest() {
        service.maskReturn("input1");
        assertLogs().assertLines(1)
                .assertContains(0, "INFO  [org.tki.qua.log.cdi.tes.app.DataService] (main) maskReturn(input1):###");
    }
}
