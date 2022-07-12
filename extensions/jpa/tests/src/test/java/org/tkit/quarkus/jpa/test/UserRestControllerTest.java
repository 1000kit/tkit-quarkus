package org.tkit.quarkus.jpa.test;

import static io.restassured.RestAssured.given;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class UserRestControllerTest extends AbstractTest {

    @Test
    public void createUserTest() {
        given()
                .pathParam("id", "1234")
                .get("users/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        User user = new User();
        user.setEmail("Rest@Rest.Rest");
        user.setName("RestName");

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

    @Test
    public void pageTest() {
        User user = new User();
        user.setEmail("email");
        user.setName("PageRestName");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("users")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(User.class);

        given()
                .contentType(ContentType.JSON)
                .pathParam("index", 1)
                .pathParam("size", 10)
                .get("users/page/{index}/{size}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void pageHeaderTest() {
        User user = new User();
        user.setEmail("email");
        user.setName("PageRestName");

        User tmp = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("users")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(User.class);

        given()
                .contentType(ContentType.JSON)
                .pathParam("index", 1)
                .pathParam("size", 10)
                .get("users/pageHeader/{index}/{size}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void pageSearchTest() {
        User user = new User();
        user.setEmail("email100");
        user.setName("PageRestName100");

        User tmp = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("users")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(User.class);

        given()
                .contentType(ContentType.JSON)
                .queryParam("name", "email")
                .queryParam("email", "PageRest")
                .queryParam("index", 1)
                .queryParam("size", 10)
                .get("users/search")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }
}
