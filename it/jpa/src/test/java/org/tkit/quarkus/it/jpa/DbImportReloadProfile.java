package org.tkit.quarkus.it.jpa;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

/**
 * Dummy-config test profile whose only purpose is to differ from the default profile.
 *
 * <p>
 * Applied to {@link UserRestControllerDbImportReloadIT}, it makes that class use a different
 * Quarkus configuration than the default-profile {@code UserRestControllerIT}. In a
 * {@code @QuarkusIntegrationTest} run (single shared failsafe JVM) that difference forces Quarkus to
 * reload the application onto a freshly provisioned DevServices database between the two classes -
 * which is exactly what exercises the {@code DataSourcePool} JDBC-URL-aware cache key.
 */
public class DbImportReloadProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of("tkit.it.db-import-reload", "dummy");
    }
}
