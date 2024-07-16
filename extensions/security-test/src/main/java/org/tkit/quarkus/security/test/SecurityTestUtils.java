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
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

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

    private static RequestSpecification createRequestSpec() {
        return given();
    }

    /**
     * Method to manually add a new client with scopes to the default quarkus realm
     *
     * @param clientName name of client which should be added to the realm
     * @param scopeNames list of scopes which should be added to the realm and the given client
     */
    public static void addClient(String clientName, List<String> scopeNames) {
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

        if (!scopeNames.isEmpty()) {
            List<String> createdScopes = new ArrayList<>();
            scopeNames.forEach(scopeName -> {
                ClientScopeRepresentation scope = new ClientScopeRepresentation();
                scope.setName(scopeName.trim());
                scope.setId(scopeName.trim());
                scope.setAttributes(Map.of("include.in.token.scope", "true"));
                scope.setProtocol("openid-connect");
                createdScopes.add(scope.getId());
                try {
                    ((ValidatableResponse) ((Response) createRequestSpec().auth()
                            .oauth2(keycloakTestClient.getAdminAccessToken())
                            .contentType("application/json").body(JsonSerialization.writeValueAsBytes(scope)).when()
                            .post(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/client-scopes")).then())
                            .statusCode(201);
                    log.debug("Created scope with id: {}", scope.getId());
                } catch (IOException var2) {
                    log.error("Error while creating scope with id: {}", scope.getId());
                    throw new RuntimeException(var2);
                }

            });
            client.setDefaultClientScopes(createdScopes);

        }

        try {
            ((ValidatableResponse) ((Response) createRequestSpec().auth().oauth2(keycloakTestClient.getAdminAccessToken())
                    .contentType("application/json").body(JsonSerialization.writeValueAsBytes(client)).when()
                    .post(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/clients")).then()).statusCode(201);
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
        ((ValidatableResponse) ((Response) createRequestSpec().auth().oauth2(keycloakTestClient.getAdminAccessToken()).when()
                .delete(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/clients/" + clientName)).then())
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
            ((ValidatableResponse) ((Response) createRequestSpec().auth().oauth2(keycloakTestClient.getAdminAccessToken())
                    .when()
                    .delete(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/client-scopes/" + id)).then())
                    .statusCode(204);
            log.debug("Removed scope with id: {}", id);
        });
    }
}
