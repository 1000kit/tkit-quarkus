package org.tkit.quarkus.log.rs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class RestPayloadInterceptor implements ContainerRequestFilter {

    /**
     * The resource info.
     */
    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        RestRuntimeConfig config = RestRecorder.getConfig();
        if (!config.payload().enabled()) {
            return;
        }
        if (!(requestContext.getMethod().equals(HttpMethod.POST) || requestContext.getMethod().equals(HttpMethod.PUT))) {
            return;
        }

        // check regex exclude
        if (RestRecorder.isRegexPayloadEnabled()) {
            if (RestRecorder.excludePayloadUrl(requestContext.getUriInfo().getPath())) {
                return;
            }
        }

        // check annotation
        RestServiceValue.MethodItem ano = RestRecorder.getRestService(resourceInfo.getResourceClass().getName(),
                resourceInfo.getResourceMethod().getName());
        if (ano != null && !ano.config.payload) {
            return;
        }

        //TODO: https://github.com/quarkusio/quarkus/issues/23263

        Logger logger = LoggerFactory.getLogger(resourceInfo.getResourceClass());
        InputStream stream = requestContext.getEntityStream();
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        stream.mark(config.payload().maxEntitySize() + 1);
        StringBuilder sb = new StringBuilder();
        stream.mark(config.payload().maxEntitySize() + 1);
        final byte[] entity = new byte[config.payload().maxEntitySize() + 1];
        final int entitySize = stream.read(entity);
        if (entitySize <= 0) {
            if (config.payload().emptyBodyEnabled()) {
                sb.append(config.payload().emptyBodyMessage());
            }
        } else {
            sb.append(new String(entity, 0, Math.min(entitySize, config.payload().maxEntitySize()), StandardCharsets.UTF_8));
            if (entitySize > config.payload().maxEntitySize()) {
                sb.append(config.payload().pageMessage());
            }
        }
        if (!sb.isEmpty()) {
            logger.info(
                    String.format(config.payload().template(), requestContext.getMethod(),
                            requestContext.getUriInfo().getPath(),
                            sb));
        }
        stream.reset();
        requestContext.setEntityStream(stream);
    }
}
