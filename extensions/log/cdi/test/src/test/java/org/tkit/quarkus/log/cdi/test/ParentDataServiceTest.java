package org.tkit.quarkus.log.cdi.test;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class ParentDataServiceTest {

    @Inject
    ParentDataService service;

    @Test
    public void disableLogTest() {
        service.disableLog("123");
    }

    @Test
    public void enableLogTest() {
        service.enableLog("123");
    }
}
