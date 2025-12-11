package org.tkit.quarkus.test.dbunit;

import java.sql.Connection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;

public class LocalDatabase implements Database {

    private static final Logger log = LoggerFactory.getLogger(LocalDatabase.class);

    private static final ConcurrentHashMap<String, DataSource> dataSourceCache = new ConcurrentHashMap<>();

    @Override
    public void deleteData(Request request) throws Exception {
        IDataSet dataSet = getDataSet(request);
        IDatabaseConnection conn = null;
        try {
            conn = getConnection(request.ano().datasource());
            DatabaseOperation.DELETE_ALL.execute(conn, dataSet);
        } finally {
            if (conn != null)
                conn.close();
        }
    }

    @Override
    public void insertData(Request request) throws Exception {
        IDataSet dataSet = getDataSet(request);
        DatabaseOperation op = DatabaseOperation.INSERT;
        if (request.ano().deleteBeforeInsert()) {
            op = DatabaseOperation.CLEAN_INSERT;
        }
        IDatabaseConnection conn = null;
        try {
            conn = getConnection(request.ano().datasource());
            op.execute(conn, dataSet);
        } finally {
            if (conn != null)
                conn.close();
        }
    }

    protected IDatabaseConnection getConnection(String dataSourceName) {
        try {
            String dsKey = dataSourceName.equals("default") ? "default" : dataSourceName;
            DataSource dataSource = dataSourceCache.computeIfAbsent(dsKey, this::createDataSource);
            Connection con = dataSource.getConnection();
            log.debug("[DB-IMPORT] Get connection from pooled datasource {}", dataSourceName);
            DatabaseConnection dbCon = new DatabaseConnection(con);
            dbCon.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
            // support only postgresql
            dbCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new CustomPostgresDataTypeFactory());
            return dbCon;
        } catch (Exception ex) {
            throw new RuntimeException("Error create database connection", ex);
        }
    }

    protected DataSource createDataSource(String name) {
        try {
            String prefix = "tkit-db-import.quarkus.datasource.";
            if (name != null && !name.equals("default")) {
                prefix = prefix + name + ".";
            }
            Config config = ConfigProvider.getConfig();
            String username = config.getValue(prefix + "username", String.class);
            String password = config.getValue(prefix + "password", String.class);
            String url = config.getValue(prefix + "jdbc.url", String.class);

            // Extract JDBC pool configuration
            Integer jdbcMaxSize = config.getOptionalValue(prefix + "jdbc.max-size", Integer.class).orElse(20);
            Integer jdbcMinSize = config.getOptionalValue(prefix + "jdbc.min-size", Integer.class).orElse(2);
            Integer jdbcInitialSize = config.getOptionalValue(prefix + "jdbc.initial-size", Integer.class).orElse(2);
            Integer acquisitionTimeoutSeconds = config.getOptionalValue(prefix + "jdbc.acquisition-timeout", Integer.class)
                    .orElse(10);

            log.info("[DB-IMPORT] Creating pooled datasource for {} with url: {} (pool size: {}-{})", name, url, jdbcMinSize,
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

    protected IDataSet getDataSet(Request request) throws Exception {
        boolean columnSensing = request.ano().columnSensing();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (Objects.requireNonNull(request.type()) == FileType.XML) {
            return new FlatXmlDataSetBuilder().setColumnSensing(columnSensing).build(cl.getResourceAsStream(request.path()));
        }
        throw new RuntimeException("No datasource found for the type " + request.type());
    }

}
