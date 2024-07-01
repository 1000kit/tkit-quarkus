package org.tkit.quarkus.security.test;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.util.JsonSerialization;

import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class SecurityTestUtils {

    protected final static KeycloakTestClient keycloakTestClient = new KeycloakTestClient();

    public static String getKeycloakClientToken(String clientName) {
        return keycloakTestClient.getRealmClientAccessToken("quarkus", clientName, "secret");
    }

    private static RequestSpecification createRequestSpec() {
        return given();
    }

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
                            .post(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/client-scopes")).then()
                            .log().all())
                            .statusCode(201);
                } catch (IOException var2) {
                    throw new RuntimeException(var2);
                }

            });
            client.setDefaultClientScopes(createdScopes);

        }

        try {
            ((ValidatableResponse) ((Response) createRequestSpec().auth().oauth2(keycloakTestClient.getAdminAccessToken())
                    .contentType("application/json").body(JsonSerialization.writeValueAsBytes(client)).when()
                    .post(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/clients")).then()).statusCode(201);
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static void removeClient(String clientName) {
        ((ValidatableResponse) ((Response) createRequestSpec().auth().oauth2(keycloakTestClient.getAdminAccessToken()).when()
                .delete(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/clients/" + clientName)).then().log()
                .all())
                .statusCode(204);
    }

    public static void removeClientScopes(List<String> scopes) {
        scopes.forEach(id -> {
            ((ValidatableResponse) ((Response) createRequestSpec().auth().oauth2(keycloakTestClient.getAdminAccessToken())
                    .when()
                    .delete(keycloakTestClient.getAuthServerBaseUrl() + "/admin/realms/quarkus/client-scopes/" + id)).then()
                    .log()
                    .all())
                    .statusCode(204);
        });
    }
}
