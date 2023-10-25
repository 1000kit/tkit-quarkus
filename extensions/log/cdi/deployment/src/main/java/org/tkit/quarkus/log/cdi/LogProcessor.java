package org.tkit.quarkus.log.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.jandex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.log.cdi.interceptor.LogParamValueService;
import org.tkit.quarkus.log.cdi.runtime.LogRuntimeConfig;

import io.quarkus.arc.deployment.*;
import io.quarkus.arc.processor.AnnotationStore;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.arc.processor.BuildExtension;
import io.quarkus.deployment.annotations.*;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ConfigPropertiesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.RuntimeConfigSetupCompleteBuildItem;

public class LogProcessor {

    static final String FEATURE_NAME = "tkit-log-cdi";

    private static final Logger log = LoggerFactory.getLogger(LogProcessor.class);

    private static final String LOG_BUILDER_SERVICE = LogParamValueService.class.getName();

    private static final DotName LOG_SERVICE = DotName.createSimple(LogService.class.getName());

    private static final LogService DEFAULT_LOG_SERVICE = LogServiceImpl.class.getAnnotation(LogService.class);

    private static final Set<String> EXCLUDE_METHODS = Arrays.stream(Object.class.getMethods()).map(Method::getName)
            .collect(Collectors.toSet());

    private static final List<DotName> ANNOTATION_DOT_NAMES = List.of(
            DotName.createSimple(ApplicationScoped.class.getName()),
            DotName.createSimple(Singleton.class.getName()),
            DotName.createSimple(RequestScoped.class.getName()));

    @BuildStep
    void build(BuildProducer<FeatureBuildItem> feature) {
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    @Consume(RuntimeConfigSetupCompleteBuildItem.class)
    void configureRuntimeProperties(LogRecorder recorder, LogRuntimeConfig config, ServiceBuildItem items) {
        recorder.init(items.values, config);
    }

    @BuildStep
    void services(BeanDiscoveryFinishedBuildItem beanDiscoveryFinishedBuildItem,
            BeanRegistrationPhaseBuildItem beanRegistrationPhase,
            LogBuildTimeConfig buildConfig,
            BuildProducer<ServiceBuildItem> producer) {

        ServiceValue values = new ServiceValue();

        AnnotationStore annotationStore = beanRegistrationPhase.getContext().get(BuildExtension.Key.ANNOTATION_STORE);

        for (BeanInfo bean : beanRegistrationPhase.getContext().beans().classBeans()) {
            ClassInfo ci = bean.getImplClazz();

            AnnotationInstance ano = ci.classAnnotation(LOG_SERVICE);
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
        String configKey = ano.configKey();
        if (!configKey.isBlank()) {
            item.configKey = configKey;
        }
    }

    /**
     * Autodiscovery
     */
    @BuildStep
    public AnnotationsTransformerBuildItem interceptorBinding(LogBuildTimeConfig buildConfig) {
        if (!buildConfig.autoDiscover.enabled) {
            return null;
        }

        final Pattern ignorePattern = buildConfig.autoDiscover.ignorePattern.map(Pattern::compile).orElse(null);

        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            @Override
            public boolean appliesTo(AnnotationTarget.Kind kind) {
                return kind == AnnotationTarget.Kind.CLASS;
            }

            public void transform(TransformationContext context) {
                ClassInfo target = context.getTarget().asClass();

                // skip if annotation @LogService exists
                AnnotationInstance classLogService = target.declaredAnnotation(LOG_SERVICE);
                if (classLogService != null) {
                    return;
                }

                // skip for none Bean class
                Map<DotName, List<AnnotationInstance>> tmp = target.annotationsMap();
                Optional<DotName> dot = ANNOTATION_DOT_NAMES.stream().filter(tmp::containsKey).findFirst();
                if (dot.isEmpty()) {
                    return;
                }

                String name = target.name().toString();
                Optional<String> add = buildConfig.autoDiscover.packages.stream().filter(name::startsWith).findFirst();
                if (add.isPresent() && !LOG_BUILDER_SERVICE.equals(name) && !matchesIgnorePattern(name)) {
                    context.transform().add(LogService.class).done();
                }
            }

            private boolean matchesIgnorePattern(String name) {
                if (buildConfig.autoDiscover.ignorePattern.isEmpty()
                        || buildConfig.autoDiscover.ignorePattern.get().isBlank()) {
                    return false;
                }
                if (ignorePattern == null) {
                    return false;
                }
                boolean matches = ignorePattern.matcher(name).matches();
                if (matches) {
                    log.info(
                            "Disabling tkit logs on: {} because it matches the ignore pattern: '{}' (set via 'tkit.log.cdi.auto-discovery.ignore.pattern')",
                            name,
                            buildConfig.autoDiscover.ignorePattern);
                }
                return matches;
            }
        });
    }

    @BuildStep
    public ConfigPropertiesBuildItem validateDeprecatedConfiguration() {
        Set<String> quarkusTkitConfigNames = StreamSupport
                .stream(ConfigProvider.getConfig().getPropertyNames().spliterator(), false)
                .filter(s -> s.startsWith("quarkus.tkit")).collect(Collectors.toSet());
        if (!quarkusTkitConfigNames.isEmpty()) {
            throw new IllegalArgumentException(
                    "Configuration with prefix quarkus.tkit are deprecated : " + quarkusTkitConfigNames
                            + " . You can find new configuration mapping on https://github.com/1000kit/tkit-quarkus/tree/main/extensions/log");
        }
        return null;
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
        };
    }

    @LogService
    private static class LogServiceImpl {
    }

}
