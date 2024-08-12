package org.tkit.quarkus.test.callbacks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.test.TestConfig;
import org.tkit.quarkus.test.TestConfigUtil;

import io.quarkus.test.junit.callback.QuarkusTestBeforeTestExecutionCallback;
import io.quarkus.test.junit.callback.QuarkusTestMethodContext;
import io.restassured.RestAssured;

public class RestAssuredUriTestExecutionCallback implements QuarkusTestBeforeTestExecutionCallback {

    private static final Logger log = LoggerFactory.getLogger(RestAssuredUriTestExecutionCallback.class);

    @Override
    public void beforeTestExecution(QuarkusTestMethodContext context) {

        TestConfig testConfig = TestConfigUtil.config();

        if (!testConfig.restAssuredConfig().enabled()) {
            log.trace("Rest-Assured configuration is disabled");
            return;
        }

        if (!testConfig.restAssuredConfig().uriOverwrite().enabled()) {
            log.trace("Rest-Assured URI overwrite is disabled");
            return;
        }

        if (!testConfig.integrationTestEnabled()) {
            log.trace("Integration tests are not enabled");
            return;
        }

        if (testConfig.ciConfig().enabled()) {
            log.trace("CI pipeline is enabled");
            if (!Boolean.parseBoolean(System.getenv(testConfig.ciConfig().envName()))) {
                log.trace("No CI pipeline environment variable found. Env: {}", testConfig.ciConfig().envName());
                return;
            }
        }

        var uri = testConfig.restAssuredConfig().uriOverwrite().uri();
        log.debug("Overwriting base URI of RestAssured from '{}' to '{}'", RestAssured.baseURI, uri);
        RestAssured.baseURI = uri;
    }
}
