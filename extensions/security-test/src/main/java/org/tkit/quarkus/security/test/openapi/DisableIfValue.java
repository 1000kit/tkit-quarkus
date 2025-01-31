package org.tkit.quarkus.security.test.openapi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

@Target({ METHOD, TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(DisableIfValueCondition.class)
public @interface DisableIfValue {

    /**
     * System property name for the value.
     */
    String valueProperty();

    /**
     * System property name for the regex.
     */
    String regexProperty();
}
