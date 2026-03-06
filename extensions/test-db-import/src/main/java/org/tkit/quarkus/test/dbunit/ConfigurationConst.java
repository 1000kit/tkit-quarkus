package org.tkit.quarkus.test.dbunit;

public interface ConfigurationConst {

    String DEFAULT_DATASOURCE_VALUE = "default";

    String ENABLED = "tkit.test.db-import.enabled";

    String PREFIX = "tkit-db-import.";

    String QUARKUS_DATASOURCE_PREFIX = "quarkus.datasource.";

    String DATASOURCE_PREFIX = PREFIX + QUARKUS_DATASOURCE_PREFIX;

    String USERNAME = "username";

    String PASSWORD = "password";

    String JDBC_URL = "jdbc.url";
}
