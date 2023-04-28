package org.tkit.quarkus.log.rs;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.context.ApplicationContext;

/**
 * The rest client log interceptor
 *
 */
@Provider
public class RestClientLogInterceptor implements ClientRequestFilter, ClientResponseFilter {

    /**
     * The logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(RestClientLogInterceptor.class);

    /**
     * The context interceptor property.
     */
    private static final String CONTEXT = "context";

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ClientRequestContext requestContext) {
        RestRuntimeConfig config = RestRecorder.getConfig();

        if (!config.client.enabled) {
            return;
        }
        //propagate our log context if present
        if (!ApplicationContext.isEmpty() && config.correlationIdEnabled) {
            requestContext.getHeaders().add(config.correlationIdHeader, ApplicationContext.get().correlationId);
        }

        RestInterceptorContext context = new RestInterceptorContext();
        context.method = requestContext.getMethod();
        context.uri = requestContext.getUri().toString();

        // add header parameters to MDC
        if (config.client.mdcHeaders != null && !config.client.mdcHeaders.isEmpty()) {
            config.client.mdcHeaders.forEach((k, v) -> {
                String tmp = requestContext.getHeaderString(k);
                if (tmp != null && !tmp.isBlank()) {
                    MDC.put(v, tmp);
                    context.mdcKeys.add(v);
                }
            });
        }

        if (config.client.start.enabled) {
            log.info(String.format(config.client.start.template, context.method, context.uri));
        }
        requestContext.setProperty(CONTEXT, context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
        RestRuntimeConfig config = RestRecorder.getConfig();

        if (!config.client.enabled) {
            return;
        }

        RestInterceptorContext context = (RestInterceptorContext) requestContext.getProperty(CONTEXT);
        if (context == null) {
            if (config.client.error.enabled) {
                Response.StatusType status = responseContext.getStatusInfo();
                log.info(String.format(config.client.end.template, requestContext.getMethod(),
                        requestContext.getUri().getPath(), 0.000,
                        status.getStatusCode(), status.getReasonPhrase()));
            }
            return;
        }

        try {
            Response.StatusType status = responseContext.getStatusInfo();
            context.close();

            if (config.client.end.enabled) {
                log.info(String.format(config.client.end.template, context.method, context.uri,
                        context.time, status.getStatusCode(), status.getReasonPhrase()));
            }
        } finally {
            // clean up MDC header keys
            context.mdcKeys.forEach(MDC::remove);
        }
    }
}
