package org.tkit.quarkus.dataimport.deployment;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;
import org.jboss.logging.Logger;
import org.tkit.quarkus.dataimport.DataImport;
import org.tkit.quarkus.dataimport.DataImportRecorder;
import org.tkit.quarkus.dataimport.DataImportService;

import io.quarkus.agroal.spi.JdbcDataSourceSchemaReadyBuildItem;
import io.quarkus.arc.deployment.AutoAddScopeBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanDiscoveryFinishedBuildItem;
import io.quarkus.arc.deployment.SyntheticBeansRuntimeInitBuildItem;
import io.quarkus.arc.deployment.TransformedAnnotationsBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem;
import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.arc.processor.BuiltinScope;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Consume;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.hibernate.orm.deployment.PersistenceProviderSetUpBuildItem;

class QuarkusImportProcessor {

    private static final Logger LOGGER = Logger.getLogger(QuarkusImportProcessor.class);

    private static final String FEATURE = "tkit-quarkus-import";

    private static final DotName IMPORT_DATA_ANNOTATION = DotName.createSimple(DataImport.class.getName());
    private static final DotName IMPORT_DATA_SERVICE = DotName.createSimple(DataImportService.class.getName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AutoAddScopeBuildItem autoAddScope() {
        return AutoAddScopeBuildItem.builder().containsAnnotations(IMPORT_DATA_ANNOTATION)
                .defaultScope(BuiltinScope.SINGLETON)
                .reason("Found import data methods").build();
    }

    @BuildStep
    void collectImportDataMethods(BeanArchiveIndexBuildItem beanArchives, BeanDiscoveryFinishedBuildItem beanDiscovery,
            TransformedAnnotationsBuildItem transformedAnnotations,
            BuildProducer<ValidationPhaseBuildItem.ValidationErrorBuildItem> validationErrors,
            BuildProducer<DataImportBeanInfo> importDataBeans) {

        List<Throwable> errors = new ArrayList<>();

        // We need to collect all import data beans annotated with @ImportData first
        for (BeanInfo bean : beanDiscovery.beanStream().classBeans()) {
            AnnotationTarget at = bean.getTarget().orElse(null);
            if (at == null) {
                continue;
            }
            AnnotationInstance importData = transformedAnnotations.getAnnotation(at, IMPORT_DATA_ANNOTATION);
            if (importData == null) {
                continue;
            }
            if (!validateImportDataClass(at)) {
                errors.add(new IllegalStateException("@ImportData bean must implements interface: " + IMPORT_DATA_SERVICE));
            }

            importDataBeans.produce(new DataImportBeanInfo(bean.getIdentifier(), importData.value().asString()));
            LOGGER.debugf("Found import data declared on %s", bean);
        }

        if (!errors.isEmpty()) {
            validationErrors.produce(new ValidationPhaseBuildItem.ValidationErrorBuildItem(errors));
        }
    }

    private boolean validateImportDataClass(AnnotationTarget at) {
        for (DotName i : at.asClass().interfaceNames()) {
            if (i.equals(IMPORT_DATA_SERVICE)) {
                return true;
            }
        }
        return false;
    }

    @BuildStep
    public List<UnremovableBeanBuildItem> unRemovableBeans() {
        // Beans annotated with @ImportData should never be removed
        return Collections.singletonList(new UnremovableBeanBuildItem(
                new UnremovableBeanBuildItem.BeanClassAnnotationExclusion(IMPORT_DATA_ANNOTATION)));
    }

    @BuildStep
    @Record(RUNTIME_INIT)
    @Consume(SyntheticBeansRuntimeInitBuildItem.class)
    public void build(@SuppressWarnings("unused") List<PersistenceProviderSetUpBuildItem> persistenceUnitsStarted,
            @SuppressWarnings("unused") List<JdbcDataSourceSchemaReadyBuildItem> schemaReadyBuildItems,
            DataImportRecorder recorder,
            List<DataImportBeanInfo> importDataBeans) {

        Map<String, String> items = new HashMap<>();
        for (DataImportBeanInfo bean : importDataBeans) {
            items.put(bean.getKey(), bean.getBean());
        }
        recorder.createContext(items);
    }
}
