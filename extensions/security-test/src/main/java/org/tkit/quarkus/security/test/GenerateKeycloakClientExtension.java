package org.tkit.quarkus.security.test;

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
        implements QuarkusTestBeforeClassCallback, QuarkusTestAfterAllCallback {

    private static final Logger log = LoggerFactory.getLogger(GenerateKeycloakClientExtension.class);

    private static KeycloakTestAdminClient CLIENT;

    private static Class<?> TEST_CLASS;

    private static GenerateKeycloakClient TEST_CLASS_ANNOTATION;

    KeycloakTestAdminClient getClient() {
        if (CLIENT == null) {
            CLIENT = new KeycloakTestAdminClient();
        }
        return CLIENT;
    }

    @Override
    public void beforeClass(Class<?> beforeClass) {

        TEST_CLASS = null;
        TEST_CLASS_ANNOTATION = null;

        GenerateKeycloakClient an = beforeClass.getAnnotation(GenerateKeycloakClient.class);
        if (an == null) {
            return;
        }

        TEST_CLASS = beforeClass;
        TEST_CLASS_ANNOTATION = an;
        CLIENT = new KeycloakTestAdminClient();

        log.info("[SECURITY-TEST] Init class level for {} create client: '{}' and scopes: {}",
                TEST_CLASS.getSimpleName(), TEST_CLASS_ANNOTATION.clientName(), TEST_CLASS_ANNOTATION.scopes());
        getClient().addClient(an.clientName(), Arrays.stream(an.scopes()).toList());
    }

    @Override
    public void afterAll(QuarkusTestContext context) {
        if (TEST_CLASS == null || TEST_CLASS_ANNOTATION == null) {
            return;
        }
        if (TEST_CLASS_ANNOTATION.deleteAfter()) {
            log.info("[SECURITY-TEST] After class level for {} delete client: '{}' and scopes: {}",
                    TEST_CLASS.getSimpleName(),
                    TEST_CLASS_ANNOTATION.clientName(), TEST_CLASS_ANNOTATION.scopes());
            getClient().removeClient(TEST_CLASS_ANNOTATION.clientName());
            getClient().removeClientScopes(List.of(TEST_CLASS_ANNOTATION.scopes()));
        }
    }

}
