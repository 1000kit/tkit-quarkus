package org.tkit.quarkus.it.metrics.test;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PingMetricsTest {
    @Test
    void pingMetrics() {
        given()
                .when()
                .get("/q/metrics")
                .then()
                .statusCode(200);
    }
}
