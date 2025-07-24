package org.tkit.quarkus.rs.context.it;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class NoHeaderPropagationTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        Map<String, String> config = new HashMap<>();
        // Override the header propagation property with empty list
        config.put("tkit.rs.response.propagation.headers", "[]");
        return config;
    }

    @Override
    public String getConfigProfile() {
        return "no-header-propagation";
    }
}
