package org.tkit.quarkus.context;

import java.util.Map;

import org.eclipse.microprofile.context.spi.ThreadContextProvider;
import org.eclipse.microprofile.context.spi.ThreadContextSnapshot;

/**
 * Microprofile context provider for propagation of correlation scope between threads.
 */
public class ApplicationContextProvider implements ThreadContextProvider {

    private static final String TYPE = "tkit-application-context";

    @Override
    public ThreadContextSnapshot currentContext(Map<String, String> props) {
        Context captured = ApplicationContext.get();
        return () -> {
            Context current = restore(captured);
            return () -> restore(current);
        };
    }

    private Context restore(Context context) {
        Context currentContext = ApplicationContext.get();
        ApplicationContext.set(context);
        return currentContext;
    }

    @Override
    public ThreadContextSnapshot clearedContext(Map<String, String> props) {
        return () -> {
            Context current = restore(null);
            return () -> restore(current);
        };
    }

    @Override
    public String getThreadContextType() {
        return TYPE;
    }
}
