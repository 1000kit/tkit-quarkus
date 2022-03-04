package org.tkit.quarkus.log.cdi.test.app;

import org.tkit.quarkus.log.cdi.LogService;

@LogService
public class AbstractService {

    @LogService(configKey = "test")
    public void superTestAnnotation() {

    }

}
