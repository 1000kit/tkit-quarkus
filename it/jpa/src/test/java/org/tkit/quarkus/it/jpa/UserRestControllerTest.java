package org.tkit.quarkus.it.jpa;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(UserRestController.class)
public class UserRestControllerTest extends AbstractTest {

    @Test
    public void ping() {
        given()
                .get("ping")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void test() {
        given()
                .get("test")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void notfound() {
        given()
                .get("not-found")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @WithDBData(value = { "data/test.xml" })
    public void importDataTest() throws InterruptedException {
        given()
                .pathParam("id", "GUID_3")
                .get("{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @WithDBData(value = { "data/remote.xml" })
    public void importRemoteXmlDataTest() throws InterruptedException {
        given()
                .pathParam("id", "GUID_1")
                .get("{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @WithDBData(value = { "data/update.xml" })
    public void updateUserTest() {
        var dto = given()
                .get("U_GUID_3")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(UserDTO.class);

        var update = new UserDTO();
        update.username = dto.username;
        update.email = dto.email + "+update";
        update.modificationCount = dto.modificationCount;

        given()
                .body(update)
                .contentType(ContentType.JSON)
                .put(dto.id)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(UserDTO.class);

        dto = given()
                .get(dto.id)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(UserDTO.class);

        update.username = dto.username;
        update.email = dto.email + "+update";
        update.modificationCount = 1;

        var msg = given()
                .body(update)
                .contentType(ContentType.JSON)
                .put(dto.id)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract().asString();

        Assertions.assertNotNull(msg);
        Assertions.assertEquals(
                "Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [org.tkit.quarkus.it.jpa.User#U_GUID_3]",
                msg);
    }

    @Test
    public void createUserTest() {
        given()
                .pathParam("id", "1234")
                .get("{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        User user = new User();
        user.username = "RestName";

        User tmp = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(User.class);

        User find = given()
                .contentType(ContentType.JSON)
                .pathParam("id", tmp.getId())
                .get("{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(User.class);

        Assertions.assertNotNull(find);
        Assertions.assertEquals(tmp.getId(), find.getId());
    }
}
