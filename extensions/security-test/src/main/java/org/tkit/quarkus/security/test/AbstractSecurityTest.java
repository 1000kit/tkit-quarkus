package org.tkit.quarkus.security.test;

import static io.restassured.RestAssured.given;
import static org.tkit.quarkus.security.test.SecurityTestUtils.*;

import java.util.List;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.config.SmallRyeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class AbstractSecurityTest {
    private static final Logger log = LoggerFactory.getLogger(AbstractSecurityTest.class);

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
        SecurityTestConfig testConfig = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class)
                .getConfigMapping(SecurityTestConfig.class);
        if (!testConfig.options().isEmpty() && testConfig.enabled()) {
            testConfig.options().keySet().forEach(key -> {
                if (testConfig.options().get(key).enabled()) {
                    log.info("Start security test for key: {}", key );
                    default_security_test(key + "Client",
                            testConfig.options().get(key).scopes(),
                            testConfig.options().get(key).expectation(),
                            testConfig.options().get(key).url(),
                            testConfig.options().get(key).method());
                }
            });
        }
    }

}
