package org.tkit.quarkus.log.rs;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import org.jboss.jandex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanDiscoveryFinishedBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class RestLogProcessor {

    static final String FEATURE_NAME = "tkit-log-rs";

    static final DotName ANO_REST_SERVICE = DotName.createSimple(RestService.class.getName());

    private static final Set<String> EXCLUDE_METHODS = Arrays.stream(Object.class.getMethods()).map(Method::getName)
            .collect(Collectors.toSet());

    private static final Logger log = LoggerFactory.getLogger(RestLogProcessor.class);

    @BuildStep
    void build(BuildProducer<FeatureBuildItem> feature) {
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void init(RestRecorder recorder, RestRuntimeConfig config, RestServiceBuildItem items) {
        recorder.init(config, items.value);
    }

    @BuildStep
    void restServices(BeanArchiveIndexBuildItem index,
            BeanDiscoveryFinishedBuildItem beanDiscoveryFinishedBuildItem,
            BuildProducer<RestServiceBuildItem> producer) {

        RestServiceValue values = new RestServiceValue();
        beanDiscoveryFinishedBuildItem.beanStream()
                .forEach(x -> {
                    try {
                        ClassInfo ci = x.getImplClazz();
                        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(ci.name().toString());

                        RestServiceValue.ClassItem classItem = null;
                        RestService classRestService = clazz.getAnnotation(RestService.class);
                        if (classRestService != null) {
                            classItem = values.getOrCreate(ci.name().toString());
                            classItem.config = RestServiceValue.createConfig();
                            updateValue(classRestService, classItem.config);
                        }

                        for (Method method : clazz.getMethods()) {
                            if (Modifier.isPublic(method.getModifiers()) && !EXCLUDE_METHODS.contains(method.getName())) {

                                RestService methodLogService = method.getAnnotation(RestService.class);
                                if (methodLogService != null) {
                                    if (classItem == null) {
                                        classItem = values.getOrCreate(ci.name().toString());
                                    }
                                }

                                if (methodLogService != null || classRestService != null) {

                                    RestServiceValue.MethodItem methodItem = classItem.getOrCreate(method.getName());
                                    if (methodLogService != null) {
                                        methodItem.config = RestServiceValue.createConfig();
                                        updateValue(methodLogService, methodItem.config);
                                    }
                                }
                            }
                        }

                    } catch (ClassNotFoundException cnfe) {
                        // ignore
                        log.debug("Missing class in current class-loader. Class: {}", cnfe.getMessage());
                    } catch (Exception ex) {
                        throw new RuntimeException("Error load class " + x.getImplClazz(), ex);
                    }

                });
        values.updateMapping();
        producer.produce(new RestServiceBuildItem(values));
    }

    private static void updateValue(RestService ano, RestServiceValue.RestServiceAnnotation item) {
        item.log = ano.log();
        item.payload = ano.payload();
        String configKey = ano.configKey();
        if (!configKey.isBlank()) {
            item.configKey = configKey;
        }
    }

}
