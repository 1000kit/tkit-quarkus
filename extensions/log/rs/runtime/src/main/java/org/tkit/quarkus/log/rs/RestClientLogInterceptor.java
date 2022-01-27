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
import org.tkit.quarkus.log.cdi.LogService;
import org.tkit.quarkus.log.cdi.context.TkitLogContext;
import org.tkit.quarkus.log.cdi.interceptor.InterceptorContext;
import org.tkit.quarkus.log.cdi.interceptor.LogConfig;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static org.tkit.quarkus.log.rs.RestConfig.convert;

/**
 * The rest client log interceptor
 *
 */
@Provider
@LogService(log = false)
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
     * The message start.
     */
    private static final MessageFormat messageStart;

    /**
     * The message succeed.
     */
    private static final MessageFormat messageSucceed;


    /**
     * The rest client logger interceptor disable flag.
     */
    private static final boolean disable;

    /**
     * Add MDC parameters to the log.
     */
    private static final boolean mdcLog;

    /**
     * Headers log parameters
     */
    private static final Map<String,String> headersLog = new HashMap<>();

    static  {
        Config config = ConfigProvider.getConfig();
        messageStart = new MessageFormat(config.getOptionalValue("tkit.log.rs.client.start", String.class).orElse("{0} {1} [{2}] started."));
        messageSucceed = new MessageFormat(config.getOptionalValue("tkit.log.rs.client.succeed", String.class).orElse("{0} {1} finished in [{2}s] with [{3}-{4},{5}]."));
        disable = config.getOptionalValue("tkit.log.rs.client.disable", Boolean.class).orElse(false);
        mdcLog = config.getOptionalValue("tkit.log.rs.client.mdc", Boolean.class).orElse(false);
        String[] items = config.getOptionalValue("tkit.log.rs.client.header", String[].class).orElse(null);
        headersLog.putAll(convert(items));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ClientRequestContext requestContext) {
        if (disable) {
            return;
        }
        //propagate our log context if present
        if (TkitLogContext.get() != null) {
            requestContext.getHeaders().add(TkitLogContext.X_CORRELATION_ID, TkitLogContext.get().correlationId);
        }

        InterceptorContext context = new InterceptorContext(requestContext.getMethod(), requestContext.getUri().toString());

        // add header parameters to MDC
        for (Map.Entry<String, String> e : headersLog.entrySet()) {
            String tmp = requestContext.getHeaderString(e.getKey());
            if (tmp != null && !tmp.isBlank()) {
                MDC.put(e.getValue(), tmp);
            }
        }

        // mdc log
        if (mdcLog) {
            MDC.put("rs-client-method", context.method);
            MDC.put("rs-client-uri", context.parameters);
        }
        requestContext.setProperty(CONTEXT, context);
        log.info(LogConfig.msg(messageStart, new Object[]{requestContext.getMethod(), requestContext.getUri(), requestContext.hasEntity()}));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
        if (disable) {
            return;
        }
        InterceptorContext context = (InterceptorContext) requestContext.getProperty(CONTEXT);
        if (context != null) {
            try {
                Response.StatusType status = responseContext.getStatusInfo();
                context.closeContext(status.getReasonPhrase());

                // mdc log
                if (mdcLog) {
                    MDC.put("rs-client-method", context.method);
                    MDC.put("rs-client-uri", context.parameters);
                    MDC.put("rs-client-time", context.time);
                    MDC.put("rs-client-response-status", status.getStatusCode());
                    MDC.put("rs-client-response-phrase", status.getReasonPhrase());
                    MDC.put("rs-client-response-entity", responseContext.hasEntity());
                }

                log.info(LogConfig.msg(messageSucceed, new Object[]{context.method, requestContext.getUri(), context.time, status.getStatusCode(), context.result, responseContext.hasEntity()}));
            } finally {
                // mdc log
                for (String e : headersLog.keySet()) {
                    MDC.remove(e);
                }
                if (mdcLog) {
                    MDC.remove("rs-client-method");
                    MDC.remove("rs-client-uri");
                    MDC.remove("rs-client-entity");
                    MDC.remove("rs-client-time");
                    MDC.remove("rs-client-response-status");
                    MDC.remove("rs-client-response-phrase");
                    MDC.remove("rs-client-response-entity");
                }
            }
        }
    }
}
