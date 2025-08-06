package org.tkit.quarkus.log.cdi.test.app;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogExclude;
import org.tkit.quarkus.log.cdi.LogService;

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

    @LogService(logStart = LogService.Log.DISABLED)
    public String disableLogStart(String input) {
        return "data1 " + input;
    }

    @LogService(logStart = LogService.Log.ENABLED)
    public String enableLogStart(String input) {
        return "data1 " + input;
    }
}
