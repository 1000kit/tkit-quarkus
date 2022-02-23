package org.tkit.quarkus.log.rs;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.*;

/**
 * The logger service annotation.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RestController {

    /**
     * Log the method or class if the flag is {@code true}.
     *
     * @return the log flag.
     */
    @Nonbinding boolean log() default true;

}
