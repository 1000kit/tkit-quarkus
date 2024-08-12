package org.tkit.quarkus.test;

import io.smallrye.config.SmallRyeConfigBuilder;

public final class TestConfigUtil {

    private final static TestConfig TEST_CONFIG;

    static {
        TEST_CONFIG = new SmallRyeConfigBuilder()
                .addDefaultSources()
                .withMapping(TestConfig.class)
                .build()
                .getConfigMapping(TestConfig.class);
    }

    static public TestConfig config() {
        return TEST_CONFIG;
    }
}
