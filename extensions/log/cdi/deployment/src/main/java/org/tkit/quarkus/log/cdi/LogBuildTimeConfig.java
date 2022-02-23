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
package org.tkit.quarkus.log.cdi.runtime;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

import java.util.List;
import java.util.Optional;

/**
 * Build configuration.
 */
@ConfigRoot(name = "tkit.log")
public class LogBuildTimeConfig {

    /**
     * Enable java types.
     */
    @ConfigItem(name = "auto-discover", defaultValue = "false")
    public boolean autoDiscover;

    /**
     * Binding includes packages.
     */
    @ConfigItem(name = "packages", defaultValue = "org.tkit")
    public List<String> packages;


    /**
     * Specify ignore pattern.
     */
    @ConfigItem(name = "ignore.pattern", defaultValue = "")
    public Optional<String> ignorePattern;

    /**
     * Mdc error key for FBN error code
     */
    @ConfigItem(name = "mdc.errorKey", defaultValue = "errorNumber")
    public String errorNumberKey;

    /**
     * Prefix for custom data mdc entries
     */
    @ConfigItem(name = "customdata.prefix")
    public Optional<String> customDataPrefix;


}
