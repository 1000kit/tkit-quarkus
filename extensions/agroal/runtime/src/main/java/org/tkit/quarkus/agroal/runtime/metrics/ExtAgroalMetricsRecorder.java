package org.tkit.quarkus.agroal.runtime.metrics;

import java.util.function.Consumer;

import org.jboss.logging.Logger;

import io.quarkus.agroal.runtime.AgroalDataSourceUtil;
import io.quarkus.datasource.common.runtime.DataSourceUtil;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.runtime.metrics.MetricsFactory;

@Recorder
public class ExtAgroalMetricsRecorder {

    private static final Logger log = Logger.getLogger(ExtAgroalMetricsRecorder.class);

    /* RUNTIME_INIT */
    public Consumer<MetricsFactory> registerDataSourceMetrics(String dataSourceName) {
        return new Consumer<MetricsFactory>() {
            @Override
            public void accept(MetricsFactory metricsFactory) {

                if (!AgroalDataSourceUtil.activeDataSourceNames().contains(dataSourceName)) {
                    log.debug("Not registering metrics for datasource '" + dataSourceName + "'"
                            + " as the datasource has been deactivated in the configuration");
                    return;
                }

                var ds = AgroalDataSourceUtil.dataSourceIfActive(dataSourceName);
                if (ds.isEmpty()) {
                    log.warn("Data-source '" + dataSourceName + "' is not active for metrics");
                    return;
                }

                String tagValue = DataSourceUtil.isDefault(dataSourceName) ? "default" : dataSourceName;
                var config = ds.get().getConfiguration().connectionPoolConfiguration();

                metricsFactory.builder("agroal.config.pool.min.size")
                        .description("The minimum number of connections on the pool.")
                        .tag("datasource", tagValue)
                        .buildCounter(config::minSize);

                metricsFactory.builder("agroal.config.pool.max.size")
                        .description("The maximum number of connections on the pool.")
                        .tag("datasource", tagValue)
                        .buildCounter(config::maxSize);
            }
        };
    }
}
