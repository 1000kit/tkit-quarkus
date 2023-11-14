package org.tkit.quarkus.log.rs;

import javax.annotation.Priority;
import javax.ws.rs.container.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

/**
 * The rest log interceptor.
 */
@Provider
@Priority(1)
public class RestLogInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * The context interceptor property.
     */
    private static final String CONTEXT = "context";

    private static final Logger log = LoggerFactory.getLogger(RestLogInterceptor.class);

    @javax.ws.rs.core.Context
    ResourceInfo resourceInfo;

    @javax.ws.rs.core.Context
    HttpHeaders headers;

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        RestRuntimeConfig config = RestRecorder.getConfig();

        //start log scope/correlation id
        String correlationId = null;
        if (config.correlationIdEnabled) {
            correlationId = requestContext.getHeaders().getFirst(config.correlationIdHeader);
        }

        ApplicationContext.start(Context.builder().correlationId(correlationId).build());

        if (!config.enabled) {
            return;
        }

        RestInterceptorContext restContext = new RestInterceptorContext();

        // check regex exclude
        if (config.regex.enabled) {
            String url = requestContext.getUriInfo().getPath();
            restContext.exclude = RestRecorder.excludeUrl(url);
            if (restContext.exclude) {
                requestContext.setProperty(CONTEXT, restContext);
                return;
            }
        }

        // add header parameters to MDC
        if (config.mdcHeaders != null && !config.mdcHeaders.isEmpty()) {
            config.mdcHeaders.forEach((k, v) -> {
                String tmp = requestContext.getHeaderString(k);
                if (tmp != null && !tmp.isBlank()) {
                    MDC.put(v, tmp);
                    restContext.mdcKeys.add(v);
                }
            });
        }

        restContext.ano = RestRecorder.getRestService(resourceInfo.getResourceClass().getName(),
                resourceInfo.getResourceMethod().getName());

        UriInfo uriInfo = requestContext.getUriInfo();
        restContext.method = requestContext.getMethod();
        restContext.path = uriInfo.getPath();
        restContext.uri = uriInfo.getRequestUri().toString();

        // start message log
        boolean log = true;
        if (restContext.ano != null) {
            log = restContext.ano.config.log;
        }
        if (config.start.enabled && log) {
            LoggerFactory.getLogger(resourceInfo.getResourceClass())
                    .info(String.format(config.start.template, restContext.method, restContext.path, restContext.uri));
        }
        requestContext.setProperty(CONTEXT, restContext);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        try {
            RestRuntimeConfig config = RestRecorder.getConfig();
            if (!config.enabled) {
                return;
            }

            RestInterceptorContext restContext = (RestInterceptorContext) requestContext.getProperty(CONTEXT);

            // if we do not have context we are in error mode
            if (restContext == null) {
                if (config.error.enabled) {
                    Response.StatusType status = responseContext.getStatusInfo();
                    log.info(String.format(config.end.template, requestContext.getMethod(),
                            requestContext.getUriInfo().getPath(), 0.000,
                            status.getStatusCode(), status.getReasonPhrase(), requestContext.getUriInfo().getRequestUri()));
                }
                return;
            }

            try {
                // stop if we need to exclude
                if (restContext.exclude) {
                    return;
                }

                // close rest context
                restContext.close();

                // log end message
                boolean log = true;
                if (restContext.ano != null) {
                    log = restContext.ano.config.log;
                }
                if (config.end.enabled && log) {

                    Response.StatusType status = responseContext.getStatusInfo();

                    if (config.end.mdc.enabled) {
                        MDC.put(config.end.mdc.durationName, restContext.time);
                        restContext.mdcKeys.add(config.end.mdc.durationName);

                        MDC.put(config.end.mdc.responseStatusName, status.getStatusCode());
                        restContext.mdcKeys.add(config.end.mdc.responseStatusName);
                    }

                    LoggerFactory.getLogger(resourceInfo.getResourceClass())
                            .info(String.format(config.end.template, restContext.method, restContext.path,
                                    restContext.time, status.getStatusCode(), status.getReasonPhrase(),
                                    restContext.uri));
                }
            } finally {
                // clean up MDC header keys
                restContext.mdcKeys.forEach(MDC::remove);
            }
        } finally {
            //we are exiting server context, close the application context
            ApplicationContext.close();
        }
    }

}
