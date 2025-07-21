package org.tkit.quarkus.security.dynamic.test;

import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

public class SecurityDynamicTestCodeGenProvider implements CodeGenProvider {

    private static final Logger log = LoggerFactory.getLogger(SecurityDynamicTestCodeGenProvider.class);

    @Override
    public String providerId() {
        return SecurityDynamicTestBuild.FEATURE_NAME;
    }

    @Override
    public String inputDirectory() {
        return "java";
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        if (!context.test()) {
            return false;
        }

        var value = context.config().getOptionalValue("tkit.security-test.openapi.disable-value", String.class);
        if (value.isPresent()) {
            var regex = context.config().getOptionalValue("tkit.security-test.openapi.disable-regex", String.class);
            if (regex.isPresent()) {
                if (value.get().matches(regex.get())) {
                    log.info("Create dynamic test is disabled. Value: {}, Regex: {}", value.get(), regex.get());
                    return false;
                }
            }
        }

        var dir = context.outDir().resolve("org/tkit/quarkus/security/dynamic/test/");
        var file = dir.resolve("SecurityDynamicImplTest.java");
        var template = """
                package org.tkit.quarkus.security.dynamic.test;

                import io.quarkus.test.junit.QuarkusTest;
                import javax.annotation.processing.Generated;
                import org.tkit.quarkus.security.dynamic.test.openapi.SecurityDynamicTest;

                @Generated(value = "org.tkit.quarkus.security.dynamic.test.SecurityDynamicTestCodeGenProvider")
                @QuarkusTest
                public class SecurityDynamicImplTest extends SecurityDynamicTest {
                }
                """;

        log.info("Create dynamic test: {}", context.workDir().relativize(file));
        try {
            Files.createDirectories(dir);
            Files.writeString(file, template);
        } catch (Exception e) {
            log.error("Creating the dynamic test class failed. Error: {}", e.getMessage(), e);
        }
        return true;
    }
}
