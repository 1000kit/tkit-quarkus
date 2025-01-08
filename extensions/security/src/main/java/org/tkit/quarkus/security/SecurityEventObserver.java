package org.tkit.quarkus.security;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.security.spi.runtime.AuthenticationFailureEvent;
import io.quarkus.security.spi.runtime.AuthorizationFailureEvent;
import io.vertx.ext.web.RoutingContext;

public class SecurityEventObserver {

    private static final Logger log = LoggerFactory.getLogger(SecurityEventObserver.class);

    @Inject
    SecurityConfig config;

    void observeAuthenticationFailure(@ObservesAsync AuthenticationFailureEvent event) {
        if (!config.events().log()) {
            return;
        }
        if (log.isWarnEnabled()) {
            RoutingContext routingContext = (RoutingContext) event.getEventProperties().get(RoutingContext.class.getName());
            log.warn("Authentication failed, request path: {}, code: {}", routingContext.request().path(),
                    routingContext.response().getStatusCode());
        }
    }

    void observeAuthorizationFailure(@Observes AuthorizationFailureEvent event) {
        if (!config.events().log()) {
            return;
        }
        if (log.isWarnEnabled()) {
            RoutingContext routingContext = (RoutingContext) event.getEventProperties().get(RoutingContext.class.getName());
            log.warn("Authorization failed, request path: {}, code: {}", routingContext.request().path(),
                    routingContext.response().getStatusCode());
        }
    }

}
