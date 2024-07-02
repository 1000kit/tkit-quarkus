package org.tkit.quarkus.security.test;

import static org.tkit.quarkus.security.test.SecurityTestUtils.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.keycloak.client.KeycloakTestClient;

/**
 * This junit5 extension is using the
 *
 * @see KeycloakTestClient
 *      to generate clients with scopes
 * @see GenerateKeycloakClient
 */
public class GenerateKeycloakClientExtension
        implements BeforeAllCallback, AfterAllCallback {

    private static final Logger log = LoggerFactory.getLogger(GenerateKeycloakClientExtension.class);

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        Class<?> clazz = context.getRequiredTestClass();
        GenerateKeycloakClient an = clazz.getAnnotation(GenerateKeycloakClient.class);
        if (an == null) {
            return;
        }
        log.debug("[GENERATE KEYCLOAK TEST-CLIENT] After class level remove data for {}", clazz.getName());
        if (an.deleteAfterAll()) {
            removeClient(an.clientName());
            removeClientScopes(List.of(an.scopes()));
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Object tmp = context.getStore(ExtensionContext.Namespace.GLOBAL).get(GenerateKeycloakClientExtension.class.getName());
        if (tmp == null) {

            context.getStore(ExtensionContext.Namespace.GLOBAL).put(GenerateKeycloakClientExtension.class.getName(),
                    Boolean.TRUE.toString());
            context.getStore(ExtensionContext.Namespace.GLOBAL).put(GenerateKeycloakClient.class.getName(),
                    Boolean.TRUE.toString());
        }

        Class<?> clazz = context.getRequiredTestClass();
        GenerateKeycloakClient an = clazz.getAnnotation(GenerateKeycloakClient.class);
        if (an == null) {
            return;
        }
        log.info("[GENERATE KEYCLOAK TEST-CLIENT] Init class level data for {} , client: {}", clazz.getSimpleName(),
                an.clientName());
        addClient(an.clientName(), Arrays.stream(an.scopes()).toList());
    }
}
