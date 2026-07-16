package org.tkit.quarkus.test;

import static org.tkit.quarkus.test.dbunit.ConfigurationConst.DATASOURCE_PREFIX;
import static org.tkit.quarkus.test.dbunit.ConfigurationConst.DEFAULT_DATASOURCE_VALUE;
import static org.tkit.quarkus.test.dbunit.ConfigurationConst.JDBC_URL;
import static org.tkit.quarkus.test.dbunit.ConfigurationConst.PASSWORD;
import static org.tkit.quarkus.test.dbunit.ConfigurationConst.USERNAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Helper class for direct JDBC operations in tests.
 * Works in both {@code @QuarkusTest} and {@code @QuarkusIntegrationTest} as
 * long as
 * the datasource is provided by Dev Services and the
 * {@code tkit-quarkus-test-db-import}
 * library is on the test classpath (it auto-registers
 * {@code LocalDatabaseTestResource}
 * which publishes the correct, host-accessible JDBC URL via
 * {@code ConfigProvider}).
 */
public class JdbcTestHelper {

    private final String datasourceName;

    /** Uses the default (unnamed) datasource. */
    public JdbcTestHelper() {
        this(DEFAULT_DATASOURCE_VALUE);
    }

    /** Uses a named datasource, e.g. {@code quarkus.datasource.<name>.jdbc.url}. */
    public JdbcTestHelper(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    protected Connection getConnection() throws SQLException {
        String prefix = DATASOURCE_PREFIX;
        if (datasourceName != null && !datasourceName.equals(DEFAULT_DATASOURCE_VALUE)) {
            prefix = prefix + datasourceName + ".";
        }
        Config config = ConfigProvider.getConfig();
        String url = config.getValue(prefix + JDBC_URL, String.class);
        String username = config.getValue(prefix + USERNAME, String.class);
        String password = config.getValue(prefix + PASSWORD, String.class);
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        return DriverManager.getConnection(url, props);
    }

    public long count(String sql, Object... params) {
        return queryOne(sql, rs -> rs.getLong(1), params).orElse(0L);
    }

    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        List<T> rows = queryList(sql, mapper, params);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public <T> List<T> queryList(String sql, RowMapper<T> mapper, Object... params) {
        List<T> results = new ArrayList<>();
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query failed: " + sql, e);
        }
        return results;
    }

    public List<Map<String, String>> queryList(String sql, Object... params) {
        return queryList(sql, this::mapRowToStrings, params);
    }

    private Map<String, String> mapRowToStrings(ResultSet rs) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        Map<String, String> row = new LinkedHashMap<>();
        for (int col = 1; col <= metadata.getColumnCount(); col++) {
            row.put(metadata.getColumnLabel(col), rs.getString(col));
        }
        return row;
    }

    public int executeUpdate(String sql, Object... params) {
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Update failed: " + sql, e);
        }
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
