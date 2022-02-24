package org.tkit.quarkus.it.rs.client.test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.tkit.quarkus.it.rs.client.AbstractTest;
import org.tkit.quarkus.it.rs.client.InjectMockServerClient;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@QuarkusIntegrationTest
public class UserRestClientIT extends UserRestClientTest {

}
