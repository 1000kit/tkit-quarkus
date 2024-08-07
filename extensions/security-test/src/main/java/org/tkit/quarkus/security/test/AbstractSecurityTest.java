package org.tkit.quarkus.security.test;

import static io.restassured.RestAssured.given;
import static org.tkit.quarkus.security.test.SecurityTestUtils.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSecurityTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractSecurityTest.class);

    public abstract SecurityTestConfig getConfig();

    public void default_security_test(String client, List<String> scopes, Integer expectation, String url, String method) {

        addClient(client, scopes);
        var givenClient = given()
                .contentType("application/json")
                .auth().oauth2(getKeycloakClientToken(client))
                .when();

        var defaultClient = given()
                .contentType("application/json")
                .auth().oauth2(getKeycloakClientToken("quarkus-app"))
                .when();

        if (method.equalsIgnoreCase("get")) {
            givenClient
                    .get(url)
                    .then().statusCode(expectation);

            //client with missing scope => forbidden
            defaultClient
                    .get(url)
                    .then().statusCode(403);

        } else if (method.equalsIgnoreCase("post")) {
            givenClient
                    .post(url)
                    .then().statusCode(expectation);

            //client with missing scope => forbidden
            defaultClient
                    .post(url)
                    .then().statusCode(403);

        } else if (method.equalsIgnoreCase("delete")) {
            givenClient
                    .delete(url)
                    .then().statusCode(expectation);

            //client with missing scope => forbidden
            defaultClient
                    .delete(url)
                    .then().statusCode(403);

        } else if (method.equalsIgnoreCase("put")) {
            givenClient
                    .put(url)
                    .then().statusCode(expectation);

            //client with missing scope => forbidden
            defaultClient
                    .put(url)
                    .then().statusCode(403);
        }
        removeClient(client);
        removeClientScopes(scopes);
    }

    @Test
    public void test_initializer() {
        if (Boolean.getBoolean(System.getProperty("tkit.security-test.disabled"))) {
            log.info("[SECURITY-TEST] Security tests disabled");
            return;
        }
        getConfig().options.keySet().forEach(key -> {
            log.info("[SECURITY-TEST] Start security test for key: {}", key);
            default_security_test(key + "Client",
                    getConfig().options.get(key).scopes,
                    getConfig().options.get(key).expectation,
                    getConfig().options.get(key).url,
                    getConfig().options.get(key).method);
        });
    }
}
