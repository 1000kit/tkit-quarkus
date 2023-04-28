package org.tkit.quarkus.log.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If present, do not include this parameter value in the logger.
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExclude {

    public static String MASK = "";

    /**
     * If this flag is empty the parameter name will be used to log
     * otherwise the value of this attribute will be use.
     *
     * @return the mask flag.
     */
    String mask() default MASK;
}
