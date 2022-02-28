package org.tkit.quarkus.log.cdi.test;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class ErrorDataServiceTest {

    @Inject
    ErrorDataService service;

    @Inject
    ErrorWrapperService wrapper;

    @Test
    public void error1Test() {
        Assertions.assertThrows(RuntimeException.class, () -> service.error1("Error"));
    }

    @Test
    public void noStacktraceTest() {
        Assertions.assertThrows(RuntimeException.class, () -> service.noStacktrace());
    }

    @Test
    public void wrapperTest() {
        Assertions.assertThrows(RuntimeException.class, () -> wrapper.wrapperMethod("WrapperError"));
    }

}
