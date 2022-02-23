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

import java.text.MessageFormat;

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

}

