package org.tkit.quarkus.log.cdi.test.app;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogService;

@LogService(log = false)
@ApplicationScoped
public class ParentDataService {

    public String disableLog(String input) {
        return "data1 " + input;
    }

    @LogService
    public String enableLog(String input) {
        return "data1 " + input;
    }

}
