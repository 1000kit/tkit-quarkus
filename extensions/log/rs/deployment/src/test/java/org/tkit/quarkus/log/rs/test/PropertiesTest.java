package org.tkit.quarkus.log.rs.test;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.rs.test.app.PropertiesRestController;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;

public class PropertiesTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(PropertiesRestController.class)
                    .addAsResource("properties.properties", "application.properties"));

    @Test
    public void test1Test() {
        RestAssured.get("/properties/test1").then()
                .body(is("OK"));
        assertLogs().assertNoLogs();
    }

    @Test
    public void test2Test() {
        RestAssured.get("/properties/test2").then()
                .body(is("OK"));
        assertLogs().assertLines(1)
                .assertContains(0,
                        "INFO  [org.tki.qua.log.rs.tes.app.PropertiesRestController] (executor-thread-1) GET /properties/test2 [200] [");
    }

}
