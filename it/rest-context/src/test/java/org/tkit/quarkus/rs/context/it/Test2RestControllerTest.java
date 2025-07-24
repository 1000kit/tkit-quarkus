package org.tkit.quarkus.rs.context.it;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;

import org.hamcrest.Matchers;
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

    @Test
    void testResponseHeaderPropagation() {
        given().when()
                .header("test-header-forward-1", "test-value-1")
                .header("test-header-no-forward", "test-value-3")
                .header("test-header-forward-2", "test-value-2")
                .contentType(ContentType.JSON)
                .get("1")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .header("test-header-forward-1", "test-value-1")
                .header("test-header-forward-2", "test-value-2")
                .header("test-header-forward-3", Matchers.nullValue())
                .header("test-header-no-forward", Matchers.nullValue());
    }
}
