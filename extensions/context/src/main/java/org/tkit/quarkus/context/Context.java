package org.tkit.quarkus.context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApplicationContext {



    /**
     * User principal
     */
    public final String principal;

    /**
     * Correlation id that should be preserved throughout invocation chain
     */
    public final String correlationId;

    /**
     * Business context that should be preserved throughout invocation chain
     */
    public final String businessContext;

    /**
     * Additional metadata that should be preserved throughout invocation chain
     */
    public final Map<String, String> meta;

    private ApplicationContext(String correlationId, String businessContext, String principal, Map<String, String> meta) {
        this.correlationId = correlationId;
        this.businessContext = businessContext;
        this.meta = meta;
        this.principal = principal;
    }

    @Override
    public String toString() {
        return "correlationId=" + correlationId;
    }

    public static ApplicationContextBuilder builder() {
        return new ApplicationContextBuilder();
    }

    public static class ApplicationContextBuilder {

        private String principal;

        private String correlationId = UUID.randomUUID().toString();

        private String businessContext;

        private Map<String, String> meta = new HashMap<>();

        public ApplicationContextBuilder correlationId(String correlationId) {
            if (correlationId == null) {
                return this;
            }
            this.correlationId = correlationId;
            return this;
        }

        public ApplicationContextBuilder principal(String principal) {
            this.principal = principal;
            return this;
        }

        public ApplicationContextBuilder businessContext(String businessContext) {
            this.businessContext = businessContext;
            return this;
        }

        public ApplicationContext build() {
            return new ApplicationContext(correlationId, businessContext, principal, meta);
        }
    }
}