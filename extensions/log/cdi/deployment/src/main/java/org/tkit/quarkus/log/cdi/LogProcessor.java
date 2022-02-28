package org.tkit.quarkus.log.cdi;

import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.*;
import org.jboss.logging.Logger;
import org.tkit.quarkus.log.cdi.interceptor.LogParamValueService;
import org.tkit.quarkus.log.cdi.runtime.LogRuntimeConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Singleton;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;


public class LogProcessor {

    static final String FEATURE_NAME = "tkit-log-cdi";

    private static final Logger log = Logger.getLogger(LogProcessor.class);

    private static final DotName ANO_LOG_SERVICE = DotName.createSimple(LogService.class.getName());

    private static final DotName ANO_LOG_EXCLUDE = DotName.createSimple(LogExclude.class.getName());

    private static final String LOG_BUILDER_SERVICE = LogParamValueService.class.getName();

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
    void restServices(BeanArchiveIndexBuildItem index, BuildProducer<ServiceBuildItem> producer, LogBuildTimeConfig buildConfig) {
        ServiceValue values = new ServiceValue();
        IndexView view = index.getIndex();
        view.getAnnotations(ANO_LOG_SERVICE).forEach(a -> createClassServiceValue(view, a, values));
        view.getAnnotations(ANO_LOG_SERVICE).forEach(a -> createMethodServiceValue(view, a, values));
        values.updateMapping();
        view.getAnnotations(ANO_LOG_EXCLUDE).forEach(a -> createExcludeParam(view, a, values));
        producer.produce(new ServiceBuildItem(values));
    }

    private static void createExcludeParam(IndexView view, AnnotationInstance ano, ServiceValue values) {
        if (ano.target().kind() == AnnotationTarget.Kind.METHOD) {
            MethodInfo methodInfo = ano.target().asMethod();
            ServiceValue.ClassItem clazz = values.get(methodInfo.declaringClass().name().toString());
            if (clazz == null) {
                return;
            }
            ServiceValue.MethodItem method = clazz.getOrCreate(methodInfo.name(), clazz);
            method.returnMask = ano.valueWithDefault(view, "mask").asString();
        }
        if (ano.target().kind() == AnnotationTarget.Kind.METHOD_PARAMETER) {
            MethodParameterInfo methodParameterInfo = ano.target().asMethodParameter();
            MethodInfo methodInfo = methodParameterInfo.method();
            ServiceValue.ClassItem clazz = values.get(methodInfo.declaringClass().name().toString());
            if (clazz == null) {
                return;
            }
            ServiceValue.MethodItem method = clazz.getOrCreate(methodInfo.name(), clazz);
            if (method.params == null) {
                method.params = new HashMap<>();
            }
            String mask = ano.valueWithDefault(view, "mask").asString();
            if (mask.isBlank()) {
                mask = null;
            }
            method.params.put(methodParameterInfo.position(), mask);
        }
    }

    private static void createClassServiceValue(IndexView view, AnnotationInstance ano, ServiceValue values) {
        if (ano.target().kind() != AnnotationTarget.Kind.CLASS) {
            return;
        }
        ClassInfo classInfo = ano.target().asClass();
        String className = classInfo.name().toString();
        if (!Modifier.isAbstract(classInfo.flags()) && !Modifier.isInterface(classInfo.flags())) {
            ServiceValue.ClassItem clazz = values.getOrCreate(className);
            updateValue(view, ano, clazz.config);
        }
        if (Modifier.isInterface(classInfo.flags())) {
            checkSubClasses(view.getAllKnownImplementors(classInfo.name()), view, ano, values);
        } else {
            checkSubClasses(view.getAllKnownSubclasses(classInfo.name()), view, ano, values);
        }
    }

    private static void createMethodServiceValue(IndexView view, AnnotationInstance ano, ServiceValue values) {
         if (ano.target().kind() != AnnotationTarget.Kind.METHOD) {
             return;
         }

        MethodInfo methodInfo = ano.target().asMethod();
        if (!Modifier.isPublic(methodInfo.flags())) {
            return;
        }

        String methodName = methodInfo.name();
        ClassInfo classInfo = methodInfo.declaringClass();
        String className = classInfo.name().toString();
        if (!Modifier.isAbstract(classInfo.flags()) && !Modifier.isInterface(classInfo.flags())) {
            ServiceValue.ClassItem clazz = values.getOrCreate(className, ServiceValue.DEFAULT_ANO);
            ServiceValue.MethodItem item = clazz.getOrCreate(methodName);
            updateValue(view, ano, item.config);
        }
        if (Modifier.isInterface(classInfo.flags())) {
            checkSubClassesMethod(view.getAllKnownImplementors(classInfo.name()), methodName, view, ano, values);
        } else {
            checkSubClassesMethod(view.getAllKnownSubclasses(classInfo.name()), methodName, view, ano, values);
        }
    }

    private static void updateValue(IndexView view, AnnotationInstance ano, ServiceValue.LogServiceAnnotation item) {
        item.log = ano.valueWithDefault(view, "log").asBoolean();
        item.stacktrace = ano.valueWithDefault(view, "stacktrace").asBoolean();
        String configKey = ano.valueWithDefault(view, "configKey").asString();
        if (!configKey.isBlank()) {
            item.configKey = configKey;
        }
    }

    private static void checkSubClasses(Collection<ClassInfo> classes, IndexView view, AnnotationInstance ano, ServiceValue values) {
        classes.forEach(subclass -> {
            if (!Modifier.isAbstract(subclass.flags()) && !Modifier.isInterface(subclass.flags())) {
                String subClassName = subclass.name().toString();
                if (!values.exists(subClassName)) {
                    ServiceValue.ClassItem clazz = values.getOrCreate(subClassName);
                    updateValue(view, ano, clazz.config);
                }
            }
        });
    }

    private static void checkSubClassesMethod(Collection<ClassInfo> classes, String methodName, IndexView view, AnnotationInstance ano, ServiceValue values) {
        classes.forEach(subclass -> {
            if (!Modifier.isAbstract(subclass.flags()) && !Modifier.isInterface(subclass.flags())) {
                String subClassName = subclass.name().toString();
                ServiceValue.ClassItem clazz = values.getOrCreate(subClassName);
                if (!clazz.exists(methodName)) {
                    ServiceValue.MethodItem item = clazz.getOrCreate(methodName);
                    updateValue(view, ano, item.config);
                }
            }
        });
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
                    log.infof("Disabling tkit logs on: {%s} because it matches the ignore pattern: '%s' (set via 'tkit.log.ignore.pattern')", name,
                            buildConfig.autoDiscover.ignorePattern);
                }
                return matches;
            }
        });
    }

}

