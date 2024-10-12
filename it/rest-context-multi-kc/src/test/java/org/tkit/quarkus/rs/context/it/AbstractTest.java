package org.tkit.quarkus.rs.context.it;

import static org.tkit.quarkus.rs.context.it.KeycloakTestResource.*;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTestResource(KeycloakTestResource.class)
public class AbstractTest {

    static final String ALICE = "alice";

    protected KeycloakTestClient createClient() {
        return new KeycloakTestClient(getPropertyValue(authServerUrlProp(KC0), null));
    }

    protected KeycloakTestClient createClient1() {
        return new KeycloakTestClient(getPropertyValue(authServerUrlProp(KC1), null));
    }

    protected KeycloakTestClient createClient2() {
        return new KeycloakTestClient(getPropertyValue(authServerUrlProp(KC2), null));
    }

    public String getPropertyValue(String prop, String defaultValue) {
        return ConfigProvider.getConfig().getOptionalValue(prop, String.class)
                .orElse(null);
    }

}
