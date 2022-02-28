package org.tkit.quarkus.log.cdi.runtime;

import io.quarkus.arc.Unremovable;
import org.tkit.quarkus.log.cdi.LogParam;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Unremovable
public class LogParamContainer {

    @Inject
    @Any
    Instance<LogParam> parameters;

    public List<LogParam> getParameters() {
        return parameters.stream().collect(Collectors.toList());
    }
}
