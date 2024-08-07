package org.tkit.quarkus.it.security.test.test;

import java.util.List;

import org.tkit.quarkus.security.test.AbstractSecurityTest;
import org.tkit.quarkus.security.test.SecurityTestConfig;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SecurityTest extends AbstractSecurityTest {
    @Override
    public SecurityTestConfig getConfig() {
        SecurityTestConfig config = new SecurityTestConfig();
        config.addConfig("write", "/users/1", 200, List.of("ocx-user:write"), "post");
        config.addConfig("read", "/users/1", 200, List.of("ocx-user:read"), "get");
        return config;
    }
}
