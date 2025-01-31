package org.tkit.quarkus.metrics.test;

import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * Metrics test configuration.
 */
@ConfigDocFilename("tkit-quarkus-metrics-test.adoc")
@ConfigMapping(prefix = "tkit.metrics-test")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface TestMetricsConfig {

    /**
     * This property is used to disable MetricsDynamicTest and must be configured in the maven-surefire-plugin.
     */
    @WithName("disabled")
    @WithDefault("false")
    boolean disabled();

    /**
     * This property is used to define a list of metric keys.
     * Key is prefix of the system property. Test will check if key exists with {@code startWith} method.
     */
    @WithName("keys")
    Map<String, List<String>> keys();

}
