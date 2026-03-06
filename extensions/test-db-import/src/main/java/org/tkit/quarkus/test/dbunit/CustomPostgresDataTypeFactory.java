package org.tkit.quarkus.test.dbunit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.TypeCastException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.postgresql.util.PGobject;

public class CustomPostgresDataTypeFactory extends PostgresqlDataTypeFactory {

    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if ("jsonb".equals(sqlTypeName)) {
            return new JsonBDataType();
        }
        return super.createDataType(sqlType, sqlTypeName);
    }

    public static class JsonBDataType extends AbstractDataType {

        public JsonBDataType() {
            super("jsonb", Types.OTHER, String.class, false);
        }

        @Override
        public Object typeCast(Object o) {
            return o.toString();
        }

        @Override
        public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException, TypeCastException {
            PGobject jsonObj = new PGobject();
            jsonObj.setType("json");
            if (value == null) {
                jsonObj.setValue(null);
            } else {
                jsonObj.setValue(value.toString());
            }
            statement.setObject(column, jsonObj);

        }
    }

}
