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

    void default_security_test(String client, List<String> scopes, Integer expectation, String url, String method) {

        addClient(client, scopes);

        if (method.equalsIgnoreCase("get")) {
            given()
                    .contentType("application/json")
                    .auth().oauth2(getKeycloakClientToken(client))
                    .when()
                    .get(url)
                    .then().statusCode(expectation);

            //client with missing scope => forbidden
            given()
                    .contentType("application/json")
                    .auth().oauth2(getKeycloakClientToken("quarkus-app"))
                    .when()
                    .get(url)
                    .then().statusCode(403);

        } else if (method.equalsIgnoreCase("post")) {
            given()
                    .contentType("application/json")
                    .auth().oauth2(getKeycloakClientToken(client))
                    .when()
                    .post(url)
                    .then().statusCode(expectation);

            //client with missing scope => forbidden
            given()
                    .contentType("application/json")
                    .auth().oauth2(getKeycloakClientToken("quarkus-app"))
                    .when()
                    .post(url)
                    .then().statusCode(403);

        } else if (method.equalsIgnoreCase("delete")) {
            given()
                    .contentType("application/json")
                    .auth().oauth2(getKeycloakClientToken(client))
                    .when()
                    .delete(url)
                    .then().statusCode(expectation);

            //client with missing scope => forbidden
            given()
                    .contentType("application/json")
                    .auth().oauth2(getKeycloakClientToken("quarkus-app"))
                    .when()
                    .delete(url)
                    .then().statusCode(403);

        } else if (method.equalsIgnoreCase("put")) {
            given()
                    .contentType("application/json")
                    .auth().oauth2(getKeycloakClientToken(client))
                    .when()
                    .put(url)
                    .then().statusCode(expectation);

            //client with missing scope => forbidden
            given()
                    .contentType("application/json")
                    .auth().oauth2(getKeycloakClientToken("quarkus-app"))
                    .when()
                    .put(url)
                    .then().statusCode(403);
        }
        removeClient(client);
        removeClientScopes(scopes);
    }

    @Test
    public void test_initializer() {
        System.out.println();
        log.info("OPTIONS: {}", getConfig().toString());

        getConfig().options.keySet().forEach(key -> {
            log.info("Start security test for key: {}", key);
            default_security_test(key + "Client",
                    getConfig().options.get(key).scopes,
                    getConfig().options.get(key).expectation,
                    getConfig().options.get(key).url,
                    getConfig().options.get(key).method);
        });
    }

}
