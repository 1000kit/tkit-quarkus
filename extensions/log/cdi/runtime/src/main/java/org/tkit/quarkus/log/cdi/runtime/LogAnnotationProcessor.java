/*
 * Copyright 2020 1000kit.org.
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
package org.tkit.quarkus.log.cdi.runtime;

import org.tkit.quarkus.log.cdi.LogParam;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.*;

/**
 * The logger processor.
 */
@SupportedAnnotationTypes(LogAnnotationProcessor.LOGGER_PARAM_CLASS)
public class LogAnnotationProcessor extends AbstractProcessor {

    public static final String LOGGER_PARAM_CLASS = "org.tkit.quarkus.log.cdi.LogParam";

    private static final int MAX_ROUND = 20;
    private int round;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        LogAnnotationGenerator.createLogParam(processingEnv, roundEnv.getElementsAnnotatedWith(LogParam.class));
        if (!roundEnv.processingOver() && round > MAX_ROUND) {
            messager().printMessage(Diagnostic.Kind.ERROR, "LogParam possible processing loop detected (" + (MAX_ROUND + 1) + ")");
        }
        round++;
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(LogParam.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Messager messager() {
        return processingEnv.getMessager();
    }


}
