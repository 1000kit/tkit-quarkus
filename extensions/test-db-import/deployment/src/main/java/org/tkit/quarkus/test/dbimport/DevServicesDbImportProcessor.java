package org.tkit.quarkus.test.dbimport;

import static org.testcontainers.DockerClientFactory.SESSION_ID;
import static org.testcontainers.DockerClientFactory.TESTCONTAINERS_SESSION_ID_LABEL;

import java.util.List;
import java.util.Optional;

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
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;
import io.quarkus.deployment.builditem.DevServicesSharedNetworkBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.console.ConsoleInstalledBuildItem;
import io.quarkus.deployment.console.StartupLogCompressor;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;
import io.quarkus.deployment.logging.LoggingSetupBuildItem;

public class DevServicesDbImportProcessor {

    private static final Logger log = LoggerFactory.getLogger(DevServicesDbImportProcessor.class);

    private static final String DATASOURCE_DEFAULT_URL = "quarkus.datasource.jdbc.url";
    private static final Integer POSTGRESQL_PORT = 5432;

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = GlobalDevServicesConfig.Enabled.class)
    public DevServicesDbImportBuildItem startDbImportDevService(
            DevServicesDatasourceResultBuildItem devServiceDatasource,
            LaunchModeBuildItem launchMode,
            DbImportBuildTimeConfig dbImportClientBuildTimeConfig,
            List<DevServicesSharedNetworkBuildItem> devServicesSharedNetworkBuildItem,
            Optional<ConsoleInstalledBuildItem> consoleInstalledBuildItem,
            BuildProducer<DevServicesResultBuildItem> devServicePropertiesProducer,
            LoggingSetupBuildItem loggingSetupBuildItem) {

        // create dev service config
        DbImportDevServicesBuildTimeConfig configuration = dbImportClientBuildTimeConfig.devservices;
        if (!configuration.enabled.orElse(true)) {
            // explicitly disabled
            log.debug("Not starting dev services for db-import, as it has been disabled in the config.");
            return null;
        }

        DevServicesDatasourceResultBuildItem.DbResult dbResult = devServiceDatasource.getDefaultDatasource();
        if (!DatabaseKind.POSTGRESQL.equals(dbResult.getDbType())) {
            log.warn("Default datasource of the type " + dbResult.getDbType() + " is not supported!");
            return null;
        }

        StartupLogCompressor compressor = new StartupLogCompressor(
                (launchMode.isTest() ? "(test) " : "") + "Database Import Dev Services Starting:", consoleInstalledBuildItem,
                loggingSetupBuildItem);

        try {

            // update system properties for local connection for integration tests
            setupDbResultSystemProperties(dbResult);
            devServiceDatasource.getNamedDatasources().forEach((k, v) -> setupDbResultSystemProperties(v));

            if (!devServicesSharedNetworkBuildItem.isEmpty()) {

                InspectContainerResponse dbInspect = null;
                try (DockerClient dockerClient = DockerClientFactory.lazyClient()) {
                    final Optional<Container> postgresql = dockerClient.listContainersCmd().exec().stream()
                            .filter(c -> (SESSION_ID.equals(c.getLabels().get(TESTCONTAINERS_SESSION_ID_LABEL))
                                    && c.getImage().startsWith(configuration.dbImageName)))
                            .findAny();
                    if (postgresql.isPresent()) {
                        Container dbContainer = postgresql.get();
                        dbInspect = dockerClient.inspectContainerCmd(dbContainer.getId()).exec();
                    }
                }

                if (dbInspect != null) {

                    // db local port
                    Ports.Binding[] binding = dbInspect.getNetworkSettings().getPorts().getBindings()
                            .get(new ExposedPort(POSTGRESQL_PORT));
                    if (binding != null) {
                        String port = binding[0].getHostPortSpec();
                        String url = dbResult.getConfigProperties().get(DATASOURCE_DEFAULT_URL);
                        url = url.substring(url.lastIndexOf("/"));
                        url = String.format("jdbc:postgresql://%s:%s%s", DockerClientFactory.instance().dockerHostIpAddress(),
                                port, url);
                        System.setProperty(DATASOURCE_DEFAULT_URL, url);
                        log.info("Local jdbc {}={}", DATASOURCE_DEFAULT_URL, url);
                    }
                } else {
                    log.error("No postgres image found.");
                }
            }
            compressor.close();
        } catch (Throwable t) {
            compressor.closeAndDumpCaptured();
            throw new RuntimeException(t);
        }

        return null;
    }

    private static void setupDbResultSystemProperties(DevServicesDatasourceResultBuildItem.DbResult dbResult) {
        if (dbResult != null) {
            dbResult.getConfigProperties().forEach(System::setProperty);
        }
    }

}
