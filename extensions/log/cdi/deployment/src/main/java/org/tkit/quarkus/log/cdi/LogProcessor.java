package org.tkit.quarkus.log.cdi;

import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.BeanDiscoveryFinishedBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.log.cdi.interceptor.LogParamValueService;
import org.tkit.quarkus.log.cdi.runtime.LogRuntimeConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Singleton;
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


public class LogProcessor {

    static final String FEATURE_NAME = "tkit-log-cdi";

    private static final Logger log = LoggerFactory.getLogger(LogProcessor.class);

    private static final String LOG_BUILDER_SERVICE = LogParamValueService.class.getName();

    private static final Set<String> EXCLUDE_METHODS = Arrays.stream(Object.class.getMethods()).map(Method::getName).collect(Collectors.toSet());

    private static final List<DotName> ANNOTATION_DOT_NAMES = List.of(
            DotName.createSimple(ApplicationScoped.class.getName()),
            DotName.createSimple(Singleton.class.getName()),
            DotName.createSimple(RequestScoped.class.getName())
    );

    @BuildStep
    void build(BuildProducer<FeatureBuildItem> feature) {
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void configureRuntimeProperties(LogRecorder recorder, LogRuntimeConfig config, ServiceBuildItem items) {
        recorder.init(items.values, config);
    }

    @BuildStep
    void restServices(BeanDiscoveryFinishedBuildItem beanDiscoveryFinishedBuildItem,
                      BuildProducer<ServiceBuildItem> producer) {

        ServiceValue values = new ServiceValue();
        beanDiscoveryFinishedBuildItem.beanStream()
                .forEach(x -> {
                    try {
                        ClassInfo ci = x.getImplClazz();
                        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(ci.name().toString());

                        ServiceValue.ClassItem classItem = null;
                        LogService classLogService = clazz.getAnnotation(LogService.class);
                        if (classLogService != null) {
                            classItem = values.getOrCreate(ci.name().toString());
                            classItem.config = ServiceValue.createConfig();
                            updateValue(classLogService, classItem.config);
                        }

                        for (Method method : clazz.getMethods()) {
                            if (Modifier.isPublic(method.getModifiers()) && !EXCLUDE_METHODS.contains(method.getName())) {

                                LogService methodLogService = method.getAnnotation(LogService.class);
                                if (methodLogService != null) {
                                    if (classItem == null) {
                                        classItem = values.getOrCreate(ci.name().toString());
                                    }
                                }

                                if (methodLogService != null || classLogService != null) {

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
                                    for (int i=0; i<annotations.length; i++) {
                                        Annotation[] annotationList = annotations[i];
                                        if (annotationList != null && annotationList.length > 0) {
                                            for (Annotation annotation : annotationList) {
                                                if (annotation instanceof LogExclude) {
                                                    LogExclude e = (LogExclude) annotation;
                                                    if (methodItem.params == null) {
                                                        methodItem.params = new HashMap<>();
                                                    }
                                                    String mask = e.mask();
                                                    if (mask.isBlank()) {
                                                        mask = null;
                                                    }
                                                    methodItem.params.put((short) i, mask);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException("Error load class " + x.getImplClazz(), ex);
                    }
                });
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
                Map<DotName, List<AnnotationInstance>> tmp = target.annotations();
                Optional<DotName> dot = ANNOTATION_DOT_NAMES.stream().filter(tmp::containsKey).findFirst();
                if (dot.isPresent()) {
                    String name = target.name().toString();
                    Optional<String> add = buildConfig.autoDiscover.packages.stream().filter(name::startsWith).findFirst();
                    if (add.isPresent() && !LOG_BUILDER_SERVICE.equals(name) && !matchesIgnorePattern(name)) {
                        context.transform().add(LogService.class).done();
                    }
                }
            }

            private boolean matchesIgnorePattern(String name) {
                if (buildConfig.autoDiscover.ignorePattern.isEmpty() || buildConfig.autoDiscover.ignorePattern.get().isBlank()) {
                    return false;
                }
                if (ignorePattern == null) {
                    return false;
                }
                boolean matches = ignorePattern.matcher(name).matches();
                if (matches) {
                    log.info("Disabling tkit logs on: {} because it matches the ignore pattern: '{}' (set via 'tkit.log.ignore.pattern')", name,
                            buildConfig.autoDiscover.ignorePattern);
                }
                return matches;
            }
        });
    }

}

