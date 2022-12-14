package org.tkit.quarkus.log.cdi.test.app;

import javax.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogService;

@LogService
@ApplicationScoped
public class ErrorDataService {

    public void error1(String error) {
        throw new RuntimeException(error);
    }

    @LogService(stacktrace = false)
    public void noStacktrace() {
        throw new RuntimeException("Error1");
    }
}
