package org.tkit.quarkus.log.cdi.test.app;

import javax.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogService;

@LogService(configKey = "data")
@ApplicationScoped
public class DataConfigKeyService {

    public String noAnnotationMethod(String input) {
        return "out " + input;
    }

    @LogService(configKey = "anno")
    public String annotationMethod(String input) {
        return "out " + input;
    }
}
