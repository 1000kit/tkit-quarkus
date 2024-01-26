package org.tkit.quarkus.rs.context.it;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(Test2RestController.class)
class Test2RestControllerTest {

    @Test
    void testTenantAnnotation() {
        given().when()
                .contentType(ContentType.JSON)
                .get("1")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        given().when()
                .contentType(ContentType.JSON)
                .get("2")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

    }
}
