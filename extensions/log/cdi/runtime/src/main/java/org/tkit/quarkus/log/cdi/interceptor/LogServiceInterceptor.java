package org.tkit.quarkus.log.cdi.interceptor;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.log.cdi.LogExclude;
import org.tkit.quarkus.log.cdi.LogFriendlyException;
import org.tkit.quarkus.log.cdi.LogService;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.*;
import java.util.concurrent.CompletionStage;

import static org.tkit.quarkus.log.cdi.interceptor.LogConfig.getLoggerServiceAno;

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
        Object result;
        Method method = ic.getMethod();
        String className = getObjectClassName(ic.getTarget());


        LogService ano = getLoggerServiceAno(ic.getTarget().getClass(), className, method, LogParamValueService.getConfig().enableProtectedMethod);
        if (ano.log()) {
            boolean isEntrypoint = false;
            if (ApplicationContext.isEmpty()) {
                isEntrypoint = true;
                ApplicationContext.start();
            }

            Logger logger = LoggerFactory.getLogger(className);
            String parameters = getValuesString(ic.getParameters(), method.getParameters());

            InterceptorContext context = new InterceptorContext(method.getName(), parameters);

            try {
                logger.info(LogConfig.msgStart(context));
                result = ic.proceed();

                if (result instanceof CompletionStage) {

                    logger.info(LogConfig.msgFutureStart(context));
                    CompletionStage cs = (CompletionStage) result;
                    cs.toCompletableFuture().whenComplete((u, eex) -> {
                        if (eex != null) {
                            handleException(context, logger, ano, (Throwable) eex);
                        } else {
                            String contextResult = LogConfig.RESULT_VOID;
                            if (u != Void.TYPE) {
                                contextResult = getValue(u);
                            }
                            context.closeContext(contextResult);
                            // log the success message
                            logger.info(LogConfig.msgSucceed(context));
                        }
                    });
                } else {
                    String contextResult = LogConfig.RESULT_VOID;
                    if (method.getReturnType() != Void.TYPE) {
                        contextResult = getValue(result);
                    }
                    context.closeContext(contextResult);

                    // log the success message
                    logger.info(LogConfig.msgSucceed(context));
                }
            } catch (InvocationTargetException ie) {
                handleException(context, logger, ano, ie.getCause());
                throw ie;
            } catch (Exception ex) {
                handleException(context, logger, ano, ex);
                throw ex;
            } finally {
                if (isEntrypoint){
                    ApplicationContext.close();
                }
            }
        } else {
            result = ic.proceed();
        }
        return result;
    }

    /**
     * Handles the exception.
     *
     * @param context the interceptor context.
     * @param logger  the logger.
     * @param ano     the annotation.
     * @param ex      the exception.
     */
    private void handleException(InterceptorContext context, Logger logger, LogService ano, Throwable ex) {
        context.closeContext(getValue(ex));
        logger.error(LogConfig.msgFailed(context));
        boolean stacktrace = ano.stacktrace();

        if (ex instanceof LogFriendlyException) {
            if (stacktrace && ((LogFriendlyException) ex).shouldLogStacktrace()) {
                logger.error("Error ", ex);
            }
        } else if (stacktrace) {
            logger.error("Error ", ex);
        }
    }

    /**
     * Gets the list of string corresponding to the list of parameters.
     *
     * @param value      the list of parameters.
     * @param parameters the list of method parameters.
     * @return the list of string corresponding to the list of parameters.
     */
    private String getValuesString(Object[] value, Parameter[] parameters) {
        if (value != null && value.length > 0) {
            StringBuilder sb = new StringBuilder();
            int index = 0;
            sb.append(getValue(value[index], parameters[index]));
            index++;
            for (; index < value.length; index++) {
                sb.append(',');
                sb.append(getValue(value[index], parameters[index]));
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * Get the parameter log value.
     *
     * @param value     the parameter value.
     * @param parameter the method parameter.
     * @return the corresponding log value.
     */
    private String getValue(Object value, Parameter parameter) {
        LogExclude pa = parameter.getAnnotation(LogExclude.class);
        if (pa != null) {
            if (!pa.mask().isEmpty()) {
                return pa.mask();
            }
            return parameter.getName();
        }
        return getValue(value);
    }

    /**
     * Gets the string corresponding to the parameter.
     *
     * @param parameter the method parameter.
     * @return the string corresponding to the parameter.
     */
    private String getValue(Object parameter) {
        return LogParamValueService.getParameterValue(parameter);
    }
}
