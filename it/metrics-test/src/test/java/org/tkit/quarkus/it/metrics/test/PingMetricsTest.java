package org.tkit.quarkus.it.metrics.test;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PingMetricsTest {
    @Test
    void pingMetrics() {
        var response = given()
                .when()
                .get("/q/metrics")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().asString();

        // check extended agroal metrics
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.contains("agroal_config_pool_max_size_total{datasource=\"default\"}"));
        Assertions.assertTrue(response.contains("agroal_config_pool_min_size_total{datasource=\"default\"}"));
    }
}
