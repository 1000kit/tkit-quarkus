package org.tkit.quarkus.security.test;

import static org.tkit.quarkus.security.test.SecurityTestUtils.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
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
        QuarkusTestAfterEachCallback, QuarkusTestBeforeClassCallback, AfterAllCallback {

    private static final Logger log = LoggerFactory.getLogger(GenerateKeycloakClientExtension.class);

    @Override
    public void beforeEach(QuarkusTestMethodContext context) {
        GenerateKeycloakClient methodAn = context.getTestMethod().getAnnotation(GenerateKeycloakClient.class);
        if (methodAn != null) {
            log.info("[SECURITY-TEST] Init method level for {} create client: '{}' and scopes: {}",
                    context.getTestMethod().getName(),
                    methodAn.clientName(), methodAn.scopes());

            addClient(methodAn.clientName(),
                    Arrays.stream(methodAn.scopes()).toList());
        }
    }

    @Override
    public void afterEach(QuarkusTestMethodContext context) {
        if (context.getTestInstance() == null) {
            return;
        }
        GenerateKeycloakClient methodAn = context.getTestMethod().getAnnotation(GenerateKeycloakClient.class);
        if (methodAn != null) {
            if (methodAn.deleteAfter()) {
                log.info("[SECURITY-TEST] After method level for {} delete client: '{}' and scopes: {}",
                        context.getTestMethod().getName(), methodAn.clientName(), methodAn.scopes());
                removeClient(methodAn.clientName());
                removeClientScopes(List.of(methodAn.scopes()));
            }
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        Class<?> clazz = context.getRequiredTestClass();
        GenerateKeycloakClient an = clazz.getAnnotation(GenerateKeycloakClient.class);
        if (an == null) {
            return;
        }
        log.info("[SECURITY-TEST] After class level for {} delete client: '{}' and scopes: {}", clazz.getName(),
                an.clientName(), an.scopes());
        if (an.deleteAfter()) {
            removeClient(an.clientName());
            removeClientScopes(List.of(an.scopes()));
        }
    }

    @Override
    public void beforeClass(Class<?> testClass) {
        GenerateKeycloakClient an = testClass.getAnnotation(GenerateKeycloakClient.class);
        if (an == null) {
            return;
        }
        log.info("[SECURITY-TEST] Init class level for {} create client: '{}' and scopes: {}",
                testClass.getSimpleName(), an.clientName(), an.scopes());
        addClient(an.clientName(), Arrays.stream(an.scopes()).toList());
    }
}
