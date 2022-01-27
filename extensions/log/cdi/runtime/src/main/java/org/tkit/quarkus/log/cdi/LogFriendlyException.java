package org.tkit.quarkus.log.cdi;

/**
 * Interface for handling special exception logging
 */
public interface LogFriendlyException {

    /**
     * @return unique error number within defined micro service
     */
    String getErrorNumber();

    /**
     * @return define if stack trace should be printed or not
     */
    boolean shouldLogStacktrace();
}
