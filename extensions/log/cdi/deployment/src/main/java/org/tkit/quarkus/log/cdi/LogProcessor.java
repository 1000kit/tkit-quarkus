/*
 * Copyright 2019 1000kit.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tkit.quarkus.log.cdi;

import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CapabilityBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.logging.Logger;
import org.tkit.quarkus.log.cdi.interceptor.LogParamValueService;
import org.tkit.quarkus.log.cdi.runtime.LogBuildTimeConfig;
import org.tkit.quarkus.log.cdi.runtime.LogRecorder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

public class LogProcessor {

    static final String FEATURE_NAME = "tkit-cdi-log";

    private static final Logger log = Logger.getLogger(LogProcessor.class);


    private static final String LOG_BUILDER_SERVICE = LogParamValueService.class.getName();

    private static final List<DotName> ANNOTATION_DOT_NAMES = List.of(
            DotName.createSimple(ApplicationScoped.class.getName()),
            DotName.createSimple(Singleton.class.getName()),
            DotName.createSimple(RequestScoped.class.getName())
    );

    LogBuildTimeConfig buildConfig;

    @BuildStep
    CapabilityBuildItem capability() {
        return new CapabilityBuildItem(FEATURE_NAME);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void configureRuntimeProperties(LogRecorder recorder, BeanContainerBuildItem beanContainer) {
        BeanContainer container = beanContainer.getValue();
        recorder.init(container);
    }

    @BuildStep
    @Record(STATIC_INIT)
    void build(BuildProducer<FeatureBuildItem> feature, LogRecorder recorder) throws Exception {
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
    }

    @BuildStep
    public AnnotationsTransformerBuildItem interceptorBinding() {
        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            private Pattern ignorePattern = buildConfig.ignorePattern.isPresent() ? Pattern.compile(buildConfig.ignorePattern.get()): null;

            @Override
            public boolean appliesTo(AnnotationTarget.Kind kind) {
                return !buildConfig.disable && kind == AnnotationTarget.Kind.CLASS;
            }

            public void transform(TransformationContext context) {
                ClassInfo target = context.getTarget().asClass();
                Map<DotName, List<AnnotationInstance>> tmp = target.annotations();
                Optional<DotName> dot = ANNOTATION_DOT_NAMES.stream().filter(tmp::containsKey).findFirst();
                if (dot.isPresent()) {
                    String name = target.name().toString();
                    Optional<String> add = buildConfig.packages.stream().filter(name::startsWith).findFirst();
                    if (add.isPresent() && !LOG_BUILDER_SERVICE.equals(name) && !matchesIgnorePattern(name)) {
                        context.transform().add(LogService.class).done();
                    }
                }
            }

            private boolean matchesIgnorePattern(String name) {
                if (buildConfig.ignorePattern.isEmpty() || buildConfig.ignorePattern.get().isBlank()) {
                    return false;
                }
                boolean matches = ignorePattern.matcher(name).matches();
                if (matches) {
                    log.infof("Disabling tkit logs on: {%s} because it matches the ignore pattern: '%s' (set via 'tkit.log.ignore.pattern')", name,
                            buildConfig.ignorePattern);
                }
                return matches;
            }
        });
    }

}

