package org.tkit.quarkus.test.dbunit;

import java.sql.Connection;
import java.util.Objects;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalDatabase implements Database {

    private static final Logger log = LoggerFactory.getLogger(LocalDatabase.class);

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
            Connection con = DataSourcePool.getConnection(dataSourceName);
            log.debug("[DB-IMPORT] Get connection from pooled datasource {}", dataSourceName);
            DatabaseConnection dbCon = new DatabaseConnection(con);
            dbCon.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
            // support only postgresql
            dbCon.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                    new CustomPostgresDataTypeFactory());
            return dbCon;
        } catch (Exception ex) {
            throw new RuntimeException("Error create database connection", ex);
        }
    }

    protected IDataSet getDataSet(Request request) throws Exception {
        boolean columnSensing = request.ano().columnSensing();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (Objects.requireNonNull(request.type()) == FileType.XML) {
            return new FlatXmlDataSetBuilder().setColumnSensing(columnSensing)
                    .build(cl.getResourceAsStream(request.path()));
        }
        throw new RuntimeException("No datasource found for the type " + request.type());
    }

}