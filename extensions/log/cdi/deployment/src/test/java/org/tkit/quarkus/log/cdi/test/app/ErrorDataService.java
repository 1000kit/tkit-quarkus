package org.tkit.quarkus.log.cdi.test.app;

import javax.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogService;

@LogService
@ApplicationScoped
public class ErrorDataService {

    public void error1(String error) {
        throw new RuntimeException(error);
    }

    public void error2() {
        throw new DummyLogFriendlyException();
    }

    @LogService(stacktrace = false)
    public void noStacktrace() {
        throw new RuntimeException("Error1");
    }
}
