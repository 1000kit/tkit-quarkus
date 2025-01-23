package org.tkit.quarkus.oidc.health.it;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.*;

public class RealmFactory {

    public static RealmRepresentation createRealm(String name, String clientId, String clientSecret,
            Map<String, List<String>> users) {
        var realm = createRealm(name);
        realm.getClients().add(createClient(clientId, clientSecret));
        if (users != null) {
            var userRoles = users.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
            for (String role : userRoles) {
                realm.getRoles().getRealm().add(new RoleRepresentation(role, null, false));
            }
            for (Map.Entry<String, List<String>> user : users.entrySet()) {
                realm.getUsers().add(createUser(user.getKey(), user.getKey(), user.getValue()));
            }
        }
        return realm;
    }

    private static RealmRepresentation createRealm(String name) {
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(name);
        realm.setEnabled(true);
        realm.setUsers(new ArrayList<>());
        realm.setClients(new ArrayList<>());
        realm.setAccessTokenLifespan(600);
        realm.setSsoSessionMaxLifespan(600);
        realm.setRefreshTokenMaxReuse(10);
        RolesRepresentation roles = new RolesRepresentation();
        List<RoleRepresentation> realmRoles = new ArrayList<>();
        roles.setRealm(realmRoles);
        realm.setRoles(roles);
        return realm;
    }

    private static UserRepresentation createUser(String username, String password, List<String> realmRoles) {
        UserRepresentation user = new UserRepresentation();

        user.setUsername(username);
        user.setFirstName(username);
        user.setLastName(username);
        user.setEnabled(true);
        user.setCredentials(new ArrayList<>());
        user.setEmail(username + "@mail.com");
        user.setEmailVerified(true);
        user.setRealmRoles(realmRoles);
        user.setRequiredActions(null);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        user.getCredentials().add(credential);

        return user;
    }

    private static ClientRepresentation createClient(String clientId, String oidcClientSecret) {
        ClientRepresentation client = new ClientRepresentation();

        client.setClientId(clientId);
        client.setRedirectUris(List.of("*"));
        client.setPublicClient(false);
        client.setSecret(oidcClientSecret);
        client.setDirectAccessGrantsEnabled(true);
        client.setServiceAccountsEnabled(true);
        client.setImplicitFlowEnabled(true);
        client.setEnabled(true);
        client.setRedirectUris(List.of("*"));
        client.setDefaultClientScopes(List.of("microprofile-jwt"));

        return client;
    }

}
