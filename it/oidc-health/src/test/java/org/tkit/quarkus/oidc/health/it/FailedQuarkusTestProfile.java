package org.tkit.quarkus.oidc.health.it;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class FailedQuarkusTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "quarkus.oidc.kc1.auth-server-url", "http://localhost:8083/realms/onecx");
    }

}
