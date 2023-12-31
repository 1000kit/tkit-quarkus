package org.tkit.quarkus.security;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;

import io.quarkus.security.spi.runtime.AuthorizationController;

@Alternative
@Priority(Interceptor.Priority.LIBRARY_AFTER)
@ApplicationScoped
public class DisabledAuthController extends AuthorizationController {

    @Inject
    SecurityConfig config;

    @Override
    public boolean isAuthorizationEnabled() {
        return config.enabled();
    }
}
