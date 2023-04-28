package org.tkit.quarkus.jpa.test;

import static io.restassured.RestAssured.given;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class BusinessProjectRestControllerTest extends AbstractTest {

    @Test
    public void createProjectTest() {
        given()
                .pathParam("id", "1234")
                .get("business/project/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        BusinessProject p = new BusinessProject();
        p.setName("RestName");

        BusinessProject tmp = given()
                .contentType(ContentType.JSON)
                .body(p)
                .post("business/projects")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(BusinessProject.class);

        BusinessProject find = given()
                .contentType(ContentType.JSON)
                .pathParam("id", tmp.getId())
                .get("business/projects/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(BusinessProject.class);

        Assertions.assertNotNull(find);
        Assertions.assertEquals(tmp.getId(), find.getId());
        Assertions.assertEquals(1, find.getBid());
    }

    @Test
    public void pageProjectTest() {
        BusinessProject p = new BusinessProject();
        p.setName("RestName");

        given()
                .contentType(ContentType.JSON)
                .body(p)
                .post("business/projects")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(BusinessProject.class);

        given()
                .contentType(ContentType.JSON)
                .pathParam("index", 1)
                .pathParam("size", 10)
                .get("projects/page/{index}/{size}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

}
