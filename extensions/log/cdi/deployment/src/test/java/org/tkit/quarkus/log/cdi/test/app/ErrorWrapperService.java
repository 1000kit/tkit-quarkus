package org.tkit.quarkus.log.cdi.test.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.tkit.quarkus.log.cdi.LogService;

@LogService
@ApplicationScoped
public class ErrorWrapperService {

    @Inject
    ErrorDataService service;

    public void wrapperMethod(String error) {
        service.error1(error);
    }
}
