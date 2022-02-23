package org.tkit.quarkus.log.cdi;

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

    public List<LogParam> getJobHandlers() {
        return parameters.stream().collect(Collectors.toList());
    }
}
