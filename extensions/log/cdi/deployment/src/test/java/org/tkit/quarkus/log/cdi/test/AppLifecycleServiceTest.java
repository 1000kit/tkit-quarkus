package org.tkit.quarkus.log.cdi.test;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.cdi.test.app.AppLifecycleService;

import io.quarkus.test.QuarkusUnitTest;

public class AppLifecycleServiceTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(AppLifecycleService.class)
                    .addAsResource("default.properties", "application.properties"));

    @Inject
    AppLifecycleService service;

    @Test
    public void startup() {
        service.info();
    }

}
