package org.tkit.quarkus.metrics.test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.config.SmallRyeConfig;

@QuarkusTest
@DisabledIfSystemProperty(named = "tkit.metrics-test.metric.disabled", matches = "true")
public class MetricsDynamicTest {

    private static final Logger log = LoggerFactory.getLogger(MetricsDynamicTest.class);
    private static final String CONFIG_PREFIX = "tkit.metrics-test.keys";

    @TestFactory
    Stream<DynamicTest> testMetric() {

        // Parse metrics
        var metricNames = loadMetrics().lines()
                .filter(s -> !s.startsWith("#"))
                .map(s -> s.split("\\{")[0])
                .map(s -> s.split(" ")[0])
                .collect(Collectors.toSet());

        log.debug("MetricNames: {}", metricNames);
        return getMetricKeys().stream().map(s -> DynamicTest.dynamicTest("Metrics test " + s, () -> {
            log.debug("Test metric: {}", s);
            assertTrue(metricNames.contains(s), "Missing metric '" + s + "'");
        }));

    }

    public String loadMetrics() {
        return given()
                .when()
                .get("/q/metrics")
                .then()
                .statusCode(200)
                .extract().asString();
    }

    public Set<String> getMetricKeys() {
        SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
        var keys = config.getMapKeys(CONFIG_PREFIX)
                .keySet();

        if (log.isDebugEnabled()) {
            log.debug("Metric keys: {}", keys);
            log.debug("Property names: {}", config.getPropertyNames());
        }

        Set<String> metricKeysToTest = new HashSet<>();
        for (String entry : config.getPropertyNames()) {
            for (String key : keys) {
                if (entry.startsWith(key)) {
                    metricKeysToTest.addAll(config.getValues(CONFIG_PREFIX + "." + key, String.class));
                }
            }
        }
        return metricKeysToTest;
    }
}
