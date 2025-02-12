package org.tkit.quarkus.it.security.test.test;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.it.security.test.UserRestController;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;

@QuarkusTest
@GenerateKeycloakClient(clientName = "log-client", scopes = { "ocx-user:write" }, deleteAfter = false)
@TestHTTPEndpoint(UserRestController.class)
class UserRestControllerLogTest {

    KeycloakTestClient keycloakTestClient = new KeycloakTestClient();

    @Test
    public void testSecurityLogEvents() {

        given().contentType(ContentType.JSON)
                .get("/401")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        given().contentType(ContentType.JSON)
                .auth().oauth2(keycloakTestClient.getClientAccessToken("log-client"))
                .get("/403")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

    @Test
    public void testSecurityLogEvents2() {

        given().contentType(ContentType.JSON)
                .auth().oauth2(keycloakTestClient.getClientAccessToken("log-client") + "a")
                .get("/WRONG_TOKEN")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testSecurityNoneLogEvents() {

        given().contentType(ContentType.JSON)
                .get("none/401")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        given().contentType(ContentType.JSON)
                .auth().oauth2(keycloakTestClient.getClientAccessToken("log-client"))
                .get("none/403")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }
}
