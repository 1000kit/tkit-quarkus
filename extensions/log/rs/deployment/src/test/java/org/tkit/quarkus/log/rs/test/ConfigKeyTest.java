package org.tkit.quarkus.log.rs.test;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.rs.test.app.ConfigKeyRestController;
import org.tkit.quarkus.log.rs.test.app.NoAnnotationRestController;

import static org.hamcrest.Matchers.is;

public class ConfigKeyTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(ConfigKeyRestController.class)
                    .addAsResource("configkey.properties", "application.properties"));


    @Test
    public void test1Test() {
        RestAssured.get("/configkey/test1").then()
                .body(is("OK"));
        assertLogs().assertNoLogs();
    }

    @Test
    public void test2Test() {
        RestAssured.get("/configkey/test2").then()
                .body(is("OK"));
        assertLogs().assertLines(1)
                .assertContains(0, "INFO  [org.tki.qua.log.rs.tes.app.ConfigKeyRestController] (executor-thread-0) GET /configkey/test2 [200] [");
    }

    @Test
    public void test3Test() {
        RestAssured.get("/configkey/test3").then()
                .body(is("OK"));
        assertLogs().assertLines(1)
                .assertContains(0, "INFO  [org.tki.qua.log.rs.tes.app.ConfigKeyRestController] (executor-thread-0) GET /configkey/test3 [200] [");
    }
}
