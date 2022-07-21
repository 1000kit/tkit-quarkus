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

import static org.tkit.quarkus.log.json.LogJsonConfig.EMPTY_LIST;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Formatter;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class LogJsonRecorder {

    public RuntimeValue<Optional<Formatter>> initializeJsonLogging(final LogJsonConfig config) {
        if (!config.enabled) {
            return new RuntimeValue<>(Optional.empty());
        }
        final JsonAdvancedFormatter formatter = new JsonAdvancedFormatter();
        formatter.setPrettyPrint(config.prettyPrint);
        final String dateFormat = config.dateFormat;
        if (!dateFormat.equals("default")) {
            formatter.setDateFormat(dateFormat);
        }
        formatter.setExceptionOutputType(config.exceptionOutputType);
        formatter.setPrintDetails(config.printDetails);
        config.recordDelimiter.ifPresent(formatter::setRecordDelimiter);
        final String zoneId = config.zoneId;
        if (!zoneId.equals("default")) {
            formatter.setZoneId(zoneId);
        }

        Map<String, String> mdcKeys = convertToMap(config.mdcKeys);
        formatter.addMdcKeys(mdcKeys);

        Map<String, String> mdcPrefix = convertToMap(config.mdcPrefixKeys);
        formatter.addMdcPrefix(mdcPrefix);

        Map<String, String> overrideKeys = convertToMap(config.overrideKeys);
        formatter.addOverrideKeys(overrideKeys);

        Map<String, String> typeKeys = convertToMap(config.typeKeys);
        formatter.addTypeKeys(typeKeys);

        Map<String, String> envKeys = convertToMap(config.envKeys);
        formatter.addEnvKeys(envKeys);

        if (!empty(config.ignoreKeys)) {
            formatter.addIgnoreKeys(new HashSet<>(config.ignoreKeys));
        }

        if (config.splitStacktracesAfter.isPresent()) {
            formatter.setSplitStacktracesAfter(config.splitStacktracesAfter.get());
        }
        return new RuntimeValue<>(Optional.of(formatter));
    }

    private boolean empty(List<String> value) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.size() == 1 && EMPTY_LIST.equals(value.get(0));
    }

    private Map<String, String> convertToMap(List<String> value) {
        if (empty(value)) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>(value.size());
        for (String item : value) {
            String[] tmp = item.split("=");
            if (tmp.length == 1) {
                result.put(tmp[0], tmp[0]);
            } else if (tmp.length > 1) {
                result.put(tmp[0], tmp[1]);
            }
        }
        return result;
    }
}
