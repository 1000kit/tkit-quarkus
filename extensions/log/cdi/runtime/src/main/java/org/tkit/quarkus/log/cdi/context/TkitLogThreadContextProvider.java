package org.tkit.quarkus.log.cdi.context;

import org.eclipse.microprofile.context.spi.ThreadContextProvider;
import org.eclipse.microprofile.context.spi.ThreadContextSnapshot;

import java.util.Map;

/**
 * Microprofile context provider for propagation of correlation scope between threads.
 */
public class TkitLogThreadContextProvider implements ThreadContextProvider {
    private static final String TYPE = "Tkit Log";

    @Override
    public ThreadContextSnapshot currentContext(Map<String, String> props) {
        CorrelationScope captured = TkitLogContext.get();
        return () -> {
            CorrelationScope current = restore(captured);
            return () -> restore(current);
        };
    }

    private CorrelationScope restore(CorrelationScope context) {
        CorrelationScope currentContext = TkitLogContext.get();
        if (context == null) {
            TkitLogContext.set(null);
        } else {
            TkitLogContext.set(context);
        }
        return currentContext;
    }

    @Override
    public ThreadContextSnapshot clearedContext(Map<String, String> props) {
        return () -> {
            CorrelationScope current = restore(null);
            return () -> restore(current);
        };
    }


    @Override
    public String getThreadContextType() {
        return TYPE;
    }
}
