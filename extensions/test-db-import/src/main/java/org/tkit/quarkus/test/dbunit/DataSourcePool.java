package org.tkit.quarkus.test.dbunit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;

public class DataSourcePool {

    private static final Logger log = LoggerFactory.getLogger(DataSourcePool.class);

    private static final ConcurrentHashMap<String, DataSource> CACHE = new ConcurrentHashMap<>();

    private DataSourcePool() {
    }

    public static Connection getConnection(String name) throws SQLException {
        return CACHE.computeIfAbsent(name, DataSourcePool::create).getConnection();
    }

    private static DataSource create(String name) {
        try {
            String prefix = ConfigurationConst.DATASOURCE_PREFIX;
            if (name != null && !name.equals(ConfigurationConst.DEFAULT_DATASOURCE_VALUE)) {
                prefix = prefix + name + ".";
            }
            Config config = ConfigProvider.getConfig();
            String username = config.getValue(prefix + ConfigurationConst.USERNAME, String.class);
            String password = config.getValue(prefix + ConfigurationConst.PASSWORD, String.class);
            String url = config.getValue(prefix + ConfigurationConst.JDBC_URL, String.class);

            Integer jdbcMaxSize = config.getOptionalValue(prefix + "jdbc.max-size", Integer.class).orElse(20);
            Integer jdbcMinSize = config.getOptionalValue(prefix + "jdbc.min-size", Integer.class).orElse(2);
            Integer jdbcInitialSize = config.getOptionalValue(prefix + "jdbc.initial-size", Integer.class).orElse(2);
            Integer acquisitionTimeoutSeconds = config
                    .getOptionalValue(prefix + "jdbc.acquisition-timeout", Integer.class)
                    .orElse(10);

            log.info("[DB-IMPORT] Creating pooled datasource for {} with url: {} (pool size: {}-{})", name, url,
                    jdbcMinSize,
                    jdbcMaxSize);

            AgroalDataSourceConfigurationSupplier configSupplier = new AgroalDataSourceConfigurationSupplier()
                    .connectionPoolConfiguration(cp -> cp
                            .maxSize(jdbcMaxSize)
                            .minSize(jdbcMinSize)
                            .initialSize(jdbcInitialSize)
                            .acquisitionTimeout(java.time.Duration.ofSeconds(acquisitionTimeoutSeconds))
                            .connectionFactoryConfiguration(cf -> cf
                                    .jdbcUrl(url)
                                    .principal(new NamePrincipal(username))
                                    .credential(new SimplePassword(password))
                                    .autoCommit(true)));

            return AgroalDataSource.from(configSupplier);
        } catch (Exception ex) {
            throw new RuntimeException("Error creating datasource for: " + name, ex);
        }
    }
}
