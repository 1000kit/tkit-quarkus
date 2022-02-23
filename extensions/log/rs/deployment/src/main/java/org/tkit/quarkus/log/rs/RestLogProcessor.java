package org.tkit.quarkus.log.rs;

import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.*;

import java.lang.reflect.Modifier;
import java.util.*;

public class RestLogProcessor {

    static final String FEATURE_NAME = "tkit-log-rs";

    static final DotName ANO_REST_SERVICE = DotName.createSimple(RestService.class.getName());

    @BuildStep
    void build(BuildProducer<FeatureBuildItem> feature) {
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void init(RestRecorder recorder, RestRuntimeConfig config, RestServiceBuildItem items) {
        recorder.init(config, items.value);
    }

    @BuildStep
    void restServices(BeanArchiveIndexBuildItem index, BuildProducer<RestServiceBuildItem> producer) {
        RestServiceValue values = new RestServiceValue();
        IndexView view = index.getIndex();
        view.getAnnotations(ANO_REST_SERVICE).forEach(a -> createRestServiceValue(view, a, values));
        values.updateMapping();
        producer.produce(new RestServiceBuildItem(values));
    }

    private static void createRestServiceValue(IndexView view, AnnotationInstance ano, RestServiceValue values) {
        if (ano.target().kind() == AnnotationTarget.Kind.CLASS) {
            ClassInfo classInfo = ano.target().asClass();
            String className = classInfo.name().toString();
            if (!Modifier.isAbstract(classInfo.flags()) && !Modifier.isInterface(classInfo.flags())) {
                RestServiceValue.ClassItem clazz = values.getOrCreate(className);
                updateValue(view, ano, clazz.config);
            }
            if (Modifier.isInterface(classInfo.flags())) {
                checkSubClasses(view.getAllKnownImplementors(classInfo.name()), view, ano, values);
            } else {
                checkSubClasses(view.getAllKnownSubclasses(classInfo.name()), view, ano, values);
            }
        } else if (ano.target().kind() == AnnotationTarget.Kind.METHOD) {
            MethodInfo methodInfo = ano.target().asMethod();
            String methodName = methodInfo.name();
            if (!Modifier.isPublic(methodInfo.flags())) {
                return;
            }
            ClassInfo classInfo = methodInfo.declaringClass();
            String className = classInfo.name().toString();
            if (!Modifier.isAbstract(classInfo.flags()) && !Modifier.isInterface(classInfo.flags())) {
                RestServiceValue.ClassItem clazz = values.getOrCreate(className);
                RestServiceValue.Item item = clazz.getOrCreate(methodName);
                updateValue(view, ano, item);
            }
            if (Modifier.isInterface(classInfo.flags())) {
                checkSubClassesMethod(view.getAllKnownImplementors(classInfo.name()), methodName, view, ano, values);
            } else {
                checkSubClassesMethod(view.getAllKnownSubclasses(classInfo.name()), methodName, view, ano, values);
            }
        }
    }

    private static void updateValue(IndexView view, AnnotationInstance ano, RestServiceValue.Item item) {
        item.log = ano.valueWithDefault(view, "log").asBoolean();
        String configKey = ano.valueWithDefault(view, "configKey").asString();
        if (!configKey.isBlank()) {
            item.configKey = configKey;
        }
    }

    private static void checkSubClasses(Collection<ClassInfo> classes, IndexView view, AnnotationInstance ano, RestServiceValue values) {
        classes.forEach(subclass -> {
            if (!Modifier.isAbstract(subclass.flags()) && !Modifier.isInterface(subclass.flags())) {
                String subClassName = subclass.name().toString();
                if (!values.exists(subClassName)) {
                    RestServiceValue.ClassItem clazz = values.getOrCreate(subClassName);
                    updateValue(view, ano, clazz.config);
                }
            }
        });
    }

    private static void checkSubClassesMethod(Collection<ClassInfo> classes, String methodName, IndexView view, AnnotationInstance ano, RestServiceValue values) {
        classes.forEach(subclass -> {
            if (!Modifier.isAbstract(subclass.flags()) && !Modifier.isInterface(subclass.flags())) {
                String subClassName = subclass.name().toString();
                RestServiceValue.ClassItem clazz = values.getOrCreate(subClassName);
                if (!clazz.exists(methodName)) {
                    RestServiceValue.Item item = clazz.getOrCreate(methodName);
                    updateValue(view, ano, item);
                }
            }
        });
    }

}

