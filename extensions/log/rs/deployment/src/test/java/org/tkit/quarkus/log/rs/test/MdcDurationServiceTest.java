package org.tkit.quarkus.log.rs.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.tkit.quarkus.log.rs.test.app.NoAnnotationRestController;

import io.quarkus.test.QuarkusUnitTest;

class MdcDurationServiceTest extends AbstractTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(NoAnnotationRestController.class)
                    .addAsResource("mdc-duration.properties", "application.properties"));

    @Test
    void mdcTest() {

        given().header("H1", "Value1").when()
                .get("/no-anno/test1")
                .then()
                .body(is("OK"));

        assertLogs().assertLines(1)
                .assertMatches(0,
                        ".+INFO  \\{rs-status=200, rs-time=.+\\} \\[org\\.tki\\.qua\\.log\\.rs\\.tes\\.app\\.NoAnnotationRestController\\] \\(executor-thread-1\\) GET \\/no-anno\\/test1 \\[200\\] \\[.+s\\]");
    }
}
