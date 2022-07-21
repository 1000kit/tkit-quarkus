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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(prefix = "tkit", phase = ConfigPhase.RUN_TIME, name = "log.json")
public class LogJsonConfig {

    /**
     * Empty list constant.
     */
    public static final String EMPTY_LIST = "<EMPTY-LIST>";

    /**
     * Determine whether to enable the JSON console formatting extension, which disables "normal" console formatting.
     */
    @ConfigItem(name = "enabled", defaultValue = "true")
    boolean enabled;
    /**
     * Enable "pretty printing" of the JSON record. Note that some JSON parsers will fail to read pretty printed output.
     */
    @ConfigItem
    boolean prettyPrint;
    /**
     * The date format to use. The special string "default" indicates that the default format should be used.
     */
    @ConfigItem(defaultValue = "default")
    String dateFormat;
    /**
     * The special end-of-record delimiter to be used. By default, no delimiter is used.
     */
    @ConfigItem
    Optional<String> recordDelimiter;
    /**
     * The zone ID to use. The special string "default" indicates that the default zone should be used.
     */
    @ConfigItem(defaultValue = "default")
    String zoneId;
    /**
     * The exception output type to specify.
     */
    @ConfigItem(defaultValue = "formatted")
    ExtendedStructureFormatter.ExceptionOutputType exceptionOutputType;
    /**
     * Enable printing of more details in the log.
     * <p>
     * Printing the details can be expensive as the values are retrieved from the caller. The details include the
     * source class name, source file name, source method name and source line number.
     */
    @ConfigItem
    boolean printDetails;

    /**
     * Add MDC keys mapping.
     */
    @ConfigItem(name = "keys.mdc", defaultValue = EMPTY_LIST)
    List<String> mdcKeys;

    /**
     * Add MDC prefix mapping.
     */
    @ConfigItem(name = "keys.group", defaultValue = EMPTY_LIST)
    List<String> mdcPrefixKeys;

    /**
     * Ignore keys.
     */
    @ConfigItem(name = "keys.ignore", defaultValue = EMPTY_LIST)
    List<String> ignoreKeys = new ArrayList<>();

    /**
     * Override keys.
     */
    @ConfigItem(name = "keys.override", defaultValue = EMPTY_LIST)
    List<String> overrideKeys = new ArrayList<>();

    /**
     * Override keys.
     */
    @ConfigItem(name = "keys.type", defaultValue = EMPTY_LIST)
    List<String> typeKeys = new ArrayList<>();

    /**
     * Environment keys.
     */
    @ConfigItem(name = "keys.env", defaultValue = EMPTY_LIST)
    List<String> envKeys = new ArrayList<>();

    /**
     * Number of characters after which the stacktrace is split and we produce linked messages
     */
    @ConfigItem(name = "splitStacktracesAfter", defaultValue = "12000")
    Optional<Integer> splitStacktracesAfter;
}
