package org.tkit.quarkus.log.rs.test;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.rs.test.app.AnnotationRestController;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;

public class AnnotationTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(AnnotationRestController.class)
                    .addAsResource("default.properties", "application.properties"));

    @Test
    public void test1Test() {
        RestAssured.get("/anno/test1").then()
                .body(is("OK"));
        assertLogs()
                .assertLines(1)
                .assertContains(0,
                        "INFO  [org.tki.qua.log.rs.tes.app.AnnotationRestController] (executor-thread-1) GET /anno/test1 [200] [");
    }

    @Test
    public void test2Test() {
        RestAssured.get("/anno/test2").then()
                .body(is("OK"));
        assertLogs().assertNoLogs();
    }

}
