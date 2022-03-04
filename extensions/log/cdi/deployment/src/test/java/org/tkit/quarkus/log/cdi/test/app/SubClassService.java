package org.tkit.quarkus.log.cdi.test.app;

import org.tkit.quarkus.log.cdi.LogService;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubClassService extends AbstractService {


    @LogService(log = false)
    public void testAnnotation() {

    }

    public void testNoAnnotation() {

    }
}
