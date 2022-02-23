package org.tkit.quarkus.log.cdi.runtime;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.annotations.Recorder;
import org.tkit.quarkus.log.cdi.interceptor.LogParamValueService;

/**
 * The logger builder interface.
 */
@Recorder
public class LogRecorder {

    public void init(LogRuntimeConfig config) {
        LogParamContainer con = Arc.container().instance(LogParamContainer.class).get();
        LogParamValueService.init(config, con.getParameters());
    }

}
