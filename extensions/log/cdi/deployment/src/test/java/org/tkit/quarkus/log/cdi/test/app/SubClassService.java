package org.tkit.quarkus.log.cdi.test.app;

import javax.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogService;

@ApplicationScoped
public class SubClassService extends AbstractService {

    @LogService(log = false)
    public void testAnnotation() {

    }

    public void testNoAnnotation() {

    }
}
