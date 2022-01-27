package org.tkit.quarkus.test.dbimport;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.datasource.common.runtime.DatabaseKind;
import io.quarkus.datasource.deployment.spi.DevServicesDatasourceResultBuildItem;
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
import org.testcontainers.DockerClientFactory;
import org.testcontainers.Testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DevServicesDbImportProcessor {

    private static final Logger log = LoggerFactory.getLogger(DevServicesDbImportProcessor.class);

    private static final int DB_IMPORT_PORT = 8080;

    private static final String DEV_SERVICE_LABEL = "quarkus-dev-service-tkit-db-import";

    private static final ContainerLocator containerLocator = new ContainerLocator(DEV_SERVICE_LABEL, DB_IMPORT_PORT);

    private final IsDockerWorking isDockerWorking = new IsDockerWorking(true);

    private static final String PROP_DB_IMPORT_URL = "quarkus.tkit.db-import.url";
    private static final String DATASOURCE_DEFAULT_URL = "quarkus.datasource.jdbc.url";
    private static final Integer POSTGRESQL_PORT = 5432;

    static volatile boolean first = true;
    static volatile Closeable closeable;
    static volatile DbImportDevServiceCfg cfg;

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = GlobalDevServicesConfig.Enabled.class)
    public DevServicesDbImportBuildItem startDbImportDevService(
            DevServicesDatasourceResultBuildItem devServiceDatasource,
            LaunchModeBuildItem launchMode,
            DbImportBuildTimeConfig dbImportClientBuildTimeConfig,
            Optional<DevServicesSharedNetworkBuildItem> devServicesSharedNetworkBuildItem,
            BuildProducer<DevServicesConfigResultBuildItem> devServicePropertiesProducer) {
       //TODO: LoggingSetupBuildItem loggingSetupBuildItem, GlobalDevServicesConfig devServicesConfig

        // create dev service config
        DbImportDevServiceCfg configuration = getConfiguration(dbImportClientBuildTimeConfig);
        if (!configuration.devServicesEnabled) {
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
        String dbIp = null;
        final Optional<Container> postgresql = DockerClientFactory.lazyClient().listContainersCmd().exec().stream()
                .filter(c -> (Boolean.TRUE.toString().equals(c.getLabels().get("org.testcontainers"))
                        && c.getImage().startsWith(configuration.dbImageName)))
                .findAny();
        if (postgresql.isPresent()) {
            Container dbContainer = postgresql.get();
            dbInspect = DockerClientFactory.lazyClient().inspectContainerCmd(dbContainer.getId()).exec();
            dbIp = dbInspect.getNetworkSettings().getLinkLocalIPv6Address();
        }

        // update system properties for local connection
        if (configuration.local) {
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
        }

        if (!configuration.remote) {
            // explicitly disabled remote db-import
            log.debug("Not starting remote dev services for db-import, as it has been disabled in the config.");
            return null;
        }

        if (closeable != null) {
            boolean shouldShutdownTheBroker = !configuration.equals(cfg);
            if (!shouldShutdownTheBroker) {
                return null;
            }
            shutdownDbImport();
            cfg = null;
        }

        DbImport server = startDbImport(configuration, launchMode, devServicesSharedNetworkBuildItem.isPresent(),
                dbResult, dbIp);
        DevServicesDbImportBuildItem serverBuildItem = null;
        if (server != null) {
            closeable = server.getCloseable();
            String url = server.url;
            devServicePropertiesProducer.produce(new DevServicesConfigResultBuildItem(PROP_DB_IMPORT_URL, url));
            System.setProperty(PROP_DB_IMPORT_URL, url);
            serverBuildItem = new DevServicesDbImportBuildItem(url);
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

    private static void setupDbResultSystemProperties(DevServicesDatasourceResultBuildItem.DbResult dbResult) {
        if (dbResult != null) {
            dbResult.getConfigProperties().forEach(System::setProperty);
        }
    }

    private DbImport startDbImport(DbImportDevServiceCfg config, LaunchModeBuildItem launchMode, boolean useSharedNetwork,
                                   DevServicesDatasourceResultBuildItem.DbResult dbResult, String dbip) {



        if (!isDockerWorking.getAsBoolean()) {
            log.warn("Docker isn't working, please configure the datasource configuration.");
            return null;
        }

        final Optional<ContainerAddress> maybeContainerAddress = containerLocator.locateContainer(config.serviceName,
                config.shared,
                launchMode.getLaunchMode());

        // Starting the broker
        final Supplier<DbImport> defaultDbImportSupplier = () -> {
            DbImportContainer container = new DbImportContainer(
                    DockerImageName.parse(config.imageName),
                    config.fixedExposedPort,
                    launchMode.getLaunchMode() == LaunchMode.DEVELOPMENT ? config.serviceName : null);

            String url = dbResult.getConfigProperties().get(DATASOURCE_DEFAULT_URL);
            if (!useSharedNetwork) {
                url = url.replace(DockerClientFactory.instance().dockerHostIpAddress(), dbip);
            }

            container.withEnv("DB_URL", url)
                .withEnv("DB_USERNAME", dbResult.getConfigProperties().get("quarkus.datasource.username"))
                .withEnv("DB_PASSWORD", dbResult.getConfigProperties().get("quarkus.datasource.password"));

            container.start();

            return new DbImport(container.getUrl(), container::close);
        };

        return maybeContainerAddress.map(containerAddress -> new DbImport(containerAddress.getUrl(), null))
                .orElseGet(defaultDbImportSupplier);
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

    private DbImportDevServiceCfg getConfiguration(DbImportBuildTimeConfig cfg) {
        DbImportDevServicesBuildTimeConfig devServicesConfig = cfg.devservices;
        return new DbImportDevServiceCfg(devServicesConfig);
    }

    private static final class DbImportDevServiceCfg {
        private final boolean devServicesEnabled;
        private final String imageName;
        private final Integer fixedExposedPort;
        private final boolean shared;
        private final String serviceName;
        private final boolean local;
        private final boolean remote;
        private final String dbImageName;

        public DbImportDevServiceCfg(DbImportDevServicesBuildTimeConfig config) {
            this.devServicesEnabled = config.enabled.orElse(true);
            this.imageName = config.imageName;
            this.fixedExposedPort = config.port.orElse(0);
            this.shared = config.shared;
            this.serviceName = config.serviceName;
            this.local = config.local;
            this.remote = config.remote;
            this.dbImageName = config.dbImageName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DbImportDevServiceCfg that = (DbImportDevServiceCfg) o;
            return devServicesEnabled == that.devServicesEnabled && Objects.equals(imageName, that.imageName)
                    && Objects.equals(fixedExposedPort, that.fixedExposedPort);
        }

        @Override
        public int hashCode() {
            return Objects.hash(devServicesEnabled, imageName, fixedExposedPort);
        }
    }

    private static class DbImport {
        private final String url;
        private final Closeable closeable;

        public DbImport(String url, Closeable closeable) {
            this.url = url;
            this.closeable = closeable;
        }

        public boolean isOwner() {
            return closeable != null;
        }

        public String getUrl() {
            return url;
        }

        public Closeable getCloseable() {
            return closeable;
        }
    }

    private static final class DbImportContainer extends GenericContainer<DbImportContainer> {

        private final int port;

        private DbImportContainer(DockerImageName dockerImageName, int fixedExposedPort, String serviceName) {
            super(dockerImageName);
            this.port = fixedExposedPort;
            withNetwork(Network.SHARED);


            withExposedPorts(DB_IMPORT_PORT);
            if (serviceName != null) { // Only adds the label in dev mode.
                withLabel(DEV_SERVICE_LABEL, serviceName);
            }
            withLogConsumer(new Slf4jLogConsumer(log));
            waitingFor(Wait.forLogMessage(".*Installed features:.*", 1));
        }

        @Override
        protected void configure() {
            super.configure();
            if (port > 0) {
                addFixedExposedPort(port, DB_IMPORT_PORT);
            }
        }

        public String getUrl() {
            return String.format("http://%s:%d", getHost(), getMappedPort(DB_IMPORT_PORT));
        }

    }
}
