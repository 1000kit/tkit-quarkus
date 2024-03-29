package org.tkit.quarkus.log.rs;

import java.lang.annotation.*;

import jakarta.enterprise.util.Nonbinding;

/**
 * The rest service annotation.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface LogRestService {

    /**
     * Configuration key.
     *
     * @return rest controller configuration key.
     */
    String configKey() default "";

    /**
     * Log the method or class if the flag is {@code true}.
     *
     * @return the log flag.
     */
    @Nonbinding
    boolean log() default true;

    /**
     * Log the method payload or class if the flag is {@code true}.
     *
     * @return the payload flag.
     */
    @Nonbinding
    boolean payload() default false;

}
