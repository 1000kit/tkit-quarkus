package org.tkit.quarkus.log.cdi.context;

import org.eclipse.microprofile.context.spi.ThreadContextProvider;
import org.eclipse.microprofile.context.spi.ThreadContextSnapshot;
import org.slf4j.MDC;

import java.util.Map;

/**
 * MP context provider for propagation of MDC between threads.
 *
 */
public class MdcContextProvider implements ThreadContextProvider {

    @Override
    public ThreadContextSnapshot currentContext(Map<String, String> props) {
        Map<String, String> propagate = MDC.getCopyOfContextMap();
        return () -> {
            Map<String, String> old = MDC.getCopyOfContextMap();
            MDC.setContextMap(propagate);
            return () -> {
                MDC.setContextMap(old);
            };
        };
    }

    @Override
    public ThreadContextSnapshot clearedContext(Map<String, String> props) {
        return () -> {
            Map<String, String> old = MDC.getCopyOfContextMap();
            MDC.clear();
            return () -> {
                MDC.setContextMap(old);
            };
        };
    }

    @Override
    public String getThreadContextType() {
        return "Tkit log MDC";
    }
}