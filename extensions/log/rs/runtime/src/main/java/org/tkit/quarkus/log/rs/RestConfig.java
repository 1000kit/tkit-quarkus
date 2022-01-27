/*
 * Copyright 2019 1000kit.org.
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
package org.tkit.quarkus.log.rs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RestConfig {

    public static Map<String, String> convert(String[]  items) {
        if (items == null || items.length == 0) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>();
        for (String item : items) {
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
