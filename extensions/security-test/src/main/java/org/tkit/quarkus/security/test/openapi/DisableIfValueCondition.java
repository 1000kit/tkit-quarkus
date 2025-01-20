package org.tkit.quarkus.security.test.openapi;

import static io.quarkus.test.util.annotations.AnnotationUtils.findAnnotation;
import static java.lang.String.format;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;

import java.lang.reflect.AnnotatedElement;
import java.util.regex.Pattern;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DisableIfValueCondition implements ExecutionCondition {

    private static final ConditionEvaluationResult ENABLED_BY_DEFAULT = ConditionEvaluationResult.enabled(
            "@DisableIfValue is not present");

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        AnnotatedElement element = context
                .getElement()
                .orElseThrow(IllegalStateException::new);
        return findAnnotation(element, DisableIfValue.class)
                .map(annotation -> disableIf(annotation.getAnnotation(), annotation.getElement()))
                .orElse(ENABLED_BY_DEFAULT);
    }

    private ConditionEvaluationResult disableIf(DisableIfValue annotation, AnnotatedElement element) {

        var value = getValue(annotation.valueProperty());
        if (value == null) {
            return enabled(format("%s is enabled because '%s' value property is null", element, annotation.valueProperty()));
        }

        var regex = getValue(annotation.regexProperty());
        if (regex == null) {
            return enabled(format("%s is enabled because '%s' regex property is null", element, annotation.regexProperty()));
        }

        if (value.matches(regex)) {
            return disabled(format("%s is disabled because %s[%s] value matches regex property %s[%s] ", element,
                    annotation.valueProperty(), value, annotation.regexProperty(), regex));
        }

        return enabled(format("%s is enabled because %s[%s] value does not match regex property %s[%s]", element,
                annotation.valueProperty(), value, annotation.regexProperty(), regex));
    }

    private static final Pattern PATTERN = Pattern.compile("[^a-zA-Z0-9_]");

    private String getValue(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        var value = System.getProperty(name, null);
        if (value != null) {
            return value;
        }
        var sanitizedName = PATTERN.matcher(name).replaceAll("_");
        var env = System.getenv(sanitizedName);
        if (env != null) {
            return env;
        }
        return System.getenv(sanitizedName.toUpperCase());
    }
}
