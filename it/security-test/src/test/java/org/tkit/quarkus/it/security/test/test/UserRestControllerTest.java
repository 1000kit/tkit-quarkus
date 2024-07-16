package org.tkit.quarkus.it.security.test.test;

import static io.restassured.RestAssured.given;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.it.security.test.UserRestController;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-user:read", "ocx-user:write" })
@TestHTTPEndpoint(UserRestController.class)
public class UserRestControllerTest {

    @Test
    public void testUserRestClient() {
        given().header("Content-Type", "application/json")
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .get("/1")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @GenerateKeycloakClient(clientName = "methodLevelClient", scopes = { "ocx-user:read", "ocx-user:write" })
    public void testCreateUserRestClient() {
        given().header("Content-Type", "application/json")
                .body("{\"id\":\"2\"}")
                .auth().oauth2(getKeycloakClientToken("methodLevelClient"))
                .post("/2")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void testUserRestClient2() {
        given().header("Content-Type", "application/json")
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .get("/1")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }
}
