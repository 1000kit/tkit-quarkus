package org.tkit.quarkus.log.cdi.runtime;

import org.tkit.quarkus.log.cdi.LogParam;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class LogParamContainer {

    @Inject
    @Any
    Instance<LogParam> parameters;

    public List<LogParam> getParameters() {
        return parameters.stream().collect(Collectors.toList());
    }
}
