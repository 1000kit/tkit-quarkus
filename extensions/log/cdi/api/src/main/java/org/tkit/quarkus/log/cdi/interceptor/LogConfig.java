package org.tkit.quarkus.log.cdi.interceptor;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.tkit.quarkus.log.cdi.LogService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Optional;

/**
 * The logger configuration.
 */
public class LogConfig {

    /**
     * The result void text.
     */
    static final String RESULT_VOID;

    /**
     * The message start.
     */
    private static MessageFormat messageStart;

    /**
     * The message succeed.
     */
    private static MessageFormat messageSucceed;

    /**
     * The message future start.
     */
    private static MessageFormat messageFutureStart;

    /**
     * The message failed.
     */
    private static MessageFormat messageFailed;

    static {
        Config config = ConfigProvider.getConfig();
        RESULT_VOID = config.getOptionalValue("org.tkit.logger.result.void", String.class).orElse("void");
        messageStart = new MessageFormat(config.getOptionalValue("org.tkit.logger.start", String.class).orElse("{0}({1}) started."));
        messageSucceed = new MessageFormat(config.getOptionalValue("org.tkit.logger.succeed", String.class).orElse("{0}({1}):{2} [{3}s] succeed."));
        messageFailed = new MessageFormat(config.getOptionalValue("org.tkit.logger.failed", String.class).orElse("{0}({1}):{2} [{3}s] failed."));
        messageFutureStart = new MessageFormat(config.getOptionalValue("org.tkit.logger.futureStart", String.class).orElse("{0}({1}) future started" +
                "."));
    }

    /**
     * The default constructor.
     */
    private LogConfig() {
        // empty constructor
    }

    /**
     * The message failed method.
     *
     * @param context the interceptor context.
     * @return the log message.
     */
    static String msgFailed(InterceptorContext context) {
        return msg(messageFailed, new Object[]{context.method, context.parameters, context.result, context.time});
    }

    /**
     * The message succeed method.
     *
     * @param context the interceptor context.
     * @return the log message.
     */
    static String msgSucceed(InterceptorContext context) {
        return msg(messageSucceed, new Object[]{context.method, context.parameters, context.result, context.time});
    }

    /**
     * The message future start method.
     *
     * @param context the interceptor context.
     * @return the log message.
     */
    static String msgFutureStart(InterceptorContext context) {
        return msg(messageFutureStart, new Object[]{context.method, context.parameters, context.result, context.time});
    }

    /**
     * The message start method.
     *
     * @param context the interceptor context.
     * @return the log message.
     */
    static String msgStart(InterceptorContext context) {
        return msg(messageStart, new Object[]{context.method, context.parameters});
    }

    /**
     * Log message method.
     *
     * @param mf         the message formatter.
     * @param parameters the log entry parameters.
     * @return the log parameter.
     */
    public static String msg(MessageFormat mf, Object[] parameters) {
        return mf.format(parameters, new StringBuffer(), null).toString();
    }

    /**
     * Gets the logger service annotation.
     *
     * @param clazz                  the class.
     * @param method                 the method.
     * @param enableProtectedMethod {@code true} to disable to log protected methods.
     * @return the logger service annotation.
     */
    public static LogService getLoggerServiceAno(Class<?> clazz, String className, Method method, boolean enableProtectedMethod) {

        if (!enableProtectedMethod && Modifier.isProtected(method.getModifiers())) {
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
}

