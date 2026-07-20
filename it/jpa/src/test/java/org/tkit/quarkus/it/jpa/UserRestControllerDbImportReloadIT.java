package org.tkit.quarkus.it.jpa;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.TestProfile;

/**
 * Integration-test regression guard for the {@code test-db-import} {@code DataSourcePool} stale-cache
 * bug.
 *
 * <p>
 * This class reuses {@link UserRestControllerTest}'s {@code @WithDBData} import + {@code GET {id}}
 * verification, but runs under {@link DbImportReloadProfile}. Together with the default-profile
 * {@code UserRestControllerIT}, the two classes share one failsafe JVM but use different profiles, so
 * Quarkus reloads the app onto a new DevServices database between them. The static
 * {@code DataSourcePool} cache survives that reload.
 *
 * <ul>
 * <li>Before the fix (cache keyed by datasource name only) the second class to run reuses the
 * DataSource cached by the first - now pointing at a stopped database - and fails with
 * {@code PSQLException: An I/O error occurred while sending to the backend}.</li>
 * <li>With the fix (cache key includes the JDBC URL) a fresh DataSource is created for the new
 * database and the import + read-back succeed.</li>
 * </ul>
 */
@QuarkusIntegrationTest
@TestProfile(DbImportReloadProfile.class)
class UserRestControllerDbImportReloadIT extends UserRestControllerTest {
}
