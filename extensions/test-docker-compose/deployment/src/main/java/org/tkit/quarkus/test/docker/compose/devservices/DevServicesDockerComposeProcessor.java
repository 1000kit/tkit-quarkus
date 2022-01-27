package org.tkit.quarkus.test.docker.compose.devservices;

import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.IsDockerWorking;
import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.DevServicesConfigResultBuildItem;
import io.quarkus.deployment.builditem.DevServicesSharedNetworkBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;
import io.quarkus.devservices.common.ContainerAddress;
import io.quarkus.devservices.common.ContainerLocator;
import io.quarkus.runtime.LaunchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.Base58;
import org.testcontainers.utility.DockerImageName;

import java.io.Closeable;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class DevServicesDockerComposeProcessor {

    private static final Logger log = LoggerFactory.getLogger(DevServicesDockerComposeProcessor.class);

    private static final String DEV_SERVICE_LABEL = "quarkus-dev-service-tkit-docker-compose";

    private final IsDockerWorking isDockerWorking = new IsDockerWorking(true);

    static volatile boolean first = true;
    static volatile Closeable closeable;
    static volatile DockerComposeDevServiceCfg cfg;

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = GlobalDevServicesConfig.Enabled.class)
    public DevServicesDockerComposeBuildItem startDockerComposeDevService(
            LaunchModeBuildItem launchMode,
            DockerComposeBuildTimeConfig dbImportClientBuildTimeConfig,
            Optional<DevServicesSharedNetworkBuildItem> devServicesSharedNetworkBuildItem,
            BuildProducer<DevServicesConfigResultBuildItem> devServicePropertiesProducer) {
       //TODO: LoggingSetupBuildItem loggingSetupBuildItem, GlobalDevServicesConfig devServicesConfig

        DockerComposeDevServiceCfg configuration = getConfiguration(dbImportClientBuildTimeConfig);

        if (closeable != null) {
            boolean shouldShutdownTheBroker = !configuration.equals(cfg);
            if (!shouldShutdownTheBroker) {
                return null;
            }
            shutdownDbImport();
            cfg = null;
        }

        DevServicesDockerCompose server = startDockerCompose(configuration, launchMode, devServicesSharedNetworkBuildItem.isPresent());
        DevServicesDockerComposeBuildItem serverBuildItem = null;
        if (server != null) {
            closeable = server.getCloseable();
//            devServicePropertiesProducer.produce(new DevServicesConfigResultBuildItem("quarkus.tkit.db-import.url", url));
            serverBuildItem = new DevServicesDockerComposeBuildItem(null);
        }

        // Configure the watch dog
        if (first) {
            first = false;
            Runnable closeTask = () -> {
                if (closeable != null) {
                    shutdownDbImport();
                }
                first = true;
                closeable = null;
                cfg = null;
            };
            QuarkusClassLoader cl = (QuarkusClassLoader) Thread.currentThread().getContextClassLoader();
            ((QuarkusClassLoader) cl.parent()).addCloseTask(closeTask);
        }
        cfg = configuration;
        return serverBuildItem;
    }

    private DevServicesDockerCompose startDockerCompose(DockerComposeDevServiceCfg config, LaunchModeBuildItem launchMode, boolean useSharedNetwork) {

        if (!config.devServicesEnabled) {
            // explicitly disabled
            log.debug("Not starting dev services for Kafka, as it has been disabled in the config.");
            return null;
        }

        if (!isDockerWorking.getAsBoolean()) {
            log.warn("Docker isn't working, please configure the datasource configuration.");
            return null;
        }

        return null;
//        final Optional<ContainerAddress> maybeContainerAddress = containerLocator.locateContainer(config.serviceName,
//                config.shared,
//                launchMode.getLaunchMode());


//        // Starting the broker
//        final Supplier<DbImport> defaultDbImportSupplier = () -> {
//            DbImportContainer container = new DbImportContainer(
//                    DockerImageName.parse(config.imageName),
//                    config.fixedExposedPort,
//                    launchMode.getLaunchMode() == LaunchMode.DEVELOPMENT ? config.serviceName : null,
//                    useSharedNetwork);
////            container.withEnv("DB_URL", dbresult.getConfigProperties().get("quarkus.datasource.jdbc.url"));
//            container.withEnv("DB_URL", "jdbc:postgresql://postgres:5432/default?loggerLevel=OFF")
//                .withEnv("DB_USERNAME", dbresult.getConfigProperties().get("quarkus.datasource.username"))
//                .withEnv("DB_PASSWORD", dbresult.getConfigProperties().get("quarkus.datasource.password"));
//
//            container.start();
//
//            return new DevServicesDockerCompose(container.getUrl(), container::close);
//        };
//
//        return maybeContainerAddress.map(containerAddress -> new DevServicesDockerCompose(containerAddress.getUrl(), null))
//                .orElseGet(defaultDbImportSupplier);
    }

    private void shutdownDbImport() {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable e) {
                log.error("Failed to stop the db-import", e);
            } finally {
                closeable = null;
            }
        }
    }

    private DockerComposeDevServiceCfg getConfiguration(DockerComposeBuildTimeConfig cfg) {
        DevServicesDockerComposeBuildTimeConfig devServicesConfig = cfg.devservices;
        return new DockerComposeDevServiceCfg(devServicesConfig);
    }

    private static final class DockerComposeDevServiceCfg {
        private final boolean devServicesEnabled;
        private final boolean shared;
        private final String dockerComposeFile;

        public DockerComposeDevServiceCfg(DevServicesDockerComposeBuildTimeConfig config) {
            this.devServicesEnabled = config.enabled.orElse(true);
            this.shared = config.shared;
            this.dockerComposeFile = config.dockerComposeFile;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DockerComposeDevServiceCfg that = (DockerComposeDevServiceCfg) o;
            return devServicesEnabled == that.devServicesEnabled && Objects.equals(dockerComposeFile, that.dockerComposeFile);
        }

        @Override
        public int hashCode() {
            return Objects.hash(devServicesEnabled, dockerComposeFile);
        }
    }

    private static class DevServicesDockerCompose {
        private final String dockerComposeFile;
        private final Closeable closeable;

        public DevServicesDockerCompose(String dockerComposeFile, Closeable closeable) {
            this.dockerComposeFile = dockerComposeFile;
            this.closeable = closeable;
        }

        public boolean isOwner() {
            return closeable != null;
        }

        public String getDockerComposeFile() {
            return dockerComposeFile;
        }

        public Closeable getCloseable() {
            return closeable;
        }
    }

    private static final class DbImportContainer extends GenericContainer<DbImportContainer> {

//        private final int port;
//        private final boolean useSharedNetwork;
        private String hostName = null;

        private DbImportContainer(DockerImageName dockerImageName, int fixedExposedPort, String serviceName,
                                       boolean useSharedNetwork) {
            super(dockerImageName);
//            this.port = fixedExposedPort;
//            this.useSharedNetwork = useSharedNetwork;
//            withNetwork(Network.SHARED);
//
//            if (useSharedNetwork) {
//                hostName = "db-import-" + Base58.randomString(5);
//                setNetworkAliases(Collections.singletonList(hostName));
//            } else {
//                withExposedPorts(DB_IMPORT_PORT);
//            }
//            if (serviceName != null) { // Only adds the label in dev mode.
//                withLabel(DEV_SERVICE_LABEL, serviceName);
//            }
//            withLogConsumer(new Slf4jLogConsumer(log2));
//            waitingFor(Wait.forLogMessage(".*Installed features:.*", 1));
        }

        @Override
        protected void configure() {
            super.configure();
//            if ((port > 0) && !useSharedNetwork) {
//                addFixedExposedPort(port, DB_IMPORT_PORT);
//            }
        }
//
//        public String getUrl() {
//            return String.format("http://%s:%d", getHostToUse(), getPortToUse());
//        }
//
//        private String getHostToUse() {
//            return useSharedNetwork ? hostName : getHost();
//        }
//
//        private int getPortToUse() {
//            return useSharedNetwork ? DB_IMPORT_PORT : getMappedPort(DB_IMPORT_PORT);
//        }
    }
}
