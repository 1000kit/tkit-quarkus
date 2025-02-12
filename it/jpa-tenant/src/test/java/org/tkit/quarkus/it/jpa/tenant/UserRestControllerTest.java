package org.tkit.quarkus.it.jpa.tenant;

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
@WithDBData(value = { "data/test.xml" }, deleteBeforeInsert = true, rinseAndRepeat = true)
public class UserRestControllerTest extends AbstractTest {

    @Test
    public void all1() {
        var resp = given()
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(UserListDTO.class);

        Assertions.assertNotNull(resp);
        Assertions.assertNotNull(resp.items);
        Assertions.assertEquals(6, resp.items.size());
    }

    @Test
    public void all2() {
        var resp = given()
                .header(APM_HEADER_PARAM, createToken("org1"))
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(UserListDTO.class);

        Assertions.assertNotNull(resp);
        Assertions.assertNotNull(resp.items);
        Assertions.assertEquals(2, resp.items.size());
    }

    @Test
    public void all3() {
        var resp = given()
                .header(APM_HEADER_PARAM, createToken("org0"))
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(UserListDTO.class);

        Assertions.assertNotNull(resp);
        Assertions.assertNotNull(resp.items);
        Assertions.assertEquals(8, resp.items.size());
    }

    @Test
    public void notFound() {
        given()
                .get("not-found")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void importDataTest() throws InterruptedException {
        given()
                .pathParam("id", "GUID_3")
                .get("{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void importRemoteXmlDataTest() throws InterruptedException {
        given()
                .pathParam("id", "GUID_1")
                .get("{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
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
                "Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [org.tkit.quarkus.it.jpa.tenant.User#U_GUID_3]",
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

    @Test
    public void createUserTest2() {
        User user = new User();
        user.username = "RestName";

        User tmp = given()
                .contentType(ContentType.JSON)
                .header(APM_HEADER_PARAM, createToken("org0"))
                .body(user)
                .post()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(User.class);

        var resp = given()
                .header(APM_HEADER_PARAM, createToken("org0"))
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(UserListDTO.class);

        Assertions.assertNotNull(resp);
        Assertions.assertNotNull(resp.items);
        Assertions.assertEquals(9, resp.items.size());

        User find = given()
                .contentType(ContentType.JSON)
                .header(APM_HEADER_PARAM, createToken("org0"))
                .pathParam("id", tmp.getId())
                .get("{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(User.class);

        Assertions.assertNotNull(find);
        Assertions.assertEquals(tmp.getId(), find.getId());
    }

    @Test
    public void deleteAllTest() {

        var resp = given()
                .header(APM_HEADER_PARAM, createToken("org0"))
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(UserListDTO.class);

        Assertions.assertNotNull(resp);
        Assertions.assertNotNull(resp.items);
        Assertions.assertEquals(8, resp.items.size());

        given()
                .header(APM_HEADER_PARAM, createToken("org0"))
                .delete()
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        resp = given()
                .header(APM_HEADER_PARAM, createToken("org0"))
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(UserListDTO.class);

        Assertions.assertNotNull(resp);
        Assertions.assertNotNull(resp.items);
        Assertions.assertEquals(0, resp.items.size());
    }

    @Test
    public void createUserCustomTest() {
        String tenantId = "999";

        UserDTO user = new UserDTO();
        user.username = "RestName";

        UserDTO tmp = given()
                .contentType(ContentType.JSON)
                .body(user)
                .post("custom/" + tenantId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(UserDTO.class);

        UserDTO find = given()
                .contentType(ContentType.JSON)
                .header(APM_HEADER_PARAM, createToken("org0"))
                .pathParam("id", tmp.id)
                .get("{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(UserDTO.class);

        Assertions.assertNotNull(find);
        Assertions.assertEquals(tmp.id, find.id);
        Assertions.assertEquals(tenantId, tmp.tenantId);
    }
}
