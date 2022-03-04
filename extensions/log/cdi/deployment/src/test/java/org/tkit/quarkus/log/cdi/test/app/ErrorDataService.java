package org.tkit.quarkus.log.cdi.test.app;

import org.tkit.quarkus.log.cdi.LogService;

import javax.enterprise.context.ApplicationScoped;

@LogService
@ApplicationScoped
public class ErrorDataService {

    public void error1(String error)  {
        throw new RuntimeException(error);
    }

    @LogService(stacktrace = false)
    public void noStacktrace()  {
        throw new RuntimeException("Error1");
    }
}
