package org.tkit.quarkus.log.cdi.interceptor;

import java.lang.reflect.*;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;
import org.tkit.quarkus.log.cdi.LogFriendlyException;
import org.tkit.quarkus.log.cdi.LogRecorder;
import org.tkit.quarkus.log.cdi.LogService;
import org.tkit.quarkus.log.cdi.ServiceValue;
import org.tkit.quarkus.log.cdi.runtime.LogRuntimeConfig;

/**
 * The logger service interceptor.
 */
@Interceptor
@LogService
@Priority(Interceptor.Priority.LIBRARY_BEFORE + 2)
public class LogServiceInterceptor {

    /**
     * Gets the service class name.
     *
     * @param object the target class.
     * @return the corresponding class name.
     */
    private static String getObjectClassName(Object object) {
        if (object instanceof Proxy) {
            Class<?>[] interf = object.getClass().getInterfaces();
            if (interf.length > 0) {
                return getClassName(interf[0]);
            }
        }
        return getClassName(object.getClass());
    }

    /**
     * Gets the service class name.
     *
     * @param clazz the target class.
     * @return the corresponding class name.
     */
    private static String getClassName(Class<?> clazz) {
        if (clazz != null && clazz.getSuperclass() != null) {
            return clazz.getSuperclass().getName();
        }
        if (clazz != null) {
            return clazz.getName();
        }
        return null;
    }

    /**
     * The method execution.
     *
     * @param ic the invocation context.
     * @return the method result object.
     * @throws Exception if the method fails.
     */
    @AroundInvoke
    public Object methodExecution(final InvocationContext ic) throws Exception {

        LogRuntimeConfig config = LogRecorder.getConfig();
        if (!config.enabled) {
            return ic.proceed();
        }

        Method method = ic.getMethod();
        String methodName = method.getName();
        String className = getObjectClassName(ic.getTarget());

        ServiceValue.MethodItem methodItem = LogRecorder.getService(className, methodName);
        if (methodItem == null) {
            return ic.proceed();
        }
        if (!methodItem.config.log) {
            return ic.proceed();
        }

        final boolean isEntrypoint = ApplicationContext.isEmpty();
        if (isEntrypoint) {
            ApplicationContext.start();
        }

        Logger logger = LoggerFactory.getLogger(className);
        String parameters = null;
        long startTime = System.currentTimeMillis();

        try {
            if (config.start.enabled) {
                parameters = getValuesString(methodItem, ic.getParameters(), method.getParameters());
                logger.info(String.format(config.start.template, methodName, parameters));
            }
            Object result = ic.proceed();

            String returnValue = config.returnVoidTemplate;
            if (method.getReturnType() != Void.TYPE) {
                returnValue = getReturnValue(methodItem, result);
            }

            // log the success message
            if (config.succeed.enabled) {
                if (parameters == null) {
                    parameters = getValuesString(methodItem, ic.getParameters(), method.getParameters());
                }
                logger.info(String.format(config.succeed.template, methodName, parameters, returnValue,
                        (System.currentTimeMillis() - startTime) / 1000f));
            }

            return result;

        } catch (Exception ex) {
            if (!config.failed.enabled) {
                throw ex;
            }
            Context.ApplicationError aex = ApplicationContext.get().addError(ex);

            Throwable error = ex;
            if (ex instanceof LogFriendlyException) {
                ApplicationContext.addBusinessLogParam(config.errorNumberKey, ((LogFriendlyException) ex).getErrorNumber());
            }
            if (ex instanceof InvocationTargetException) {
                error = ex.getCause();
            }
            if (parameters == null) {
                parameters = getValuesString(methodItem, ic.getParameters(), method.getParameters());
            }
            String er = getReturnValue(methodItem, error);
            logger.error(String.format(config.failed.template, methodName, parameters, er,
                    (System.currentTimeMillis() - startTime) / 1000f));

            if (methodItem.config.stacktrace && !aex.stacktrace) {
                logger.error("", ex);
                aex.stacktrace = true;
            }

            throw ex;
        } finally {
            if (isEntrypoint) {
                ApplicationContext.close();
            }
        }
    }

    /**
     * Gets the list of string corresponding to the list of parameters.
     *
     * @param values the list of parameters.
     * @param parameters the list of method parameters.
     * @return the list of string corresponding to the list of parameters.
     */
    private String getValuesString(ServiceValue.MethodItem methodItem, Object[] values, Parameter[] parameters) {
        if (values != null && values.length > 0) {
            StringBuilder sb = new StringBuilder();
            int index = 0;
            sb.append(getValue(methodItem, index, values, parameters));
            index++;
            for (; index < values.length; index++) {
                sb.append(',');
                sb.append(getValue(methodItem, index, values, parameters));
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * Get the parameter log value.
     *
     * @param methodItem method item configuration.
     * @param index index of the parameter.
     * @param values the parameter values.
     * @param parameters the method parameters.
     * @return the corresponding log value.
     */
    private String getValue(ServiceValue.MethodItem methodItem, int index, Object[] values, Parameter[] parameters) {
        if (methodItem.params != null && methodItem.params.containsKey((short) index)) {
            String mask = methodItem.params.get((short) index);
            if (mask != null) {
                return mask;
            }
            return parameters[index].getName();
        }
        return LogParamValueService.getParameterValue(values[index]);
    }

    /**
     * Gets the string corresponding to the parameter.
     *
     * @param parameter the method parameter.
     * @return the string corresponding to the parameter.
     */
    private String getReturnValue(ServiceValue.MethodItem methodItem, Object parameter) {
        if (methodItem.returnMask == null) {
            return LogParamValueService.getParameterValue(parameter);
        }
        return methodItem.returnMask;
    }
}
