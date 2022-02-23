package org.tkit.quarkus.log.rs;

import io.quarkus.runtime.annotations.Recorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Recorder
public class RestRecorder {

    private static final Logger log = LoggerFactory.getLogger(RestRecorder.class);

    static RestRuntimeConfig CONFIG;

    static RestServiceValue REST_SERVICE;

    static List<Pattern> PATTERNS;

    public void init(RestRuntimeConfig config, RestServiceValue values) {
        CONFIG = config;
        if (config.regex.enabled) {
            List<String> items = config.regex.exclude.orElse(null);
            if (items != null && !items.isEmpty()) {
                PATTERNS = new ArrayList<>();
                items.forEach(item -> {
                    try {
                        PATTERNS.add(Pattern.compile(item));
                    } catch (PatternSyntaxException ex) {
                        log.error("Error compile regex pattern '{}' error {}", item, ex.getMessage());
                    }
                });
            } else {
                config.regex.enabled = false;
                log.info("No exclude regex patterns found. Disable exclude regex patterns for rest log interceptor.");
            }
        }
        REST_SERVICE = values;
        if (config.controller != null) {
            config.controller.forEach((key, value) -> {
                List<RestServiceValue.ClassItem> items = values.getByConfig(key);
                if (items != null) {

                    // update class values from properties
                    if (value.config.log.isPresent()) {
                        items.forEach(item -> {
                            value.config.log.ifPresent(x -> item.config.log = x);
                        });
                    }

                    value.method.forEach((mk, mv) -> {

                        items.forEach(item -> {
                            List<RestServiceValue.Item> methods = item.getByConfig(mk);
                            if (methods != null) {
                                methods.forEach(method -> {
                                    mv.log.ifPresent(x -> method.log = x);
                                });
                            }
                        });
                    });
                }
            });
        }
    }

    public static boolean excludeUrl(String url) {
        if (PATTERNS == null) {
            return false;
        }
        for (Pattern pattern : PATTERNS) {
            if (pattern.matcher(url).matches()) {
                return true;
            }
        }
        return false;
    }

    public static RestRuntimeConfig getConfig() {
        return CONFIG;
    }


    public static RestServiceValue.Item getRestService(String clazz, String method) {
        RestServiceValue.ClassItem c = REST_SERVICE.classes.get(clazz);
        if (c == null) {
            return RestServiceValue.DEFAULT;
        }
        RestServiceValue.Item m = c.methods.get(method);
        if (m == null) {
            return c.config;
        }
        return m;
    }

}
