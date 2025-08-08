package org.tkit.quarkus.log.cdi.test;

import static org.tkit.quarkus.log.cdi.test.app.DummyLogFriendlyException.DUMMY_ERROR_NUMBER;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.cdi.test.app.DummyLogFriendlyException;
import org.tkit.quarkus.log.cdi.test.app.ErrorDataService;
import org.tkit.quarkus.log.cdi.test.app.ErrorWrapperService;

import io.quarkus.test.QuarkusUnitTest;

@EnabledForJreRange(max = JRE.JAVA_17)
public class ErrorDataServiceJdk17Test extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(ErrorDataService.class, ErrorWrapperService.class, DummyLogFriendlyException.class)
                    .addAsResource("default.properties", "application.properties"));

    @Inject
    ErrorDataService service;

    @Inject
    ErrorWrapperService wrapper;

    @Test
    public void error1Test() {
        Assertions.assertThrows(RuntimeException.class, () -> service.error1("Error"));
        assertLogs()
                .assertLines(97)
                .assertContains(0,
                        "ERROR [org.tki.qua.log.cdi.tes.app.ErrorDataService] (main) error1(Error) throw java.lang.RuntimeException: Error");
    }

    @Test
    public void error2Test() {
        Assertions.assertThrows(RuntimeException.class, () -> service.error2());
        assertLogs().assertContains(0,
                DUMMY_ERROR_NUMBER
                        + " ERROR [org.tki.qua.log.cdi.tes.app.ErrorDataService] (main) error2() throw org.tkit.quarkus.log.cdi.test.app.DummyLogFriendlyException");
    }

    @Test
    public void noStacktraceTest() {
        Assertions.assertThrows(RuntimeException.class, () -> service.noStacktrace());
        assertLogs().assertLines(1)
                .assertContains(0,
                        "ERROR [org.tki.qua.log.cdi.tes.app.ErrorDataService] (main) noStacktrace() throw java.lang.RuntimeException: Error1");
    }

    @Test
    public void wrapperTest() {
        Assertions.assertThrows(RuntimeException.class, () -> wrapper.wrapperMethod("WrapperError"));
        assertLogs().assertLines(116)
                .assertContains(0,
                        "ERROR [org.tki.qua.log.cdi.tes.app.ErrorDataService] (main) error1(WrapperError) throw java.lang.RuntimeException: WrapperError");
    }

}
