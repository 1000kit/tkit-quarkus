package org.tkit.quarkus.oidc.health.it;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

@QuarkusTest
@TestProfile(FailedQuarkusTestProfile.class)
class OidcHealthCheckFailedTest extends AbstractTest {

    @Test
    void testHealthCheck() {
        var response = given().when()
                .contentType(ContentType.JSON)
                .get("/q/health")
                .then()
                .log().all()
                .statusCode(Response.Status.SERVICE_UNAVAILABLE.getStatusCode())
                .extract().as(new TypeRef<Map<String, Object>>() {
                });

        Assertions.assertNotNull(response);
        Assertions.assertEquals("DOWN", response.get("status"));
        List<?> checks = (List<?>) response.get("checks");

        Assertions.assertNotNull(checks);
        Assertions.assertEquals(1, checks.size());
        Map<?, ?> check = (Map<?, ?>) checks.get(0);

        Assertions.assertEquals("OIDC tenant meta-data health check", check.get("name"));
        Assertions.assertEquals("DOWN", check.get("status"));

        Map<?, ?> data = (Map<?, ?>) check.get("data");

        Assertions.assertEquals("kc1", data.get("tenant"));
        Assertions.assertEquals("missing", data.get("status"));
    }

}
