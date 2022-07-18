package org.tkit.quarkus.log.cdi.runtime;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.tkit.quarkus.log.cdi.LogParam;
import org.tkit.quarkus.log.cdi.LogService;

import io.quarkus.arc.Unremovable;

@ApplicationScoped
@Unremovable
@LogService(log = false)
public class LogParamContainer {

    @Inject
    @Any
    Instance<LogParam> parameters;

    public List<LogParam> getParameters() {
        return parameters.stream().collect(Collectors.toList());
    }
}
