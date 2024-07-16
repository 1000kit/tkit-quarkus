package org.tkit.quarkus.security.test;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;

public class SecurityTestUtils {

    private static final Logger log = LoggerFactory.getLogger(SecurityTestUtils.class);

    protected final static KeycloakTestClient keycloakTestClient = new KeycloakTestClient();

    /**
     * Method to fetch the access-token needed for oauth2 for a client by name
     * used realm is "quarkus" by default
     * used client-secret is "secret" by default
     *
     * @param clientName name of client to get the access token from.
     * @return access-token for given client
     */
    public static String getKeycloakClientToken(String clientName) {
        return keycloakTestClient.getRealmClientAccessToken("quarkus", clientName, "secret");
    }

    /**
     * Method to manually add a new client with scopes to the default quarkus realm
     *
     * @param clientName name of client which should be added to the realm
     * @param scopes list of scopes which should be created to the realm and add to the client
     *
     */
    public static void addClient(String clientName, List<String> scopes) {
        addClient(clientName, scopes, scopes);
    }

    /**
     * Method to manually add a new client with scopes to the default quarkus realm
     *
     * @param clientName name of client which should be added to the realm
     * @param createScopes list of scopes which should be created to the realm
     * @param clientScopes list of scopes which should be added to the realm and the given client
     *
     */
    public static void addClient(String clientName, List<String> createScopes, List<String> clientScopes) {

        List<String> createdScopes = new ArrayList<>(clientScopes);

        if (!createScopes.isEmpty()) {
            createScopes.forEach(scopeName -> {

                ClientScopeRepresentation scope = new ClientScopeRepresentation();
                scope.setName(scopeName.trim());
                scope.setId(scopeName.trim());
                scope.setAttributes(Map.of("include.in.token.scope", "true"));
                scope.setProtocol("openid-connect");
                createdScopes.add(scope.getId());
                try {
                    var response = given()
                            .auth()
                            .oauth2(keycloakTestClient.getAdminAccessToken())
                            .contentType(ContentType.JSON)
                            .body(JsonSerialization.writeValueAsBytes(scope)).when()
                            .post(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/client-scopes")
                            .then()
                            .extract();

                    switch (response.statusCode()) {
                        case 201 -> log.debug("Created client scope with id '{}'", scope.getId());
                        case 409 -> log.warn("Client scope '{}' already exists.", scope.getId());
                        default -> {
                            log.error("Error while creating scope with id: {}", scope.getId());
                            response.response().prettyPrint();
                            throw new RuntimeException("Error create client scopes: " + scopeName);
                        }
                    }
                } catch (IOException var2) {
                    throw new RuntimeException(var2);
                }

            });
        }

        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(clientName);
        client.setId(clientName);
        client.setName(clientName);
        client.setClientAuthenticatorType("client-secret");
        client.setSecret("secret");
        client.setStandardFlowEnabled(false);
        client.setImplicitFlowEnabled(false);
        client.setDirectAccessGrantsEnabled(false);
        client.setServiceAccountsEnabled(true);
        client.setEnabled(true);
        client.setRedirectUris(List.of("*"));
        client.setWebOrigins(List.of("*"));
        client.setFullScopeAllowed(true);
        client.setAttributes(Map.of("use.refresh.tokens", "true"));
        client.setDefaultClientScopes(createdScopes);

        try {
            given()
                    .auth().oauth2(keycloakTestClient.getAdminAccessToken())
                    .contentType(ContentType.JSON)
                    .body(JsonSerialization.writeValueAsBytes(client))
                    .when()
                    .post(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/clients")
                    .then()
                    .onFailMessage("Error create client: " + clientName)
                    .statusCode(201);
            log.debug("Created client with id: {}", client.getId());
        } catch (IOException var2) {
            log.error("Error while creating client with id: {}", client.getClientId());
            throw new RuntimeException(var2);
        }
    }

    /**
     * Method to manually remove a client by its name
     *
     * @param clientName name of client which should be removed from realm
     */
    public static void removeClient(String clientName) {
        given()
                .auth().oauth2(keycloakTestClient.getAdminAccessToken())
                .when()
                .delete(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/clients/" + clientName)
                .then()
                .onFailMessage("Error remove client with name: " + clientName)
                .statusCode(204);
        log.debug("Removed client with name: {}", clientName);
    }

    /**
     * Method to manually remove client scopes
     *
     * @param scopes list of scopes which should be removed from realm
     */
    public static void removeClientScopes(List<String> scopes) {
        scopes.forEach(id -> {
            given()
                    .auth().oauth2(keycloakTestClient.getAdminAccessToken())
                    .when()
                    .delete(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/client-scopes/" + id)
                    .then()
                    .onFailMessage("Error remove client scopes: " + scopes)
                    .statusCode(204);
            log.debug("Removed scope with id: {}", id);
        });
    }
}
