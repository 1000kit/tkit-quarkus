package org.tkit.quarkus.log.rs.test;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.rs.test.app.NoAnnotationRestController;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;

public class NoAnnotationTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(NoAnnotationRestController.class)
                    .addAsResource("default.properties", "application.properties"));

    @Test
    public void test1Test() {
        RestAssured.get("/no-anno/test1").then()
                .body(is("OK"));
        assertLogs().assertLines(1)
                .assertContains(0,
                        "INFO  [org.tki.qua.log.rs.tes.app.NoAnnotationRestController] (executor-thread-0) GET /no-anno/test1 [200] [");
    }

}
