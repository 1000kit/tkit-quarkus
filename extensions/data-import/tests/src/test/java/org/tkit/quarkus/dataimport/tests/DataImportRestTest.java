package org.tkit.quarkus.dataimport.tests;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class DataImportRestTest extends AbstractTest {

    @Test
    public void testHealthCheck() {
        ParameterTestEntity param = given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .get("/test/xyz")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(ParameterTestEntity.class);
        Assertions.assertNotNull(param);
        Assertions.assertEquals("param_key", param.key);
        Assertions.assertEquals("param_value", param.value);
    }
}
