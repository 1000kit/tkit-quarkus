package org.tkit.quarkus.it.rs.client.reactive.test;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.tkit.quarkus.it.rs.client.reactive.AbstractTest;
import org.tkit.quarkus.it.rs.client.reactive.InjectMockServerClient;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@QuarkusTest
public class UserRestClientTest extends AbstractTest {

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @Test
    public void testUserRestClient() {

        mockServerClient.when(request().withPath("/users/1").withMethod("GET"))
                .respond(response().withBody("{\"id\":\"1\"}")
                        .withHeader("Content-Type", "application/json"));

        given().header("Content-Type", "application/json")
                .get("users/1")
                .then()
                .log().all()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void testCreateUserRestClient() {

        mockServerClient.when(request().withPath("/users/2").withMethod("POST"))
                .respond(response().withBody("{\"id\":\"2\"}")
                        .withHeader("Content-Type", "application/json"));

        given().header("Content-Type", "application/json")
                .body("{\"id\":\"2\"}")
                .post("users/2")
                .then()
                .log().all()
                .statusCode(Response.Status.OK.getStatusCode());
    }
}
