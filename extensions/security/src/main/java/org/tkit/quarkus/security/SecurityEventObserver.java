package org.tkit.quarkus.security;

import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.security.ForbiddenException;
import io.quarkus.security.spi.runtime.AbstractSecurityEvent;
import io.quarkus.security.spi.runtime.AuthenticationFailureEvent;
import io.quarkus.security.spi.runtime.AuthorizationFailureEvent;
import io.vertx.ext.web.RoutingContext;

public class SecurityEventObserver {

    private static final Logger log = LoggerFactory.getLogger(SecurityEventObserver.class);

    @Inject
    SecurityConfig config;

    void observeAuthenticationFailure(@ObservesAsync AuthenticationFailureEvent event) {
        authenticationFailure(event);
    }

    void observeAuthorizationFailure(@ObservesAsync AuthorizationFailureEvent event) {
        if (!log.isWarnEnabled()) {
            return;
        }
        if (event.getAuthorizationFailure() instanceof ForbiddenException) {
            authorizationFailure(event);
        } else {
            authenticationFailure(event);
        }
    }

    private void authenticationFailure(AbstractSecurityEvent event) {
        if (!log.isWarnEnabled()) {
            return;
        }
        if (!config.events().authentication().log()) {
            return;
        }
        var routingContext = (RoutingContext) event.getEventProperties().get(RoutingContext.class.getName());
        var method = routingContext.request().method().name();
        var path = routingContext.request().path();
        log.warn(String.format(config.events().authentication().template(), method, path));
    }

    private void authorizationFailure(AuthorizationFailureEvent event) {
        if (!log.isWarnEnabled()) {
            return;
        }
        if (!config.events().authorization().log()) {
            return;
        }
        RoutingContext routingContext = (RoutingContext) event.getEventProperties().get(RoutingContext.class.getName());
        var method = routingContext.request().method().name();
        var path = routingContext.request().path();
        var principal = event.getSecurityIdentity().getPrincipal().getName();
        log.warn(String.format(config.events().authorization().template(), method, path, principal));
    }
}
