package org.tkit.quarkus.log.cdi.test.app;

import org.tkit.quarkus.log.cdi.LogFriendlyException;

public class DummyLogFriendlyException extends RuntimeException implements LogFriendlyException {

    public static final String DUMMY_ERROR_NUMBER = "DummyErrorNumber";

    @Override
    public String getErrorNumber() {
        return DUMMY_ERROR_NUMBER;
    }

    @Override
    public boolean shouldLogStacktrace() {
        return false;
    }
}
