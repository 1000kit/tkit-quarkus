package org.tkit.quarkus.oidc.health.it;

import static io.restassured.RestAssured.given;
import static org.tkit.quarkus.oidc.health.it.RealmFactory.createRealm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.keycloak.server.KeycloakContainer;

public class KeycloakTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger log = LoggerFactory.getLogger(KeycloakTestResource.class);

    private static boolean START_KC1 = true;

    public static final String KC0 = "kc0";
    public static final String KC1 = "kc1";

    private final List<TestKeycloakContainer> containers = new ArrayList<>();

    private static final String KEYCLOAK_REALM = "quarkus";

    @Override
    public void setContext(Context context) {
        START_KC1 = !FailedQuarkusTestProfile.class.getName().equals(context.testProfile());
    }

    @Override
    public Map<String, String> start() {

        containers.add(new TestKeycloakContainer(KC0));
        if (START_KC1) {
            containers.add(new TestKeycloakContainer(KC1));
        }
        containers.forEach(this::startContainer);

        Map<String, String> result = new HashMap<>();
        containers.forEach(c -> {
            result.put(authServerUrlProp(c.getName()), c.getServerUrl() + "/realms/" + KEYCLOAK_REALM);
        });
        return result;
    }

    private void startContainer(TestKeycloakContainer container) {
        System.out.println("---> Start container. Name: " + container.getName());
        log.info("Start container. Name: '{}'", container.getName());
        container.start();

        RealmRepresentation realm = createRealm(KEYCLOAK_REALM, "quarkus-app", "secret",
                Map.of("alice", List.of("user", "admin"), "bob", List.of("user")));

        log.info("Create realm '{}' for container '{}'", realm.getRealm(), container.getName());
        postRealm(container.getServerUrl(), realm);

        log.info("Container started. Name: '{}'", container.getName());
    }

    public static String authServerUrlProp(String name) {
        return name + ".auth-server-url";
    }

    @Override
    public void stop() {
        containers.forEach(k -> {
            try {
                deleteRealm(k.getServerUrl(), KEYCLOAK_REALM);
                k.stop();
            } catch (Exception ex) {
                log.error("Error stopping container {}", k, ex);
            }
        });
    }

    public static class TestKeycloakContainer extends KeycloakContainer {

        private final String name;

        public TestKeycloakContainer(String name) {
            this.name = name;
            this.withNetworkAliases(name);
        }

        public String getName() {
            return name;
        }

        public String getInternalUrl() {
            return String.format("http://%s:%d", name, getPort());
        }
    }

    private static void deleteRealm(String url, String name) {
        given().auth().oauth2(getAdminAccessToken(url))
                .when()
                .delete(url + "/admin/realms/" + name)
                .then()
                .statusCode(204);
    }

    private static void postRealm(String url, RealmRepresentation realm) {
        try {
            given().auth().oauth2(getAdminAccessToken(url))
                    .contentType("application/json")
                    .body(JsonSerialization.writeValueAsString(realm))
                    .when()
                    .post(url + "/admin/realms")
                    .then()
                    .statusCode(201);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getAdminAccessToken(String url) {
        return given()
                .param("grant_type", "password")
                .param("username", "admin")
                .param("password", "admin")
                .param("client_id", "admin-cli")
                .when()
                .post(url + "/realms/master/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }

}
