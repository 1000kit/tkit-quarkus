package org.tkit.quarkus.agroal.deployment.metrics;

import java.util.Map;

import org.tkit.quarkus.agroal.deployment.AgroalBuildTimeConfig;
import org.tkit.quarkus.agroal.runtime.metrics.ExtAgroalMetricsRecorder;

import io.quarkus.agroal.runtime.DataSourceJdbcBuildTimeConfig;
import io.quarkus.agroal.runtime.DataSourcesJdbcBuildTimeConfig;
import io.quarkus.datasource.runtime.DataSourceBuildTimeConfig;
import io.quarkus.datasource.runtime.DataSourcesBuildTimeConfig;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.metrics.MetricsFactoryConsumerBuildItem;

public class AgroalMetricsProcessor {

    private static final String FEATURE = "tkit-agroal-metrics";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void registerMetrics(ExtAgroalMetricsRecorder recorder,
            AgroalBuildTimeConfig config,
            DataSourcesJdbcBuildTimeConfig dataSourcesJdbcBuildTimeConfig,
            DataSourcesBuildTimeConfig dataSourcesBuildTimeConfig,
            BuildProducer<MetricsFactoryConsumerBuildItem> datasourceMetrics) {

        if (!config.metrics().enabled()) {
            return;
        }

        for (Map.Entry<String, DataSourceBuildTimeConfig> entry : dataSourcesBuildTimeConfig.dataSources().entrySet()) {
            DataSourceJdbcBuildTimeConfig jdbcBuildTimeConfig = dataSourcesJdbcBuildTimeConfig
                    .dataSources().get(entry.getKey()).jdbc();
            if (!jdbcBuildTimeConfig.enabled()) {
                continue;
            }
            if (dataSourcesBuildTimeConfig.metricsEnabled() && jdbcBuildTimeConfig.enableMetrics().orElse(true)) {
                datasourceMetrics.produce(new MetricsFactoryConsumerBuildItem(
                        recorder.registerDataSourceMetrics(entry.getKey())));
            }
        }
    }
}
