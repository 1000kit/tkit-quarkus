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
package org.tkit.quarkus.log.json;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("tkit-quarkus-log-json.adoc")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "tkit.log.json")
public interface LogJsonConfig {

    /**
     * Empty list constant.
     */
    String EMPTY_LIST = "<EMPTY-LIST>";

    /**
     * Determine whether to enable the JSON console formatting extension, which disables "normal" console formatting.
     */
    @WithName("enabled")
    @WithDefault("true")
    boolean enabled();

    /**
     * Enable "pretty printing" of the JSON record. Note that some JSON parsers will fail to read pretty printed output.
     */
    @WithName("pretty-print")
    @WithDefault("false")
    boolean prettyPrint();

    /**
     * The date format to use. The special string "default" indicates that the default format should be used.
     */
    @WithName("date-format")
    @WithDefault("default")
    String dateFormat();

    /**
     * The special end-of-record delimiter to be used. By default, no delimiter is used.
     */
    @WithName("record-delimiter")
    Optional<String> recordDelimiter();

    /**
     * The zone ID to use. The special string "default" indicates that the default zone should be used.
     */
    @WithName("zone-id")
    @WithDefault("default")
    String zoneId();

    /**
     * The exception output type to specify.
     */
    @WithName("exception-output-type")
    @WithDefault("formatted")
    ExtendedStructureFormatter.ExceptionOutputType exceptionOutputType();

    /**
     * Enable printing of more details in the log.
     * <p>
     * Printing the details can be expensive as the values are retrieved from the caller. The details include the
     * source class name, source file name, source method name and source line number.
     */
    @WithName("print-details")
    @WithDefault("false")
    boolean printDetails();

    /**
     * Add MDC keys mapping.
     */
    @WithName("keys.mdc")
    @WithDefault(EMPTY_LIST)
    List<String> mdcKeys();

    /**
     * Add MDC prefix mapping.
     */
    @WithName("keys.group")
    @WithDefault(EMPTY_LIST)
    List<String> mdcPrefixKeys();

    /**
     * Ignore keys.
     */
    @WithName("keys.ignore")
    @WithDefault(EMPTY_LIST)
    List<String> ignoreKeys();

    /**
     * Override keys.
     */
    @WithName("keys.override")
    @WithDefault(EMPTY_LIST)
    List<String> overrideKeys();

    /**
     * Override keys.
     */
    @WithName("keys.type")
    @WithDefault(EMPTY_LIST)
    List<String> typeKeys();

    /**
     * Environment keys.
     */
    @WithName("keys.env")
    @WithDefault(EMPTY_LIST)
    List<String> envKeys();

    /**
     * Number of characters after which the stacktrace is split. We produce linked messages.
     */
    @WithName("split-stacktrace-after")
    @WithDefault("12000")
    Optional<Integer> splitStacktraceAfter();
}