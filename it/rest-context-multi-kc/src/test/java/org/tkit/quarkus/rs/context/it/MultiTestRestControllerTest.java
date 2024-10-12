package org.tkit.quarkus.rs.context.it;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(MultiTestRestController.class)
class MultiTestRestControllerTest extends AbstractTest {

    KeycloakTestClient keycloakClient = createClient();
    KeycloakTestClient keycloakClient1 = createClient1();
    KeycloakTestClient keycloakClient2 = createClient2();

    @Test
    void publicAccessTokenTest() {
        var kc0_token = keycloakClient.getAccessToken(ALICE);
        given().when()
                .header("apm-principal-token", kc0_token)
                .contentType(ContentType.JSON)
                .get("/public")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    void mixTokensTest() {
        var kc0_token = keycloakClient.getAccessToken(ALICE);
        var kc1_token = keycloakClient1.getAccessToken(ALICE);
        given().when()
                .auth().oauth2(kc1_token)
                .header("apm-principal-token", kc0_token)
                .contentType(ContentType.JSON)
                .get()
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    void issuerVerifyTest() {
        given().when()
                .contentType(ContentType.JSON)
                .get()
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

        // access and valid amp-principal-token
        var kc0_token = keycloakClient.getAccessToken(ALICE);
        given().when()
                .auth().oauth2(kc0_token)
                .header("apm-principal-token", kc0_token)
                .contentType(ContentType.JSON)
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        // access and valid amp-principal-token
        var kc1_token = keycloakClient1.getAccessToken(ALICE);
        given().when()
                .auth().oauth2(kc1_token)
                .header("apm-principal-token", kc1_token)
                .contentType(ContentType.JSON)
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        // access and NOT valid amp-principal-token
        var kc2_token = keycloakClient2.getAccessToken(ALICE);
        given().when()
                .auth().oauth2(kc2_token)
                .header("apm-principal-token", kc2_token)
                .contentType(ContentType.JSON)
                .get()
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

    }

}
