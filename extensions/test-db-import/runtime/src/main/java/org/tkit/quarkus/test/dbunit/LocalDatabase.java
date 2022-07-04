package org.tkit.quarkus.test.dbunit;


import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.test.WithDBData;

import java.sql.Connection;
import java.sql.DriverManager;

public class LocalDatabase implements Database {

    private static final Logger log = LoggerFactory.getLogger(LocalDatabase.class);

    @Override
    public void deleteData(WithDBData ano, FileType type, String file) throws Exception {
        IDataSet dataSet = getDataSet(type, file);
        DatabaseOperation.DELETE_ALL.execute(getConnection(ano.datasource()), dataSet);
    }

    @Override
    public void insertData(WithDBData ano, FileType type, String file) throws Exception {
        IDataSet dataSet = getDataSet(type, file);
        DatabaseOperation op = DatabaseOperation.INSERT;
        if (ano.deleteBeforeInsert()) {
            op = DatabaseOperation.CLEAN_INSERT;
        }
        op.execute(getConnection(ano.datasource()), dataSet);
    }

    protected IDatabaseConnection getConnection(String dataSourceName) {
        try {
            Connection con;
            if (dataSourceName.equals("default")) {
                con = createConnection(null);
            } else {
                con = createConnection(dataSourceName);
            }
            log.debug("[DB-IMPORT] Create new database connection from datasource {}", dataSourceName);
            DatabaseConnection dbCon = new DatabaseConnection(con);
            dbCon.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
            // support only postgresql
            dbCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new CustomPostgresDataTypeFactory());
            return dbCon;
        } catch (Exception ex) {
            throw new RuntimeException("Error create database connection", ex);
        }
    }
    protected Connection createConnection(String name) throws Exception {
        String prefix = "quarkus.datasource.";
        if (name != null) {
            prefix = prefix + name + ".";
        }
        Config config = ConfigProvider.getConfig();
        String username = config.getValue(prefix + "username", String.class);
        String password = config.getValue(prefix + "password", String.class);
        String url = config.getValue(prefix + "jdbc.url", String.class);
        log.debug("[DB-IMPORT] db url: {}", url);
        return DriverManager.getConnection(url, username, password);
    }

    protected IDataSet getDataSet(FileType type, String file) throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        switch (type) {
            case XML:
                return new FlatXmlDataSetBuilder().build(cl.getResourceAsStream(file));
            case XLS:
                return new XlsDataSet(cl.getResourceAsStream(file));
        }
        throw new RuntimeException("No datasource found for the type " + type);
    }

}