package org.tkit.quarkus.log.rs;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.*;

/**
 * The rest service annotation.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RestService {

    /**
     * Configuration key.
     * @return rest controller configuration key.
     */
    String configKey() default "";

    /**
     * Log the method or class if the flag is {@code true}.
     *
     * @return the log flag.
     */
    @Nonbinding boolean log() default true;

    /**
     * Log the method payload or class if the flag is {@code true}.
     *
     * @return the payload flag.
     */
    @Nonbinding boolean payload() default true;

}
