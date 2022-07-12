package org.tkit.quarkus.log.cdi.test.app;

import javax.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogService;

@ApplicationScoped
public class DataPropertiesService {

    public String noAnnotationMethod(String input) {
        return "out " + input;
    }

    @LogService
    public String annotationMethod(String input) {
        return "out " + input;
    }
}
