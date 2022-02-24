package org.tkit.quarkus.log.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Provider
public class RestClientPayloadInterceptor implements ClientRequestFilter {

    /**
     * The logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(RestClientLogInterceptor.class);

    @Override
    public void filter(ClientRequestContext requestContext) {
        RestRuntimeConfig config = RestRecorder.getConfig();
        if (config.client.payload.enabled) {
            if (requestContext.getMethod().equals(HttpMethod.POST) || requestContext.getMethod().equals(HttpMethod.PUT)) {

                // check regex exclude
                if (config.client.payload.regex.enabled) {
                    if (RestRecorder.excludePayloadUrl(requestContext.getUri().getPath())) {
                        return;
                    }
                }
                log.info(
                        String.format(config.payload.message,
                                requestContext.getMethod(),
                                requestContext.getUri().getPath(),
                                requestContext.getEntity())
                );
            }
        }

    }
}
