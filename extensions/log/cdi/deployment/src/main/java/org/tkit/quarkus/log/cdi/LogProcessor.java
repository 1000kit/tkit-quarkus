package org.tkit.quarkus.log.cdi;

import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.*;
import org.jboss.logging.Logger;
import org.tkit.quarkus.log.cdi.interceptor.LogParamValueService;
import org.tkit.quarkus.log.cdi.runtime.LogRecorder;
import org.tkit.quarkus.log.cdi.runtime.LogRuntimeConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;


public class LogProcessor {

    static final String FEATURE_NAME = "tkit-log-cdi";

    private static final Logger log = Logger.getLogger(LogProcessor.class);


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
    void configureRuntimeProperties(LogRecorder recorder, LogRuntimeConfig config) {
        recorder.init(config);
    }

    /**
     * Autodiscovery
     */
    @BuildStep
    public AnnotationsTransformerBuildItem interceptorBinding(LogBuildTimeConfig buildConfig) {
        if (!buildConfig.autoDiscover.enabled) {
            return null;
        }

        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            private final Pattern ignorePattern = buildConfig.autoDiscover.ignorePattern.map(Pattern::compile).orElse(null);

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

