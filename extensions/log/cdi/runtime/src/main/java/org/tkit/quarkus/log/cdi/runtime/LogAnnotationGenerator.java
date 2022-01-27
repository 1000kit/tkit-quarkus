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
import org.tkit.quarkus.log.cdi.LogParamValue;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class LogAnnotationGenerator {

    private static String TEMPLATE = "" +
            "package org.tkit.quarkus.log.cdi;\n" +
            "\n" +
            "import java.util.List;\n" +
            "import java.util.ArrayList;\n" +
            "import javax.enterprise.context.ApplicationScoped;\n" +
            "\n" +
            "@ApplicationScoped\n" +
            "@LogService(log = false)\n" +
            "public class #name# implements LogParamValue {\n" +
            "\n" +
            "  public List<Item> getClasses() {\n" +
            "    List<Item> result = new ArrayList<>();\n" +
            "#classes#" +
            "    return result;\n" +
            "  }\n" +
            "\n" +
            "  public List<Item> getAssignableFrom() {\n" +
            "    List<Item> result = new ArrayList<>();\n" +
            "#assignable#" +
            "    return result;\n" +
            "  }\n" +
            "}";

    public static void createLogParam(ProcessingEnvironment env, Set<? extends Element> elements) {
        if (elements == null || elements.isEmpty()) {
            return;
        }

        List<MethodInfo> classes = new ArrayList<>();
        List<MethodInfo> assignable = new ArrayList<>();

        for (Element element : elements) {
            if (element.getKind() == ElementKind.METHOD && element.getModifiers().contains(Modifier.STATIC)) {
                env.getMessager().printMessage(Diagnostic.Kind.NOTE, "LogParam processing interface: " + element);

                ExecutableElement method = (ExecutableElement) element;

                for (AnnotationMirror a : element.getAnnotationMirrors()) {
                    if (LogParam.class.getName().equals(a.getAnnotationType().toString())) {
                        int priority = 0;
                        List<?> tmpClasses = null;
                        List<?> tmpAssignable = null;
                        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : a.getElementValues().entrySet()) {
                            if ("priority".equals(e.getKey().getSimpleName().toString())) {
                                priority = (Integer) e.getValue().getValue();
                            }
                            if ("classes".equals(e.getKey().getSimpleName().toString())) {
                                tmpClasses = (List<?>) e.getValue().getValue();
                            }
                            if ("assignableFrom".equals(e.getKey().getSimpleName().toString())) {
                                tmpAssignable = (List<?>) e.getValue().getValue();
                            }
                        }
                        if (tmpClasses != null) {
                            classes.addAll(create(priority, method, tmpClasses));
                        }
                        if (tmpAssignable != null) {
                            assignable.addAll(create(priority, method, tmpAssignable));
                        }
                    }
                }
            } else {
                env.getMessager().printMessage(Diagnostic.Kind.ERROR, "LogParam is not static method. The element: " + element + " kind: " + element.getKind() + " will be ignored.");
            }
        }

        if (classes.isEmpty() && assignable.isEmpty()) {
            return;
        }

        String classesSource = generate(classes);
        String assignableSource = generate(assignable);

        String name = "_" + UUID.randomUUID().toString().replaceAll("-", "_");
        String source = "";

        source = TEMPLATE.replaceFirst("#name#", LogParamValue.class.getSimpleName() + name);
        source = source.replaceFirst("#classes#", classesSource);
        source = source.replaceFirst("#assignable#", assignableSource);
        try {

            // save implementation class
            PackageElement pkg = env.getElementUtils().getPackageElement("org.tkit.quarkus.log.cdi");
            String fileName = LogParamValue.class.getName() + name;
            JavaFileObject builderFile = env.getFiler().createSourceFile(fileName, pkg);
            // write output source file
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                out.write(source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generate(List<MethodInfo> items) {
        if (items.isEmpty()) {
            return "";
        }
        return items.stream()
                .map(i -> "    result.add(item(" + i.priority + "," + i.paramClass + ".class," + i.clazz + "::" + i.method + "));\n")
                .collect(Collectors.joining());
    }

    private static List<MethodInfo> create(int priority, ExecutableElement method, List<?> values) {
        return values.stream().map(value -> {
            MethodInfo i = new MethodInfo();
            i.priority = priority;
            i.paramClass = ((AnnotationValue)value).getValue().toString();
            i.method = method.getSimpleName().toString();
            i.clazz = "" + method.getEnclosingElement();
            return i;
        }).collect(Collectors.toList());
    }

    public static class MethodInfo {

        public int priority;

        public String paramClass;

        public String clazz;

        public String method;


    }
}
