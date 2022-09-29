package org.tkit.quarkus.log.cdi.test.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.log.cdi.LogService;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class AppLifecycleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppLifecycleService.class);

    @LogService
    public void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...");
    }

    public void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }

    public void info() {
        LOGGER.info("The application info");
    }

}
