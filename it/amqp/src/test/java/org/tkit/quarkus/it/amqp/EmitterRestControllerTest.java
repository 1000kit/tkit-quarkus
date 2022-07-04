package org.tkit.quarkus.it.amqp;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;

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
