package org.tkit.quarkus.it.panache;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class UserRestControllerTest extends AbstractTest {

    @Test
    @WithDBData(value = { "data/test.xml" })
    public void importDataTest() throws InterruptedException {

        given()
                .pathParam("id", "GUID_3")
                .get("users/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @WithDBData(value = { "data/remote.xml" })
    public void importRemoteXmlDataTest() throws InterruptedException {
        given()
                .pathParam("id", "GUID_1")
                .get("users/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @WithDBData(value = { "data/test.xls" })
    public void importRemoteDataTest() throws InterruptedException {
        given()
                .pathParam("id", "ID_1")
                .get("users/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void createUserTest() {
        given()
                .pathParam("id", "1234")
                .get("users/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        User user = new User();
        user.username = "RestName";

        User tmp = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("users")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(User.class);

        User find = given()
                .contentType(ContentType.JSON)
                .pathParam("id", tmp.getId())
                .get("users/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(User.class);

        Assertions.assertNotNull(find);
        Assertions.assertEquals(tmp.getId(), find.getId());
    }
}
