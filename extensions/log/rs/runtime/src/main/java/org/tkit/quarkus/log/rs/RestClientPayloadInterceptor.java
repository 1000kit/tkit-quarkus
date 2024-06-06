package org.tkit.quarkus.log.rs;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Provider
@RegisterForReflection
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
                        String.format(config.client.payload.template,
                                requestContext.getMethod(),
                                requestContext.getUri().getPath(),
                                requestContext.getEntity()));
            }
        }

    }
}
