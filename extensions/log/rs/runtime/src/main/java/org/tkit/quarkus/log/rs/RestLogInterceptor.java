/*
 * Copyright 2019 1000kit.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tkit.quarkus.log.rs;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.log.cdi.BusinessLoggingUtil;
import org.tkit.quarkus.log.cdi.LogService;
import org.tkit.quarkus.log.cdi.context.CorrelationScope;
import org.tkit.quarkus.log.cdi.context.TkitLogContext;
import org.tkit.quarkus.log.cdi.interceptor.InterceptorContext;
import org.tkit.quarkus.log.cdi.interceptor.LogConfig;
import org.tkit.quarkus.log.cdi.interceptor.LogServiceInterceptor;

import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.tkit.quarkus.log.rs.RestConfig.convert;

/**
 * The rest log interceptor.
 */
@Provider
@LogService(log = false)
public class RestLogInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * The annotation interceptor property.
     */
    private static final String ANO = "ano";

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
        disableProtectedMethod = config.getOptionalValue(LogServiceInterceptor.PROP_DISABLE_PROTECTED_METHODS, Boolean.class).orElse(true);
        String[] items = config.getOptionalValue("tkit.log.rs.header", String[].class).orElse(null);
        headersLog.putAll(convert(items));
    }

    /**
     * The resource info.
     */
    @Context
    ResourceInfo resourceInfo;

    @Context
    HttpHeaders headers;

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (disable) {
            return;
        }
        LogService ano = LogServiceInterceptor.getLoggerServiceAno(resourceInfo.getResourceClass(), resourceInfo.getResourceClass().getName(), resourceInfo.getResourceMethod(), disableProtectedMethod);
        requestContext.setProperty(ANO, ano);

        //start log scope/correlation id
        String correlationId;
        if (requestContext.getHeaders().containsKey(TkitLogContext.X_CORRELATION_ID)) {
            //if client has sent us correlation id, take it over
            correlationId = requestContext.getHeaders().getFirst(TkitLogContext.X_CORRELATION_ID);
        } else {
            //correlation id not sent by client, we start our own
            correlationId = UUID.randomUUID().toString();
        }
        CorrelationScope correlationScope = new CorrelationScope(correlationId);
        TkitLogContext.set(correlationScope);

        // add header parameters to MDC
        for (Map.Entry<String, String> e : headersLog.entrySet()) {
            String tmp = requestContext.getHeaderString(e.getKey());
            if (tmp != null && !tmp.isBlank()) {
                MDC.put(e.getValue(), tmp);
            }
        }

        if (ano.log()) {
            InterceptorContext context = new InterceptorContext(requestContext.getMethod(), requestContext.getUriInfo().getRequestUri().toString());
            requestContext.setProperty(CONTEXT, context);

            // mdc log
            if (mdcLog) {
                MDC.put("rs-method", context.method);
                MDC.put("rs-uri", context.parameters);
            }

            // create the logger
            Logger logger = LoggerFactory.getLogger(resourceInfo.getResourceClass());
            boolean hasEntity = requestContext.getMediaType() != null && requestContext.getLength() > 0;
            logger.info("{}", LogConfig.msg(messageStart, new Object[]{context.method, requestContext.getUriInfo().getRequestUri(), hasEntity}));
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (disable) {
            return;
        }

        LogService ano = (LogService) requestContext.getProperty(ANO);
        if (ano != null && ano.log()) {
            try {
                InterceptorContext context = (InterceptorContext) requestContext.getProperty(CONTEXT);
                Response.StatusType status = responseContext.getStatusInfo();
                context.closeContext(status.getReasonPhrase());

                // mdc log
                if (mdcLog) {
                    MDC.put("rs-method", context.method);
                    MDC.put("rs-uri", context.parameters);
                    MDC.put("rs-time", context.time);
                    MDC.put("rs-response-status", status.getStatusCode());
                    MDC.put("rs-response-reason", status.getReasonPhrase());
                    MDC.put("rs-response-entity", responseContext.hasEntity());
                }

                Logger logger = LoggerFactory.getLogger(resourceInfo.getResourceClass());
                logger.info("{}", LogConfig.msg(messageSucceed,
                        new Object[]{
                                context.method,
                                context.parameters,
                                context.time,
                                status.getStatusCode(),
                                status.getReasonPhrase(),
                                responseContext.hasEntity()
                        }));
            } finally {
                //we are exiting server context, clear the log context
                TkitLogContext.set(null);
                // mdc log
                for (String e : headersLog.keySet()) {
                    MDC.remove(e);
                }
                if (mdcLog) {
                    MDC.remove("rs-method");
                    MDC.remove("rs-uri");
                    MDC.remove("rs-entity");
                    MDC.remove("rs-time");
                    MDC.remove("rs-response-status");
                    MDC.remove("rs-response-reason");
                    MDC.remove("rs-response-entity");

                    BusinessLoggingUtil.removeAll();
                }
            }
        }
    }

}
