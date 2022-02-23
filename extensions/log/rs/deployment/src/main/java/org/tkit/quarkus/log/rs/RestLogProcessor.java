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
import java.util.stream.Collectors;

public class RsLogProcessor {

    static final String FEATURE_NAME = "tkit-log-rs";

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
    void restServices(BeanArchiveIndexBuildItem index, BuildProducer<RestServiceBuildItem> producer) {

        RestServiceValue g = new RestServiceValue();
        Map<String, String> keys = new HashMap<>();

        IndexView view = index.getIndex();
        view.getAnnotations(DotName.createSimple(RestService.class.getName()))
                .forEach(a -> createRestServiceValue(view, a, g, keys));

        keys.forEach((id,key) -> g.keys.compute(key, (k, v) -> {
            if (v == null) {
                v = new HashSet<>();
            }
            v.add(id);
            return v;
        }));
        producer.produce(new RestServiceBuildItem(g));
    }

    private static void createRestServiceValue(IndexView view, AnnotationInstance ano, RestServiceValue g, Map<String, String> keys) {
        RestServiceValue.Item r = RestServiceValue.createItem();
        r.log = ano.valueWithDefault(view, "log").asBoolean();


        String key = ano.valueWithDefault(view, "key").asString();

        if (ano.target().kind() == AnnotationTarget.Kind.CLASS) {
            ClassInfo c = ano.target().asClass();
            r.id = c.asClass().name().toString();
            if (key != null && !key.isBlank()) {
                keys.put(r.id, key);
                addClassInfoKeys(r.id, view, c, key + ".",  keys);
            }
        } else {
            MethodInfo mi = ano.target().asMethod();
            r.id = mi.declaringClass().name().toString() + "." + mi.name();
            if (key != null && !key.isBlank()) {
                keys.put(r.id, key);
            }
        }


       System.out.println("### -> " + r.id + "/" + key + "/" + r.log);
       g.services.put(r.id, r);
    }

    private static void addClassInfoKeys(String clazz, IndexView view, ClassInfo c, String key, Map<String, String> keys) {
        if (c == null) {
            return;
        }
        String prefix = clazz + ".";
        c.methods().forEach(m -> {
            String name = m.name();
            if (!"<init>".equals(name) && Modifier.isPublic(m.flags()) && !Modifier.isStatic(m.flags())) {
                keys.putIfAbsent(prefix + name, key + name);
            }
        });
        if (!c.superName().equals(DotName.createSimple(Object.class.getName()))) {
            addClassInfoKeys(clazz, view, view.getClassByName(c.superName()), key, keys);
        }
        c.interfaceNames().forEach(i -> {
            ClassInfo ci = view.getClassByName(i);
            ci.methods().forEach(m -> {
                System.out.println("KINF " + m.name() + " - " + m.flags());
            });

        });
    }

}

