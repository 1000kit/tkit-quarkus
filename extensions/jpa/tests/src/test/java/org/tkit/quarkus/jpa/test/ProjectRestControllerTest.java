package org.tkit.quarkus.jpa.test;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class ProjectRestControllerTest extends AbstractTest {

    @Test
    public void createProjectTest() {
        given()
                .pathParam("id", "1234")
                .get("project/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        Project p = new Project();
        p.setName("RestName");

        Project tmp = given()
                .contentType(ContentType.JSON)
                .body(p)
                .post("projects")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(Project.class);

        Project find = given()
                .contentType(ContentType.JSON)
                .pathParam("id", tmp.getKey())
                .get("projects/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(Project.class);

        Assertions.assertNotNull(find);
        Assertions.assertEquals(tmp.getKey(), find.getKey());
    }

    @Test
    public void pageProjectTest() {
        Project p = new Project();
        p.setName("RestName");

        given()
                .contentType(ContentType.JSON)
                .body(p)
                .post("projects")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(Project.class);

        given()
                .contentType(ContentType.JSON)
                .pathParam("index", 1)
                .pathParam("size", 10)
                .get("projects/page/{index}/{size}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

}
