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
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-user:read", "ocx-user:write" }, deleteAfter = false)
@TestHTTPEndpoint(UserRestController.class)
class UserRestControllerTest {

    KeycloakTestClient keycloakTestClient = new KeycloakTestClient();

    @Test
    public void testUserRestClient() {
        given().contentType(ContentType.JSON)
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .get("/1")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @GenerateKeycloakClient(clientName = "methodLevelClient", scopes = { "ocx-user:read", "ocx-user:write" })
    public void testCreateUserRestClient() {
        given().contentType(ContentType.JSON)
                .body("{\"id\":\"2\"}")
                .auth().oauth2(keycloakTestClient.getClientAccessToken("methodLevelClient"))
                .post("/2")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void testUserRestClient2() {
        given().contentType(ContentType.JSON)
                .auth().oauth2(keycloakTestClient.getClientAccessToken("testClient"))
                .get("/1")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }
}
