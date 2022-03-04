package org.tkit.quarkus.log.cdi.test;

import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.cdi.test.app.ParentDataService;

import javax.inject.Inject;

public class ParentDataServiceTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClass(ParentDataService.class)
                    .addAsResource("application.properties", "application.properties"));

    @Inject
    ParentDataService service;

    @Test
    public void disableLogTest() {
        service.disableLog("123");
        assertLogs().assertNoLogs();
    }

    @Test
    public void enableLogTest() {
        service.enableLog("123");
        assertLogs().assertLines(1)
                .assertContains(0,"INFO  [org.tki.qua.log.cdi.tes.app.ParentDataService] (main) enableLog(123):data1 123");
    }
}
