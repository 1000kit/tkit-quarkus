package org.tkit.quarkus.log.cdi;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.MDC;

public class BusinessLoggingUtil {

    private static final String BUSINESS_DATA_PREFIX = ConfigProvider.getConfig().getOptionalValue("quarkus.tkit.log.customdata.prefix", String.class).orElse("business_information_");

    public static void add(String key, String value) {
        MDC.put(BUSINESS_DATA_PREFIX + key, value);
    }

    public static void remove(String key) {
        MDC.remove(BUSINESS_DATA_PREFIX + key);
    }

    public static void removeAll(){
        MDC.getCopyOfContextMap().keySet().stream().filter(m -> m.startsWith(BUSINESS_DATA_PREFIX)).forEach(MDC::remove);
    }
}

