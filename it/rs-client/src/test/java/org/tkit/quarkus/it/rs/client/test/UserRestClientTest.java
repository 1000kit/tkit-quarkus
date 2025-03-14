package org.tkit.quarkus.it.rs.client.test;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.it.rs.client.AbstractTest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class UserRestClientTest extends AbstractTest {

    @Test
    public void testUserRestClient() {

        given().contentType(ContentType.JSON)
                .get("users/ping")
                .then()
                .log().all()
                .statusCode(Response.Status.OK.getStatusCode());

        given().contentType(ContentType.JSON)
                .get("users/1")
                .then()
                .log().all()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void testCreateUserRestClient() {

        given().contentType(ContentType.JSON)
                .body("{\"id\":\"1\"}")
                .post("users/1")
                .then()
                .log().all()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void testGetAllUserRestClient() {

        given().contentType(ContentType.JSON)
                .get("users/")
                .then()
                .log().all()
                .statusCode(Response.Status.OK.getStatusCode());
    }
}
