package org.tkit.quarkus.log.cdi.test;

import org.tkit.quarkus.log.cdi.LogService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@LogService
@ApplicationScoped
public class ErrorWrapperService {

    @Inject
    ErrorDataService service;

    public void wrapperMethod(String error) {
        service.error1(error);
    }
}
