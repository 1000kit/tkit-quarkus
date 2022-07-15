package org.tkit.quarkus.log.rs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class RestRecorder {

    private static final Logger log = LoggerFactory.getLogger(RestRecorder.class);

    static RestRuntimeConfig CONFIG;

    static RestServiceValue REST_SERVICE;

    static List<Pattern> PATTERNS;

    static List<Pattern> PAYLOAD_PATTERNS;

    public void init(RestRuntimeConfig config, RestServiceValue values) {
        CONFIG = config;
        if (config.regex.enabled) {
            List<String> items = config.regex.exclude.orElse(null);
            PATTERNS = createPatterns(items);
            if (PATTERNS == null) {
                config.regex.enabled = false;
                log.info("No exclude regex patterns found. Disable exclude regex patterns for rest log interceptor.");
            }
        }
        if (config.payload.regex.enabled) {
            List<String> items = config.payload.regex.exclude.orElse(null);
            PAYLOAD_PATTERNS = createPatterns(items);
            if (PAYLOAD_PATTERNS == null) {
                config.payload.regex.enabled = false;
                log.info(
                        "No exclude regex payload patterns found. Disable exclude regex patterns for rest payload interceptor.");
            }
        }
        REST_SERVICE = values;

        config.controller.forEach((key, value) -> {
            List<RestServiceValue.ClassItem> items = values.getByConfig(key);
            if (items == null) {
                log.warn(
                        "No @RestService annotation found for key `tkit.log.rs.controller.\"{}\"`. Key will be ignored",
                        key);
                return;
            }

            // update class values from properties
            if (value.config.log.isPresent() || value.config.payload.isPresent()) {
                items.forEach(item -> {
                    // update class config from properties
                    if (item.config != null) {
                        value.config.log.ifPresent(x -> item.config.log = x);
                        value.config.payload.ifPresent(x -> item.config.payload = x);
                    } else {
                        log.warn(
                                "No @RestService annotation found for class {}. Key `tkit.log.rs.controller.\"{}\"` will be ignored",
                                item.id, key);
                    }
                });
            }

            // update method values from properties
            value.method.forEach((mk, mv) -> {
                items.forEach(item -> {
                    List<RestServiceValue.MethodItem> methods = item.getByConfig(mk);
                    if (methods != null) {
                        methods.forEach(method -> {
                            // update config from class if method config found
                            if (method.config == null && item.config != null) {
                                method.copyConfig(item.config);
                            }
                            // update method config from properties
                            if (method.config != null) {
                                mv.log.ifPresent(x -> method.config.log = x);
                                mv.payload.ifPresent(x -> method.config.payload = x);
                            } else {
                                log.warn(
                                        "No @RestService annotation found for method `{}.{}`. Key `tkit.log.rs.controller.\"{}\".method.{}` will be ignored",
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

    private static List<Pattern> createPatterns(List<String> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        List<Pattern> tmp = new ArrayList<>();
        items.forEach(item -> {
            try {
                tmp.add(Pattern.compile(item));
            } catch (PatternSyntaxException ex) {
                log.error("Error compile regex pattern '{}' error {}", item, ex.getMessage());
            }
        });
        return tmp;
    }

    public static boolean excludePayloadUrl(String url) {
        return excludeUrl(PAYLOAD_PATTERNS, url);
    }

    public static boolean excludeUrl(String url) {
        return excludeUrl(PATTERNS, url);
    }

    private static boolean excludeUrl(List<Pattern> patterns, String url) {
        if (patterns == null) {
            return false;
        }
        for (Pattern pattern : patterns) {
            if (pattern.matcher(url).matches()) {
                return true;
            }
        }
        return false;
    }

    public static RestRuntimeConfig getConfig() {
        return CONFIG;
    }

    public static RestServiceValue.MethodItem getRestService(String clazz, String method) {
        RestServiceValue.ClassItem classItem = REST_SERVICE.get(clazz);
        if (classItem == null) {
            return null;
        }
        return classItem.methods.get(method);
    }

}
