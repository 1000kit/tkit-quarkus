package org.tkit.quarkus.log.cdi.test.app;

import javax.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogExclude;

@ApplicationScoped
public class NoAnnotationService {

    public String noAnnotationMethod(@LogExclude String input) {
        return "out " + input;
    }

    @LogExclude
    public String annotationMethod(String input) {
        return "out " + input;
    }
}
