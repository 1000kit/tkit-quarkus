package org.tkit.quarkus.rs.context.deployment;

import java.util.stream.Collectors;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.MethodInfo;
import org.tkit.quarkus.rs.context.runtime.RestContextTenantRecorder;
import org.tkit.quarkus.rs.context.runtime.TenantAnnotationData;
import org.tkit.quarkus.rs.context.tenant.TenantExclude;

import io.quarkus.arc.deployment.BeanDiscoveryFinishedBuildItem;
import io.quarkus.arc.deployment.BeanRegistrationPhaseBuildItem;
import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.deployment.annotations.*;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.RuntimeConfigSetupCompleteBuildItem;

public class RestContextProcessor {

    private static final DotName TENANT_EXCLUDE = DotName.createSimple(TenantExclude.class.getName());
    static final String FEATURE_NAME = "tkit-rest-context";

    @BuildStep
    void build(BuildProducer<FeatureBuildItem> feature) {
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    @Consume(RuntimeConfigSetupCompleteBuildItem.class)
    public void restContextInit(RestContextTenantRecorder recorder, AnnotationDataBuildItem dataBuildItem) {
        recorder.init(dataBuildItem.data);
    }

    @BuildStep
    void services(BeanDiscoveryFinishedBuildItem beanDiscoveryFinishedBuildItem,
            BeanRegistrationPhaseBuildItem beanRegistrationPhase,
            RestContextBuildConfig buildConfig,
            BuildProducer<AnnotationDataBuildItem> producer) {

        if (!buildConfig.build.tenant.enabled) {
            return;
        }

        TenantAnnotationData tenant = new TenantAnnotationData();

        for (BeanInfo bean : beanRegistrationPhase.getContext().beans().classBeans()) {
            ClassInfo ci = bean.getImplClazz();

            // check @TenantExclude for class
            if (ci.hasDeclaredAnnotation(TENANT_EXCLUDE)) {
                tenant.addExcludeClass(ci.name().toString());
                continue;
            }

            // check @TenantExclude for class methods
            for (MethodInfo methodInfo : ci.methods()) {
                if (methodInfo.hasAnnotation(TENANT_EXCLUDE)) {

                    var params = "";
                    if (!methodInfo.parameterTypes().isEmpty()) {
                        params = methodInfo.parameterTypes()
                                .stream()
                                .map(org.jboss.jandex.Type::toString)
                                .collect(Collectors.joining());
                    }
                    tenant.addExcludeMethod(ci.name().toString(), methodInfo.name(), params);
                }
            }
        }

        producer.produce(new AnnotationDataBuildItem(tenant));
    }
}
