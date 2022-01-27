/*
 * Copyright 2020 1000kit.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tkit.quarkus.log.cdi.interceptor;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.log.cdi.LogExclude;
import org.tkit.quarkus.log.cdi.LogFriendlyException;
import org.tkit.quarkus.log.cdi.LogService;
import org.tkit.quarkus.log.cdi.context.CorrelationScope;
import org.tkit.quarkus.log.cdi.context.TkitLogContext;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * The logger service interceptor.
 */
@Interceptor
@LogService
@Priority(Interceptor.Priority.LIBRARY_BEFORE + 2)
public class LogServiceInterceptor {

    public static final String PROP_DISABLE_PROTECTED_METHODS = "tkit.log.method.protected.disable";

    /**
     * The logger builder service.
     */
    @Inject
    LogParamValueService logParamService;

    /**
     * Disable the protected methods to log.
     */
    @Inject
    @ConfigProperty(name = PROP_DISABLE_PROTECTED_METHODS, defaultValue = "true")
    boolean disableProtectedMethod;


    @Inject
    @ConfigProperty(name = "tkit.log.mdc", defaultValue = "false")
    boolean mdcLog;

    @Inject
    @ConfigProperty(name = "quarkus.tkit.log.mdc.errorKey", defaultValue = "errorNumber")
    public String errorNumberKey;

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
     * Gets the logger service annotation.
     *
     * @param clazz                  the class.
     * @param method                 the method.
     * @param disableProtectedMethod {@code true} to disable to log protected methods.
     * @return the logger service annotation.
     */
    public static LogService getLoggerServiceAno(Class<?> clazz, String className, Method method, boolean disableProtectedMethod) {

        if (disableProtectedMethod && Modifier.isProtected(method.getModifiers())) {
            return createLoggerService(false, false);
        }
        Config config = ConfigProvider.getConfig();
        String mc = className + "." + method.getName() + "/tkit-log/";
        String c = className + "/tkit-log/";

        Optional<Boolean> log = config.getOptionalValue(mc + "log", Boolean.class);
        Optional<Boolean> trace = config.getOptionalValue(mc + "trace", Boolean.class);
        LogService anno = method.getAnnotation(LogService.class);
        if (anno != null) {
            return createLoggerService(log.orElse(anno.log()), trace.orElse(anno.stacktrace()));
        }
        Optional<Boolean> clog = config.getOptionalValue(c + "log", Boolean.class);
        Optional<Boolean> ctrace = config.getOptionalValue(c + "trace", Boolean.class);
        LogService canno = clazz.getAnnotation(LogService.class);
        if (canno != null) {
            return createLoggerService(log.orElse(clog.orElse(canno.log())), trace.orElse(ctrace.orElse(canno.stacktrace())));
        }
        return createLoggerService(log.orElse(clog.orElse(true)), trace.orElse(ctrace.orElse(true)));
    }

    /**
     * Creates the logger service.
     *
     * @param log        the log flag.
     * @param stacktrace the stacktrace flag.
     * @return the corresponding logger service.
     */
    private static LogService createLoggerService(boolean log, boolean stacktrace) {
        return new LogService() {
            @Override
            public boolean log() {
                return log;
            }

            @Override
            public boolean stacktrace() {
                return stacktrace;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return LogService.class;
            }
        };
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


        LogService ano = getLoggerServiceAno(ic.getTarget().getClass(), className, method, disableProtectedMethod);
        if (ano.log()) {
            boolean isEntrypoint = false;
            if (TkitLogContext.get() == null) {
                isEntrypoint = true;
                CorrelationScope correlationScope = new CorrelationScope(UUID.randomUUID().toString());
                TkitLogContext.set(correlationScope);
            }

            Logger logger = LoggerFactory.getLogger(className);
            String parameters = getValuesString(ic.getParameters(), method.getParameters());

            InterceptorContext context = new InterceptorContext(method.getName(), parameters);

            try {

                // mdc log
                if (mdcLog) {
                    MDC.put("method", context.method);
                    MDC.put("parameters", context.parameters);
                    MDC.put("status", "started");
                }
                logger.info(LogConfig.msgStart(context));

                result = ic.proceed();

                if (result instanceof CompletionStage) {

                    // mdc log
                    if (mdcLog) {
                        MDC.put("method", context.method);
                        MDC.put("parameters", context.parameters);
                        MDC.put("status", "future");
                    }

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

                            // mdc log
                            if (mdcLog) {
                                MDC.put("method", context.method);
                                MDC.put("parameters", context.parameters);
                                MDC.put("status", "succeed");
                                MDC.put("result", context.result);
                                MDC.put("time", context.time);
                            }

                            logger.info(LogConfig.msgSucceed(context));
                        }
                    });
                } else {
                    String contextResult = LogConfig.RESULT_VOID;
                    if (method.getReturnType() != Void.TYPE) {
                        contextResult = getValue(result);
                    }
                    context.closeContext(contextResult);

                    // mdc log
                    if (mdcLog) {
                        MDC.put("method", context.method);
                        MDC.put("parameters", context.parameters);
                        MDC.put("status", "succeed");
                        MDC.put("result", context.result);
                        MDC.put("time", context.time);
                    }

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
                    TkitLogContext.set(null);
                    MDC.remove("X-Correlation-ID");
                }
                if (mdcLog) {
                    MDC.remove("method");
                    MDC.remove("parameters");
                    MDC.remove("status");
                    MDC.remove("result");
                    MDC.remove("time");
                    MDC.remove(errorNumberKey);
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
        // mdc log
        if (mdcLog) {
            MDC.put("method", context.method);
            MDC.put("parameters", context.parameters);
            MDC.put("result", context.result);
            MDC.put("time", context.time);
            MDC.put("status", "error");

            if (ex instanceof LogFriendlyException) {
                MDC.put(errorNumberKey, ((LogFriendlyException) ex).getErrorNumber());
            }
        }
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
        return logParamService.getParameterValue(parameter);
    }
}
