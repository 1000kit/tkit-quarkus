package org.tkit.quarkus.it.amqp;

import static io.restassured.RestAssured.given;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;

@QuarkusTest
public class EmitterRestControllerTest extends AbstractTest {

    @Test
    public void emitter() {
        ValidatableResponse response = given()
                .get("emitter")
                .then()
                .log().all()
                .statusCode(Response.Status.OK.getStatusCode());
    }

}
