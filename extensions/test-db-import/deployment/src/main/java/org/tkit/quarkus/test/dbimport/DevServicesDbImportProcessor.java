package org.tkit.quarkus.test.dbimport;

import static org.testcontainers.DockerClientFactory.SESSION_ID;
import static org.testcontainers.DockerClientFactory.TESTCONTAINERS_SESSION_ID_LABEL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;

import io.quarkus.datasource.common.runtime.DatabaseKind;
import io.quarkus.datasource.deployment.spi.DevServicesDatasourceResultBuildItem;
import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.BuildSteps;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;
import io.quarkus.deployment.builditem.DevServicesSharedNetworkBuildItem;
import io.quarkus.deployment.dev.devservices.DevServicesConfig;

@BuildSteps(onlyIfNot = IsNormal.class, onlyIf = DevServicesConfig.Enabled.class)
public class DevServicesDbImportProcessor {

    private static final Logger log = LoggerFactory.getLogger(DevServicesDbImportProcessor.class);

    private static final Integer POSTGRESQL_PORT = 5432;

    @BuildStep
    public DevServicesResultBuildItem startDbImportDevService(
            DevServicesDatasourceResultBuildItem devServiceDatasource,
            DbImportBuildTimeConfig dbImportClientBuildTimeConfig,
            List<DevServicesSharedNetworkBuildItem> devServicesSharedNetworkBuildItem) {

        // create dev service config
        DbImportDevServicesBuildTimeConfig configuration = dbImportClientBuildTimeConfig.devservices();
        if (!configuration.enabled().orElse(true)) {
            // explicitly disabled
            log.debug("Not starting dev services for db-import, as it has been disabled in the config.");
            return null;
        }

        // keep the previous behaviour of producing DevServicesDatasourceResultBuildItem only when the devservices first starts.
        if (devServiceDatasource == null) {
            log.debug("Dev service datasource is null, keep the previous configuration.");
            return null;
        }

        HashMap<String, String> data = new HashMap<>();

        setupDbResultSystemProperties(data, devServiceDatasource.getDefaultDatasource());
        devServiceDatasource.getNamedDatasources().forEach((k, v) -> setupDbResultSystemProperties(data, v));
        if (data.isEmpty()) {
            log.warn("No datasource of the type postgresql found!");
            return null;
        }

        try {
            if (!devServicesSharedNetworkBuildItem.isEmpty()) {

                DockerClient dockerClient = DockerClientFactory.lazyClient();
                Map<String, Container> containers = dockerClient.listContainersCmd().exec().stream()
                        .filter(c -> (SESSION_ID.equals(c.getLabels().get(TESTCONTAINERS_SESSION_ID_LABEL))
                                && c.getImage().startsWith(configuration.dbImageName())))
                        .collect(Collectors.toMap(c -> c.getLabels().get("datasource"), c -> c));

                if (containers.isEmpty()) {
                    log.error("No postgres image found.");
                } else {
                    containers.forEach((datasource, container) -> {
                        InspectContainerResponse inspect = dockerClient.inspectContainerCmd(container.getId()).exec();
                        // db local port

                        if (inspect != null) {
                            Ports.Binding[] binding = inspect.getNetworkSettings().getPorts().getBindings()
                                    .get(new ExposedPort(POSTGRESQL_PORT));
                            if (binding != null) {
                                String port = binding[0].getHostPortSpec();

                                String configProperty = "tkit-db-import.quarkus.datasource.";
                                if (!"default".equals(datasource)) {
                                    configProperty = configProperty + datasource + ".";
                                }
                                configProperty = configProperty + "jdbc.url";

                                String url = data.get(configProperty);
                                url = url.substring(url.lastIndexOf("/"));
                                url = String.format("jdbc:postgresql://%s:%s%s",
                                        DockerClientFactory.instance().dockerHostIpAddress(),
                                        port, url);

                                data.put(configProperty, url);
                                log.info("Local jdbc {}={}", configProperty, url);
                            }
                        }
                    });
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        return new DevServicesResultBuildItem("tkit-db-import", null, data);
    }

    private static void setupDbResultSystemProperties(HashMap<String, String> data,
            DevServicesDatasourceResultBuildItem.DbResult dbResult) {

        String jdbcUrlKey = null;
        String reactiveUrlKey = null;
        String reactiveUrlValue = null;
        if (dbResult != null && DatabaseKind.POSTGRESQL.equals(dbResult.getDbType())) {
            for (Map.Entry<String, String> e : dbResult.getConfigProperties().entrySet()) {
                if (e.getKey().lastIndexOf("jdbc.url") > 0) {
                    jdbcUrlKey = e.getKey();
                }
                if (e.getKey().lastIndexOf("reactive.url") > 0) {
                    reactiveUrlKey = e.getKey();
                    reactiveUrlValue = e.getValue();
                }
                data.put("tkit-db-import." + e.getKey(), e.getValue());
                System.setProperty("tkit-db-import." + e.getKey(), e.getValue());
            }
        }

        // add support for only reactive vertx client dependency
        if (jdbcUrlKey == null && reactiveUrlValue != null) {
            String newValue = reactiveUrlValue.replace("vertx-reactive", "jdbc");
            jdbcUrlKey = reactiveUrlKey.replace("reactive", "jdbc");
            data.put("tkit-db-import." + jdbcUrlKey, newValue);
        }
    }

}
