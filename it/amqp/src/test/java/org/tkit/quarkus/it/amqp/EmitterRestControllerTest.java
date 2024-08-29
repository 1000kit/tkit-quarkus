package org.tkit.quarkus.it.amqp;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class EmitterRestControllerTest extends AbstractTest {

    @Test
    void emitter() {
        given()
                .get("emitter")
                .then()
                .log().all()
                .statusCode(Response.Status.OK.getStatusCode());
    }

}
