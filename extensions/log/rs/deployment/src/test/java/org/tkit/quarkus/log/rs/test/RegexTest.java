package org.tkit.quarkus.log.rs.test;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.rs.test.app.RegexRestController;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;

public class RegexTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(RegexRestController.class)
                    .addAsResource("regex.properties", "application.properties"));

    @Test
    public void loadTest() {
        RestAssured.get("/regex/load").then()
                .body(is("OK"));
        assertLogs().assertLines(1)
                .assertContains(0,
                        "NFO  [org.tki.qua.log.rs.tes.app.RegexRestController] (executor-thread-0) GET /regex/load [200] [");
    }

    @Test
    public void createTest() {
        RestAssured.given().body("OK")
                .header("Content-Type", "application/json")
                .post("/regex/create").then()
                .body(is("OK"));
        assertLogs().assertNoLogs();
    }

}
