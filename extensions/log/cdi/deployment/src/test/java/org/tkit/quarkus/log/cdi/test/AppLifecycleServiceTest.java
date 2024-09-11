package org.tkit.quarkus.log.cdi.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.cdi.test.app.AppLifecycleService;

import io.quarkus.test.QuarkusUnitTest;

class AppLifecycleServiceTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(AppLifecycleService.class)
                    .addAsResource("default.properties", "application.properties"));

    @Inject
    AppLifecycleService service;

    @Test
    void startupTest() {
        assertDoesNotThrow(() -> {
            service.info();
        });
    }

}
