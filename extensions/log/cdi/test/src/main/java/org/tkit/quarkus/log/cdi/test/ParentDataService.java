package org.tkit.quarkus.log.cdi.test;

import org.tkit.quarkus.log.cdi.LogService;

import javax.enterprise.context.ApplicationScoped;

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
