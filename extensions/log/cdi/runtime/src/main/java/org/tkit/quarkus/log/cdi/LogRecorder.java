package org.tkit.quarkus.log.cdi;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.annotations.Recorder;
import org.tkit.quarkus.log.cdi.ServiceValue;
import org.tkit.quarkus.log.cdi.interceptor.LogParamValueService;
import org.tkit.quarkus.log.cdi.runtime.LogParamContainer;
import org.tkit.quarkus.log.cdi.runtime.LogRuntimeConfig;

import java.util.List;

/**
 * The logger builder interface.
 */
@Recorder
public class LogRecorder {

    static LogRuntimeConfig CONFIG;

    static ServiceValue SERVICE;

    public void init(ServiceValue values, LogRuntimeConfig config) {
        CONFIG = config;
        SERVICE = values;

        LogParamContainer con = Arc.container().instance(LogParamContainer.class).get();
        LogParamValueService.init(config, con.getParameters());
        if (config.service != null) {
            config.service.forEach((key, value) -> {
                List<ServiceValue.ClassItem> items = values.getByConfig(key);
                if (items != null) {

                    // update class values from properties
                    if (value.config.log.isPresent() || value.config.stacktrace.isPresent()) {
                        items.forEach(item -> {
                            value.config.log.ifPresent(x -> item.config.log = x);
                            value.config.stacktrace.ifPresent(x -> item.config.stacktrace = x);
                        });
                    }

                    value.method.forEach((mk, mv) -> {

                        items.forEach(item -> {
                            List<ServiceValue.MethodItem> methods = item.getByConfig(mk);
                            if (methods != null) {
                                methods.forEach(method -> {
                                    mv.config.log.ifPresent(x -> method.config.log = x);
                                    mv.config.stacktrace.ifPresent(x -> method.config.stacktrace = x);
                                    mv.params.ifPresent(map -> method.params.putAll(map));
                                    mv.returnMask.ifPresent(x -> method.returnMask = x);
                                });
                            }
                        });
                    });
                }
            });
        }
    }

    public static ServiceValue.MethodItem getService(String clazz, String method) {
        ServiceValue.ClassItem classItem = SERVICE.get(clazz);
        if (classItem == null) {
            return null;
        }
        ServiceValue.MethodItem m = classItem.methods.get(method);
        if (m == null) {
            m = new ServiceValue.MethodItem();
            m.config = classItem.config;
        }
        return m;
    }

    public static LogRuntimeConfig getConfig() {
        return CONFIG;
    }

}
