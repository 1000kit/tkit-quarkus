package org.tkit.quarkus.rs.context.it;

import static io.restassured.RestAssured.given;
import static org.tkit.quarkus.rs.context.it.RealmFactory.createRealm;

import java.io.IOException;
import java.util.*;

import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.*;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.keycloak.server.KeycloakContainer;

public class KeycloakTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger log = LoggerFactory.getLogger(KeycloakTestResource.class);

    public static final String KC0 = "kc0";
    public static final String KC1 = "kc1";
    public static final String KC2 = "kc2";

    private final List<TestKeycloakContainer> containers = new ArrayList<>();

    private static final String KEYCLOAK_REALM = "quarkus";

    @Override
    public Map<String, String> start() {

        containers.add(new TestKeycloakContainer(KC0));
        containers.add(new TestKeycloakContainer(KC1));
        containers.add(new TestKeycloakContainer(KC2));

        containers.forEach(this::startContainer);

        Map<String, String> result = new HashMap<>();
        containers.forEach(c -> {
            result.put(urlProp(c.getName()), c.getServerUrl());
            result.put(authServerUrlProp(c.getName()), c.getServerUrl() + "/realms/" + KEYCLOAK_REALM);
        });
        return result;
    }

    private void startContainer(TestKeycloakContainer container) {
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

    public static String urlProp(String name) {
        return name + ".url";
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
            boolean useHttps = false;
            return String.format("%s://%s:%d",
                    useHttps ? "https" : "http", name, getPort());
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
