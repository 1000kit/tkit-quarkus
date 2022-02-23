package org.tkit.quarkus.log.rs;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.context.Context;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.log.cdi.BusinessLoggingUtil;
import org.tkit.quarkus.log.cdi.LogService;
import org.tkit.quarkus.log.cdi.interceptor.LogConfig;

import javax.ws.rs.container.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static org.tkit.quarkus.log.cdi.interceptor.LogConfig.PROP_DISABLE_PROTECTED_METHODS;
import static org.tkit.quarkus.log.cdi.interceptor.LogConfig.getLoggerServiceAno;
import static org.tkit.quarkus.log.rs.RestConfig.convert;

/**
 * The rest log interceptor.
 */
@Provider
@LogService(log = false)
public class RestControllerInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * The context interceptor property.
     */
    private static final String CONTEXT = "context";

    /**
     * The message start.
     */
    private static final MessageFormat messageStart;

    /**
     * The message succeed.
     */
    private static final MessageFormat messageSucceed;

    /**
     * Add MDC parameters to the log.
     */
    private static final boolean mdcLog;

    /**
     * The rest logger interceptor disable flag.
     */
    private static final boolean disable;

    /**
     * Disable the protected methods to log.
     */
    private static final boolean disableProtectedMethod;

    /**
     * Headers log parameters
     */
    private static final Map<String, String> headersLog = new HashMap<>();


    static {
        Config config = ConfigProvider.getConfig();
        messageStart = new MessageFormat(config.getOptionalValue("tkit.log.rs.start", String.class).orElse("{0} {1} [{2}] started."));
        messageSucceed = new MessageFormat(config.getOptionalValue("tkit.log.rs.succeed", String.class).orElse("{0} {1} [{2}s] finished [{3}-{4},{5}]."));
        disable = config.getOptionalValue("tkit.log.rs.disable", Boolean.class).orElse(false);
        mdcLog = config.getOptionalValue("tkit.log.rs.mdc", Boolean.class).orElse(false);
        disableProtectedMethod = config.getOptionalValue(PROP_DISABLE_PROTECTED_METHODS, Boolean.class).orElse(true);
        String[] items = config.getOptionalValue("tkit.log.rs.header", String[].class).orElse(null);
        headersLog.putAll(convert(items));
    }

    /**
     * The resource info.
     */
    @javax.ws.rs.core.Context
    ResourceInfo resourceInfo;

    @javax.ws.rs.core.Context
    HttpHeaders headers;

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (disable) {
            return;
        }

        //start log scope/correlation id
        String correlationId = requestContext.getHeaders().getFirst(RestConfig.HEADER_X_CORRELATION_ID);
        ApplicationContext.start(Context.builder().correlationId(correlationId).build());

        // add header parameters to MDC
        for (Map.Entry<String, String> e : headersLog.entrySet()) {
            String tmp = requestContext.getHeaderString(e.getKey());
            if (tmp != null && !tmp.isBlank()) {
                MDC.put(e.getValue(), tmp);
            }
        }

        LogService ano = getLoggerServiceAno(resourceInfo.getResourceClass(), resourceInfo.getResourceClass().getName(), resourceInfo.getResourceMethod(), disableProtectedMethod);
        RestInterceptorContext context = new RestInterceptorContext(ano);

        if (ano.log()) {
            context.method = requestContext.getMethod();
            context.uri = requestContext.getUriInfo().getRequestUri().toString();

            // create the logger
            Logger logger = LoggerFactory.getLogger(resourceInfo.getResourceClass());
            boolean hasEntity = requestContext.getMediaType() != null && requestContext.getLength() > 0;
            logger.info("{}", LogConfig.msg(messageStart, new Object[]{context.method, requestContext.getUriInfo().getRequestUri(), hasEntity}));
        }

        requestContext.setProperty(CONTEXT, context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (disable) {
            return;
        }

        try {
            RestInterceptorContext context = (RestInterceptorContext) requestContext.getProperty(CONTEXT);
            if (context != null && context.ano.log()) {
                try {
                    Response.StatusType status = responseContext.getStatusInfo();
                    Logger logger = LoggerFactory.getLogger(resourceInfo.getResourceClass());
                    context.close();

                    logger.info("{}", LogConfig.msg(messageSucceed,
                            new Object[]{
                                    context.method,
                                    context.uri,
                                    context.time,
                                    status.getStatusCode(),
                                    status.getReasonPhrase(),
                                    responseContext.hasEntity()
                            }));
                } finally {
                    // mdc log
                    for (String e : headersLog.keySet()) {
                        MDC.remove(e);
                    }
                    if (mdcLog) {
                        BusinessLoggingUtil.removeAll();
                    }
                }
            }
        } finally {
            //we are exiting server context, close the application context
            ApplicationContext.close();
        }
    }

}
