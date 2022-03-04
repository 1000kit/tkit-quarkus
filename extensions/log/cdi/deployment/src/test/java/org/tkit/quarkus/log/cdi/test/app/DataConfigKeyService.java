package org.tkit.quarkus.log.cdi.test.app;

import org.tkit.quarkus.log.cdi.LogService;

import javax.enterprise.context.ApplicationScoped;

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
