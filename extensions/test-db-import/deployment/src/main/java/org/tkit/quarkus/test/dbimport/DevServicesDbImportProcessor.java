package org.tkit.quarkus.test.dbimport;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import io.quarkus.datasource.common.runtime.DatabaseKind;
import io.quarkus.datasource.deployment.spi.DevServicesDatasourceResultBuildItem;
import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.DevServicesConfigResultBuildItem;
import io.quarkus.deployment.builditem.DevServicesSharedNetworkBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;

import java.util.Map;
import java.util.Optional;

import static org.testcontainers.DockerClientFactory.SESSION_ID;
import static org.testcontainers.DockerClientFactory.TESTCONTAINERS_SESSION_ID_LABEL;

public class DevServicesDbImportProcessor {

    private static final Logger log = LoggerFactory.getLogger(DevServicesDbImportProcessor.class);

    private static final String DATASOURCE_DEFAULT_URL = "quarkus.datasource.jdbc.url";
    private static final Integer POSTGRESQL_PORT = 5432;

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = GlobalDevServicesConfig.Enabled.class)
    public DevServicesDbImportBuildItem startDbImportDevService(
            DevServicesDatasourceResultBuildItem devServiceDatasource,
            LaunchModeBuildItem launchMode,
            DbImportBuildTimeConfig dbImportClientBuildTimeConfig,
            Optional<DevServicesSharedNetworkBuildItem> devServicesSharedNetworkBuildItem,
            BuildProducer<DevServicesConfigResultBuildItem> devServicePropertiesProducer) {
       //TODO: LoggingSetupBuildItem loggingSetupBuildItem, GlobalDevServicesConfig devServicesConfig

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

        InspectContainerResponse dbInspect = null;
        final Optional<Container> postgresql = DockerClientFactory.lazyClient().listContainersCmd().exec().stream()
                .filter(c -> (SESSION_ID.equals(c.getLabels().get(TESTCONTAINERS_SESSION_ID_LABEL))
                        && c.getImage().startsWith(configuration.dbImageName)))
                .findAny();
        if (postgresql.isPresent()) {
            Container dbContainer = postgresql.get();
            dbInspect = DockerClientFactory.lazyClient().inspectContainerCmd(dbContainer.getId()).exec();
        }

        // update system properties for local connection
        setupDbResultSystemProperties(dbResult);
        devServiceDatasource.getNamedDatasources().forEach((k, v) -> setupDbResultSystemProperties(v));

        if (devServicesSharedNetworkBuildItem.isPresent()) {
            if (dbInspect != null ) {
                Ports.Binding[] binding = dbInspect.getNetworkSettings().getPorts().getBindings().get(new ExposedPort(POSTGRESQL_PORT));
                if (binding != null) {
                    String port = binding[0].getHostPortSpec();
                    String url = dbResult.getConfigProperties().get(DATASOURCE_DEFAULT_URL);
                    url = url.substring(url.lastIndexOf("/"));
                    url = String.format("jdbc:postgresql://%s:%s%s", DockerClientFactory.instance().dockerHostIpAddress(),port,url);
                    System.setProperty(DATASOURCE_DEFAULT_URL, url);
                    log.info("Local jdbc {}={}", DATASOURCE_DEFAULT_URL, url);
                }
            }
        }
        return null;
    }

    private static void setupDbResultSystemProperties(DevServicesDatasourceResultBuildItem.DbResult dbResult) {
        if (dbResult != null) {
            dbResult.getConfigProperties().forEach(System::setProperty);
        }
    }

}
