package org.tkit.quarkus.jpa.test;

import java.util.List;

import io.restassured.http.ContentType;
import jakarta.inject.Inject;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;

@QuarkusTest
@DisplayName("Parent DAO tests")
public class ParentDAOTest extends AbstractTest {

  private static final Logger log = LoggerFactory.getLogger(ParentDAOTest.class);

  @Test
  public void searchParentTest() {
    Parent create = given()
            .get("parent")
            .then()
            .log().all()
            .statusCode(Response.Status.OK.getStatusCode())
            .extract().body().as(Parent.class);

    Parent find = given()
            .contentType(ContentType.JSON)
            .pathParam("id", create.getId())
            .get("parent/{id}")
            .then()
            .log().all()
            .statusCode(Response.Status.OK.getStatusCode())
            .extract().body().as(Parent.class);

    Assertions.assertNotNull(find);
    Assertions.assertEquals(create.getId(), find.getId());
  }

  @Test
  public void loadPageResult() {
    given()
            .get("parent")
            .then()
            .log().all()
            .statusCode(Response.Status.OK.getStatusCode())
            .extract().body().as(Parent.class);

    var find = given()
            .contentType(ContentType.JSON)
            .get("parent/page")
            .then()
            .log().all()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract().asString();

    Assertions.assertNotNull(find);
  }
}
