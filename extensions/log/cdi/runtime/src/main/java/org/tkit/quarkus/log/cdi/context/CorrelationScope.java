package org.tkit.quarkus.log.cdi.context;

import java.util.Map;

/**
 * Correlation scope storage object.
 */
public class CorrelationScope {
    /** Correlation id that should be preserved throughout invocation chain */
    public final String correlationId;
    /** Business context that should be preserved throughout invocation chain */
    public final String businessContext;
    /** Additional metadata that should be preserved throughout invocation chain */
    public final Map<String, String> meta ;

    public CorrelationScope(String correlationId) {
        this.correlationId = correlationId;
        this.businessContext = null;
        this.meta = null;
    }
    public CorrelationScope(String correlationId, String businessContext, Map<String, String> meta) {
        this.correlationId = correlationId;
        this.businessContext = businessContext;
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "correlationId=" + correlationId ;
    }
}
