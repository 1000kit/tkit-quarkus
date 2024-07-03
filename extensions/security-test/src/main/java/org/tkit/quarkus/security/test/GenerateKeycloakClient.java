package org.tkit.quarkus.security.test;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
@Inherited
public @interface GenerateKeycloakClient {

    /**
     * Name of the to be generated keycloak client
     *
     * @return name of the generated client
     */
    String clientName() default "default";

    /**
     * scopes which should be assigned to the client
     *
     * @return client scopes which are assigned to the client
     */
    String[] scopes();

    /**
     * Should the created client be deleted after all tests got executed?
     *
     * @return true if client should be deleted, false otherwise
     */
    boolean deleteAfter() default true;

}
