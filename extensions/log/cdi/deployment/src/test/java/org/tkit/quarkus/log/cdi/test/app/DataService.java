package org.tkit.quarkus.log.cdi.test.app;

import org.tkit.quarkus.log.cdi.LogExclude;
import org.tkit.quarkus.log.cdi.LogService;

import javax.enterprise.context.ApplicationScoped;

@LogService
@ApplicationScoped
public class DataService {

    public String getData1(String input) {
        return "data1 " + input;
    }

    public String excludeParam(@LogExclude String input) {
        return "data1 " + input;
    }

    public String maskParam(@LogExclude(mask = "*****") String input) {
        return "data1 " + input;
    }

    @LogExclude
    public String excludeReturn(String input) {
        return "data1 " + input;
    }

    @LogExclude(mask = "###")
    public String maskReturn(String input) {
        return "data1 " + input;
    }
}
