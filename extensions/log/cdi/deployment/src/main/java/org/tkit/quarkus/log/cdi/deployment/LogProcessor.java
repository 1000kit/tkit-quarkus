package org.tkit.quarkus.log.cdi.deployment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import io.quarkus.runtime.RuntimeValue;
import org.jboss.jandex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.log.cdi.*;
import org.tkit.quarkus.log.cdi.interceptor.LogParamValueService;
import org.tkit.quarkus.log.cdi.runtime.LogRuntimeConfig;

import io.quarkus.arc.deployment.*;
import io.quarkus.arc.processor.AnnotationStore;
import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.arc.processor.BuildExtension;
import io.quarkus.deployment.annotations.*;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.RuntimeConfigSetupCompleteBuildItem;

public class LogProcessor {

    static final String FEATURE_NAME = "tkit-log-cdi";

    private static final Logger log = LoggerFactory.getLogger(LogProcessor.class);

    private static final DotName LOG_SERVICE = DotName.createSimple(LogService.class.getName());

    private static final LogService DEFAULT_LOG_SERVICE = LogServiceImpl.class.getAnnotation(LogService.class);

    private static final Set<String> EXCLUDE_METHODS = Arrays.stream(Object.class.getMethods()).map(Method::getName)
            .collect(Collectors.toSet());

    @BuildStep
    void build(BuildProducer<FeatureBuildItem> feature) {
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    @Consume(RuntimeConfigSetupCompleteBuildItem.class)
    void configureRuntimeProperties(LogRecorder recorder, RuntimeValue<LogRuntimeConfig> config, ServiceBuildItem items) {
        recorder.init(items.values, config);
    }

    @BuildStep
    void services(BeanDiscoveryFinishedBuildItem beanDiscoveryFinishedBuildItem,
            BeanRegistrationPhaseBuildItem beanRegistrationPhase,
            BuildProducer<ServiceBuildItem> producer) {

        ServiceValue values = new ServiceValue();

        AnnotationStore annotationStore = beanRegistrationPhase.getContext().get(BuildExtension.Key.ANNOTATION_STORE);

        for (BeanInfo bean : beanRegistrationPhase.getContext().beans().classBeans()) {
            ClassInfo ci = bean.getImplClazz();

            AnnotationInstance ano = ci.declaredAnnotation(LOG_SERVICE);
            if (ano == null) {
                ano = annotationStore.getAnnotation(ci, LOG_SERVICE);
            }

            ServiceValue.ClassItem classItem = null;
            if (ano != null) {
                LogService classLogService = createLogService(ano);
                classItem = values.getOrCreate(ci.name().toString());
                classItem.config = ServiceValue.createConfig();
                updateValue(classLogService, classItem.config);
            }

            try {
                Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(ci.toString());
                for (Method method : clazz.getMethods()) {
                    if (!Modifier.isPublic(method.getModifiers()) || EXCLUDE_METHODS.contains(method.getName())) {
                        continue;
                    }

                    LogService methodLogService = method.getAnnotation(LogService.class);
                    if (methodLogService != null) {
                        if (classItem == null) {
                            classItem = values.getOrCreate(ci.name().toString());
                        }
                    }

                    if (methodLogService == null && ano == null) {
                        continue;
                    }

                    ServiceValue.MethodItem methodItem = classItem.getOrCreate(method.getName());
                    if (methodLogService != null) {
                        methodItem.config = ServiceValue.createConfig();
                        updateValue(methodLogService, methodItem.config);
                    }

                    // check return LogExclude
                    LogExclude returnLogExclude = method.getAnnotation(LogExclude.class);
                    if (returnLogExclude != null) {
                        methodItem.returnMask = returnLogExclude.mask();
                    }

                    // check parameter LogExclude
                    Annotation[][] annotations = method.getParameterAnnotations();
                    for (int i = 0; i < annotations.length; i++) {
                        Annotation[] annotationList = annotations[i];
                        if (annotationList != null && annotationList.length > 0) {
                            for (Annotation annotation : annotationList) {
                                if (annotation instanceof LogExclude) {
                                    LogExclude ea = (LogExclude) annotation;
                                    if (methodItem.params == null) {
                                        methodItem.params = new HashMap<>();
                                    }
                                    String mask = ea.mask();
                                    if (mask.isBlank()) {
                                        mask = null;
                                    }
                                    methodItem.params.put((short) i, mask);
                                }
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException cnfe) {
                // ignore
                log.debug("Missing class in current class-loader. Class: {}", cnfe.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException("Error load class " + ci, ex);
            }
        }
        values.updateMapping();
        producer.produce(new ServiceBuildItem(values));
    }

    private static void updateValue(LogService ano, ServiceValue.LogServiceAnnotation item) {
        item.log = ano.log();
        item.stacktrace = ano.stacktrace();
        item.logStart = ano.logStart();
        String configKey = ano.configKey();
        if (!configKey.isBlank()) {
            item.configKey = configKey;
        }
    }

    private static LogService createLogService(AnnotationInstance annotationInstance) {

        return new LogService() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return DEFAULT_LOG_SERVICE.annotationType();
            }

            @Override
            public String configKey() {
                AnnotationValue av = annotationInstance.value("configKey");
                if (av != null) {
                    return av.asString();
                }
                return DEFAULT_LOG_SERVICE.configKey();
            }

            @Override
            public boolean log() {
                AnnotationValue av = annotationInstance.value("log");
                if (av != null) {
                    return av.asBoolean();
                }
                return DEFAULT_LOG_SERVICE.log();
            }

            @Override
            public boolean stacktrace() {
                AnnotationValue av = annotationInstance.value("stacktrace");
                if (av != null) {
                    return av.asBoolean();
                }
                return DEFAULT_LOG_SERVICE.stacktrace();
            }

            @Override
            public LogService.Log logStart() {
                AnnotationValue av = annotationInstance.value("logStart");
                if (av != null) {
                    return LogService.Log.valueOf(av.asEnum());
                }
                return DEFAULT_LOG_SERVICE.logStart();
            }
        };
    }

    @LogService
    private static class LogServiceImpl {
    }

}
