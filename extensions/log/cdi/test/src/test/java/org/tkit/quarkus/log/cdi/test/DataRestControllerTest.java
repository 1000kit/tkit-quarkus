package org.tkit.quarkus.log.cdi.test;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class DataRestControllerTest {

    @Inject
    DataService service;

    @Test
    public void data1Test() {
        service.getData1("input1");
    }

    @Test
    public void excludeParamTest() {
        service.excludeParam("input1");
    }

    @Test
    public void maskParamTest() {
        service.maskParam("input1");
    }

    @Test
    public void excludeReturnTest() {
        service.excludeReturn("input1");
    }

    @Test
    public void maskReturnTest() {
        service.maskReturn("input1");
    }
}
