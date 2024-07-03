package org.tkit.quarkus.security.test;

import static org.tkit.quarkus.security.test.SecurityTestUtils.*;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.callback.*;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

/**
 * This junit5 extension is using the
 *
 * @see KeycloakTestClient
 *      to generate clients with scopes
 * @see GenerateKeycloakClient
 */
public class GenerateKeycloakClientExtension
        implements QuarkusTestBeforeEachCallback,
        QuarkusTestAfterEachCallback {

    private static final Logger log = LoggerFactory.getLogger(GenerateKeycloakClientExtension.class);

    @Override
    public void beforeEach(QuarkusTestMethodContext context) {
        GenerateKeycloakClient methodAn = context.getTestMethod().getAnnotation(GenerateKeycloakClient.class);
        if (methodAn != null) {
            log.info("[GENERATE KEYCLOAK TEST-CLIENT] Init method level data for {} , client: {}",
                    context.getTestMethod().getName(),
                    methodAn.clientName());

            addClient(methodAn.clientName(),
                    Arrays.stream(methodAn.scopes()).toList());

        } else {
            GenerateKeycloakClient an = context.getTestInstance().getClass().getAnnotation(GenerateKeycloakClient.class);
            if (an == null) {
                return;
            }
            log.info("[GENERATE KEYCLOAK TEST-CLIENT] Init class level data for {} , client: {}",
                    context.getTestMethod().getClass().getSimpleName(),
                    an.clientName());
            addClient(an.clientName(), Arrays.stream(an.scopes()).toList());
        }
    }

    @Override
    public void afterEach(QuarkusTestMethodContext context) {
        if (context.getTestInstance() == null) {
            return;
        }
        GenerateKeycloakClient methodAn = context.getTestMethod().getAnnotation(GenerateKeycloakClient.class);
        if (methodAn != null) {
            log.debug("[GENERATE KEYCLOAK TEST-CLIENT] After method level remove data for {}",
                    context.getTestMethod().getName());
            if (methodAn.deleteAfter()) {
                removeClient(methodAn.clientName());
                removeClientScopes(List.of(methodAn.scopes()));
            }
            return;
        }
        Class<?> clazz = context.getTestInstance().getClass();
        GenerateKeycloakClient an = clazz.getAnnotation(GenerateKeycloakClient.class);
        if (an == null) {
            return;
        }
        log.debug("[GENERATE KEYCLOAK TEST-CLIENT] After class level remove data for {}", clazz.getName());
        if (an.deleteAfter()) {
            removeClient(an.clientName());
            removeClientScopes(List.of(an.scopes()));
        }
    }
}
