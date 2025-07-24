package org.tkit.quarkus.security.dynamic.test;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

public class SecurityDynamicTestCodeGenProvider implements CodeGenProvider {

    private static final Logger log = LoggerFactory.getLogger(SecurityDynamicTestCodeGenProvider.class);

    private static final String PACKAGE_DIR = "org/tkit/quarkus/security/dynamic/test/";

    private static final String CLASS_TEST_FILE = "SecurityDynamicImplTest.java";
    private static final String TEMPLATE_TEST = """
            package org.tkit.quarkus.security.dynamic.test;

            import io.quarkus.test.junit.QuarkusTest;
            import javax.annotation.processing.Generated;
            import org.tkit.quarkus.security.dynamic.test.openapi.SecurityDynamicTest;

            @Generated(value = "org.tkit.quarkus.security.dynamic.test.SecurityDynamicTestCodeGenProvider")
            @QuarkusTest
            public class SecurityDynamicImplTest extends SecurityDynamicTest {
            }
            """;
    private static final String CLASS_INTEGRATION_TEST_FILE = "SecurityDynamicImplIT.java";
    private static final String TEMPLATE_INTEGRATION_TEST = """
            package org.tkit.quarkus.security.dynamic.test;

            import io.quarkus.test.junit.QuarkusIntegrationTest;
            import javax.annotation.processing.Generated;

            @Generated(value = "org.tkit.quarkus.security.dynamic.test.SecurityDynamicTestCodeGenProvider")
            @QuarkusIntegrationTest
            public class SecurityDynamicImplIT extends SecurityDynamicImplTest {
            }
            """;

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

        log.info("Generating test classes with SecurityDynamicTestCodeGenProvider.");

        // check global open-api enabled property
        if (isDisabled(context, "test", "tkit.security-dynamic-test.openapi.enabled")) {
            return false;
        }

        // check test open-api disable-value and disable-regex property
        if (isDisabledValueRegex(context, "test", "tkit.security-dynamic-test.openapi.disable-test-value",
                "tkit.security-dynamic-test.openapi.disable-test-regex")) {
            return false;
        }

        // create package directory
        var dir = createPackageDir(context, PACKAGE_DIR);

        // create test
        generateClass(context, dir, CLASS_TEST_FILE, TEMPLATE_TEST);

        // check integration open-api enabled property
        if (isDisabled(context, "integration test", "tkit.security-dynamic-test.openapi.enabled-integration-test")) {
            return false;
        }

        // check test open-api disable-value and disable-regex property
        if (isDisabledValueRegex(context, "integration test",
                "tkit.security-dynamic-test.openapi.disable-integration-test-value",
                "tkit.security-dynamic-test.openapi.disable-integration-test-regex")) {
            return false;
        }

        // create integration test
        generateClass(context, dir, CLASS_INTEGRATION_TEST_FILE, TEMPLATE_INTEGRATION_TEST);

        return true;
    }

    private boolean isDisabled(CodeGenContext context, String label, String property) {
        var tmp = context.config().getOptionalValue(property, Boolean.class);
        if (tmp.isPresent() && !tmp.get()) {
            log.info("The generation of dynamic OpenAPI {} is disabled. Test class is not generated since '{}' is set to {}.",
                    label, property, false);
            return true;
        }
        return false;
    }

    private boolean isDisabledValueRegex(CodeGenContext context, String label, String valueProperty, String regexProperty) {
        var value = context.config().getOptionalValue(valueProperty, String.class);
        if (value.isPresent()) {
            var regex = context.config().getOptionalValue(regexProperty, String.class);
            if (regex.isPresent()) {
                if (value.get().matches(regex.get())) {
                    log.info("The generation of dynamic OpenAPI {} is disabled. {}={}, {}={}", label, valueProperty,
                            value.get(), regexProperty,
                            regex.get());
                    return true;
                }
            }
        }
        return false;
    }

    private Path createPackageDir(CodeGenContext context, String dir) {
        try {
            var tmp = context.outDir().resolve(dir);
            log.info("create directories {}", context.workDir().relativize(tmp));
            return Files.createDirectories(tmp);
        } catch (Exception e) {
            log.error("Creating package directory failed. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Error create package dir " + dir);
        }
    }

    private void generateClass(CodeGenContext context, Path dir, String fileName, String template) {
        try {
            var file = dir.resolve(fileName);
            log.info("writing file {}", context.workDir().relativize(file));
            Files.writeString(file, template);
        } catch (Exception e) {
            log.error("Creating test class failed. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Error create test class " + fileName);
        }
    }
}
