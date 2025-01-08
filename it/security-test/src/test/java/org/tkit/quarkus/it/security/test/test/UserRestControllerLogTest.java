package org.tkit.quarkus.it.security.test.test;

import static io.restassured.RestAssured.given;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.it.security.test.UserRestController;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@GenerateKeycloakClient(clientName = "log-client", scopes = { "ocx-user:write" })
@TestHTTPEndpoint(UserRestController.class)
public class UserRestControllerLogTest {

    @Test
    public void testUserRestClientUnauthorized() {
        given().contentType(ContentType.JSON)
                .get("/401")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testUserRestClientForbidden() {
        given().contentType(ContentType.JSON)
                .auth().oauth2(getKeycloakClientToken("log-client"))
                .get("/403")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

}
