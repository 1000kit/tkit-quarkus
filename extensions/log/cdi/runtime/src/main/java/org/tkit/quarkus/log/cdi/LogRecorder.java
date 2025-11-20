package org.tkit.quarkus.log.cdi;

import java.util.List;

import jakarta.enterprise.inject.Any;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.log.cdi.interceptor.LogParamValueService;
import org.tkit.quarkus.log.cdi.runtime.LogRuntimeConfig;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InjectableInstance;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

/**
 * The logger builder interface.
 */
@Recorder
public class LogRecorder {

    private static final Logger log = LoggerFactory.getLogger(LogRecorder.class);

    static LogRuntimeConfig CONFIG;

    private final RuntimeValue<LogRuntimeConfig> configValue;

    static ServiceValue SERVICE = new ServiceValue();

    public LogRecorder(RuntimeValue<LogRuntimeConfig> configValue) {
        this.configValue = configValue;
    }

    public void init(ServiceValue values) {

        InjectableInstance<LogParam> it = Arc.container().select(LogParam.class, Any.Literal.INSTANCE);
        LogParamValueService.init(it.stream());

        LogRuntimeConfig config = configValue.getValue();
        CONFIG = config;
        SERVICE = values;

        if (config.service() == null) {
            return;
        }

        config.service().forEach((key, value) -> {
            List<ServiceValue.ClassItem> items = values.getByConfig(key);
            if (items == null) {
                log.warn("No @LogService annotation found for key `tkit.log.cdi.service.\"{}\"`. Key will be ignored",
                        key);
                return;
            }

            // update class values from properties
            if (value.config().log().isPresent() || value.config().stacktrace().isPresent()) {
                items.forEach(item -> {
                    // update class config from properties
                    if (item.config != null) {
                        value.config().log().ifPresent(x -> item.config.log = x);
                        value.config().stacktrace().ifPresent(x -> item.config.stacktrace = x);
                    } else {
                        log.warn(
                                "No @LogService annotation found for class {}. Key `tkit.log.cdi.service.\"{}\"` will be ignored",
                                item.id, key);
                    }
                });
            }

            // update method values from properties
            value.method().forEach((mk, mv) -> {
                items.forEach(item -> {
                    List<ServiceValue.MethodItem> methods = item.getByConfig(mk);
                    if (methods != null) {
                        methods.forEach(method -> {
                            // update config from class if method config found
                            if (method.config == null && item.config != null) {
                                method.copyConfig(item.config);
                            }
                            // update method config from properties
                            if (method.config != null) {
                                mv.config().log().ifPresent(x -> method.config.log = x);
                                mv.config().stacktrace().ifPresent(x -> method.config.stacktrace = x);
                                if (mv.params() != null && !mv.params().isEmpty()) {
                                    method.params.putAll(mv.params());
                                }
                                mv.returnMask().ifPresent(x -> method.returnMask = x);
                            } else {
                                log.warn(
                                        "No @LogService annotation found for method `{}.{}`. Key `tkit.log.cdi.service.\"{}\".method.{}` will be ignored",
                                        item.id, method.id, key, mk);
                            }
                        });
                    }
                });
            });
        });

        // update method empty config from Class
        values.updateConfig();
    }

    public static ServiceValue.MethodItem getService(String clazz, String method) {
        ServiceValue.ClassItem classItem = SERVICE.get(clazz);
        if (classItem == null) {
            return null;
        }
        return classItem.methods.get(method);
    }

    public static LogRuntimeConfig getConfig() {
        return CONFIG;
    }

}
