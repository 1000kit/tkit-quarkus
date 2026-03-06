package org.tkit.quarkus.test.dbunit;

import static org.tkit.quarkus.test.dbunit.ConfigurationConst.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;

import io.quarkus.devservices.crossclassloader.runtime.RunningDevServicesRegistry;
import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class LocalDatabaseTestResource implements QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {

    private static final Logger log = LoggerFactory.getLogger(LocalDatabaseTestResource.class);

    private static DevServicesContext CONTEXT;

    private static final String LABEL_DATASOURCE = "datasource";

    private static final String LABEL_PROCESS_UUID = "io.quarkus.devservice.process-uuid";

    private static final Integer POSTGRESQL_PORT = 5432;

    @Override
    public Map<String, String> start() {
        boolean enabled = ConfigProvider.getConfig()
                .getOptionalValue(ConfigurationConst.ENABLED, Boolean.class)
                .orElse(true);
        if (!enabled) {
            log.warn("Db-Import for test is disabled, skipping the start.");
            return Map.of();
        }
        if (CONTEXT == null) {
            log.warn("Db-Import for test missing dev-service context.");
            return Map.of();
        }

        var properties = CONTEXT.devServicesProperties().entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(QUARKUS_DATASOURCE_PREFIX))
                .filter(e -> e.getKey().endsWith(ConfigurationConst.JDBC_URL)
                        || e.getKey().endsWith(ConfigurationConst.USERNAME) || e.getKey().endsWith(ConfigurationConst.PASSWORD))
                .collect(Collectors.groupingBy(entry -> {
                    String key = entry.getKey();
                    String rest = key.substring(QUARKUS_DATASOURCE_PREFIX.length());
                    rest = rest.replace(JDBC_URL, "").replace(USERNAME, "").replace(PASSWORD, "");
                    rest = rest.replace(".", "");

                    if (rest.isEmpty()) {
                        return ConfigurationConst.DEFAULT_DATASOURCE_VALUE;
                    } else {
                        return rest;
                    }
                }, Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        if (properties.isEmpty()) {
            log.warn("Db-Import for test no properties have been found.");
            return Map.of();
        }

        Map<String, Container> containers = new HashMap<>();
        if (CONTEXT.containerNetworkId().isPresent()) {
            DockerClient dockerClient = DockerClientFactory.lazyClient();
            containers = dockerClient.listContainersCmd().exec().stream()
                    .filter(container -> RunningDevServicesRegistry.APPLICATION_UUID
                            .equals(container.getLabels().get(LABEL_PROCESS_UUID)))
                    .filter(container -> container.getLabels().containsKey(LABEL_DATASOURCE))
                    .collect(Collectors.toMap(c -> c.getLabels().get(LABEL_DATASOURCE), c -> c));

        }

        var result = new HashMap<String, String>();

        for (var entry : properties.entrySet()) {
            var datasource = entry.getKey();
            var props = entry.getValue();

            for (var prop : props.entrySet()) {
                var key = prop.getKey();
                var value = prop.getValue();

                if (!containers.isEmpty() && key.endsWith(JDBC_URL)) {

                    if (datasource.startsWith("\"") && datasource.endsWith("\"")) {
                        datasource = datasource.substring(1, datasource.length() - 1);
                    }
                    var container = containers.get(datasource);
                    if (container != null) {
                        var port = getPublicPort(container);
                        value = replaceHostPort(value, DockerClientFactory.instance().dockerHostIpAddress(), port);
                    } else {
                        log.warn("No container found for datasource {}, using original jdbc url.", datasource);
                    }
                }
                result.put(ConfigurationConst.PREFIX + key, value);
            }
        }
        log.debug("Db-Import for test datasource properties: {}", result);
        return result;
    }

    private String getPublicPort(Container container) {
        for (var port : container.getPorts()) {
            if (POSTGRESQL_PORT.equals(port.getPrivatePort())) {
                return "" + port.getPublicPort();
            }
        }
        return POSTGRESQL_PORT.toString();
    }

    public static String replaceHostPort(String input, String newHost, String newPort) {
        if (input == null)
            return null;

        int schemeSep = input.indexOf("//");
        if (schemeSep < 0) {
            return input;
        }

        int authorityStart = schemeSep + 2;
        int pathStart = input.indexOf("/", authorityStart);

        int authorityEnd = (pathStart >= 0) ? pathStart : input.length();

        String authority = newHost;
        if (newPort != null) {
            authority = newHost + ":" + newPort;
        }

        return input.substring(0, authorityStart) + authority + input.substring(authorityEnd);
    }

    @Override
    public void stop() {
    }

    @Override
    public void setIntegrationTestContext(DevServicesContext context) {
        CONTEXT = context;
    }

}
