package org.tkit.quarkus.rs.context.it;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(Test2RestController.class)
@TestProfile(NoHeaderPropagationTestProfile.class)
class NoHeaderPropagationTest {

    @Test
    void testNoHeaderPropagation() {
        given().when()
                .header("test-header-1", "test-value-1")
                .header("test-header-2", "test-value-2")
                .contentType(ContentType.JSON)
                .get("1")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .header("test-header-2", Matchers.nullValue())
                .header("test-header-1", Matchers.nullValue());
    }
}
