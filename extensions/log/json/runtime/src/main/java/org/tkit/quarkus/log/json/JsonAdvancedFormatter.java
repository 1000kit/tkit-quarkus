/*
 * Copyright 2018 Red Hat, Inc.
 * Copyright 2020 1000kit.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tkit.quarkus.log.json;

import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.PropertyValues;

/**
 * A formatter that outputs the record into JSON format optionally printing details.
 * <p>
 * Note that including details can be expensive in terms of calculating the caller.
 * </p>
 * <p>The details include;</p>
 * <ul>
 * <li>{@link org.jboss.logmanager.ExtLogRecord#getSourceClassName() source class name}</li>
 * <li>{@link org.jboss.logmanager.ExtLogRecord#getSourceFileName() source file name}</li>
 * <li>{@link org.jboss.logmanager.ExtLogRecord#getSourceMethodName() source method name}</li>
 * <li>{@link org.jboss.logmanager.ExtLogRecord#getSourceLineNumber() source line number}</li>
 * <li>{@link org.jboss.logmanager.ExtLogRecord#getSourceModuleName() source module name}</li>
 * <li>{@link org.jboss.logmanager.ExtLogRecord#getSourceModuleVersion() source module version}</li>
 * </ul>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class JsonAdvancedFormatter extends ExtendedStructureFormatter {

    private final Map<String, Object> config;

    private JsonGeneratorFactory factory;

    private final Map<String,String> mdcKeys = new HashMap<>();

    private final Map<String,String> mdcPrefix = new HashMap<>();

    private final Map<String,String> overrideKeys = new HashMap<>();

    private final Map<String,String> typeKeys = new HashMap<>();

    private final Set<String> ignoreKeys = new HashSet<>();

    private final Map<String,String> envKeys = new HashMap<>();

    /**
     * Creates a new JSON formatter.
     */
    public JsonAdvancedFormatter() {
        config = new HashMap<>();
        factory = Json.createGeneratorFactory(config);
    }

    /**
     * Creates a new JSON formatter.
     *
     * @param keyOverrides a string representation of a map to override keys
     *
     * @see PropertyValues#stringToEnumMap(Class, String)
     */
    public JsonAdvancedFormatter(final String keyOverrides) {
        super(keyOverrides);
        config = new HashMap<>();
        factory = Json.createGeneratorFactory(config);
    }

    /**
     * Creates a new JSON formatter.
     *
     * @param keyOverrides a map of overrides for the default keys
     */
    public JsonAdvancedFormatter(final Map<Key, String> keyOverrides) {
        super(keyOverrides);
        config = new HashMap<>();
        factory = Json.createGeneratorFactory(config);
    }

    public void addEnvKeys(Map<String, String> envKeys) {
        if (envKeys != null) {
            this.envKeys.putAll(envKeys);
        }
    }

    public void addMdcKeys(Map<String, String> mdcKeys) {
        if (mdcKeys != null) {
            this.mdcKeys.putAll(mdcKeys);
        }
    }

    public void addMdcPrefix(Map<String, String> mdcPrefix) {
        if (mdcPrefix != null) {
            this.mdcPrefix.putAll(mdcPrefix);
        }
    }

    public void addIgnoreKeys(Set<String> ignoreKeys) {
        if (ignoreKeys != null) {
            this.ignoreKeys.addAll(ignoreKeys);
        }
    }

    public void addOverrideKeys(Map<String, String> overrideKeys) {
        if (overrideKeys != null) {
            this.overrideKeys.putAll(overrideKeys);
        }
    }

    public void addTypeKeys(Map<String, String> typeKeys) {
        if (typeKeys != null) {
            this.typeKeys.putAll(typeKeys);
        }
    }

    /**
     * Indicates whether or not pretty printing is enabled.
     *
     * @return {@code true} if pretty printing is enabled, otherwise {@code false}
     */
    public boolean isPrettyPrint() {
        synchronized (config) {
            return (config.containsKey(javax.json.stream.JsonGenerator.PRETTY_PRINTING) ? (Boolean) config.get(javax.json.stream.JsonGenerator.PRETTY_PRINTING) : false);
        }
    }

    /**
     * Turns on or off pretty printing.
     *
     * @param prettyPrint {@code true} to turn on pretty printing or {@code false} to turn it off
     */
    public void setPrettyPrint(final boolean prettyPrint) {
        synchronized (config) {
            if (prettyPrint) {
                config.put(JsonGenerator.PRETTY_PRINTING, true);
            } else {
                config.remove(JsonGenerator.PRETTY_PRINTING);
            }
            factory = Json.createGeneratorFactory(config);
        }
    }

    protected void after(final Generator generator, final ExtLogRecord record) throws Exception {
        if (!envKeys.isEmpty()) {
            for (Map.Entry<String,String> item : envKeys.entrySet()) {
                generator.add(item.getKey(), System.getenv(item.getValue()));
            }
        }
    }

    @Override
    protected Generator createGenerator(final Writer writer) {
        final JsonGeneratorFactory factory;
        synchronized (config) {
            factory = this.factory;
        }
        return new FormatterJsonGenerator(factory.createGenerator(writer));
    }

    private class FormatterJsonGenerator implements Generator {
        private final JsonGenerator generator;

        private FormatterJsonGenerator(final JsonGenerator generator) {
            this.generator = generator;
        }

        @Override
        public Generator begin() {
            generator.writeStartObject();
            return this;
        }

        @Override
        public Generator add(final String key, final int value) {
            if (ignoreKeys.contains(key)) {
                return this;
            }
            generator.write(overrideKeys.getOrDefault(key, key), value);
            return this;
        }

        @Override
        public Generator add(final String key, final long value) {
            if (ignoreKeys.contains(key)) {
                return this;
            }
            generator.write(overrideKeys.getOrDefault(key, key), value);
            return this;
        }

        @Override
        public Generator add(final String key, final Map<String, ?> value) {
            if (ignoreKeys.contains(key)) {
                return this;
            }
            if (Key.MDC.getKey().equals(key)) {
                if (!mdcKeys.isEmpty()) {
                    for (Map.Entry<String, String> e : mdcKeys.entrySet()) {
                        Object tmp = value.remove(e.getKey());
                        if (tmp != null) {
                            writeObject(e.getValue(), tmp);
                        }
                    }
                }
                if (!mdcPrefix.isEmpty()) {
                    for (Map.Entry<String, String> e : mdcPrefix.entrySet()) {
                        String k = e.getKey();
                        int length = k.length();

                        Map<String,Object> data = new HashMap<>();
                        Map<String,Object> tmp = new HashMap<>(value);
                        for (String pk : tmp.keySet()) {
                            if (pk.startsWith(k)) {
                                data.put(pk.substring(length), value.remove(pk));
                            }
                        }
                        if (!data.isEmpty()) {
                            addObject(e.getValue(), data);
                        }
                    }
                }
            }
            addObject(key, value);
            return this;
        }

        @Override
        public Generator addJsonString(final String key, final String value) {
            try (StringReader reader = new StringReader(value)){
                generator.write(key, Json.createReader(reader).readValue());
            } catch (Exception e) {
                //write it at least as string
                return add(key, value);
            }
            return this;
        }

        private void addObject(String key, Map<String, ?> value) {
            generator.writeStartObject(key);
            if (value != null) {
                for (Map.Entry<String, ?> entry : value.entrySet()) {
                    writeObject(entry.getKey(), entry.getValue());
                }
            }
            generator.writeEnd();
        }

        @Override
        public Generator add(final String key, final String value) {
            if (ignoreKeys.contains(key)) {
                return this;
            }
            if (value == null) {
                generator.writeNull(overrideKeys.getOrDefault(key, key));
            } else {
                generator.write(overrideKeys.getOrDefault(key, key), value);
            }
            return this;
        }

        @Override
        public Generator startObject(final String key) throws Exception {
            if (key == null) {
                generator.writeStartObject();
            } else {
                generator.writeStartObject(overrideKeys.getOrDefault(key, key));
            }
            return this;
        }

        @Override
        public Generator endObject() throws Exception {
            generator.writeEnd();
            return this;
        }

        @Override
        public Generator startArray(final String key) throws Exception {
            if (key == null) {
                generator.writeStartArray();
            } else {
                generator.writeStartArray(overrideKeys.getOrDefault(key, key));
            }
            return this;
        }

        @Override
        public Generator endArray() throws Exception {
            generator.writeEnd();
            return this;
        }

        @Override
        public Generator end() {
            generator.writeEnd(); // end record
            generator.flush();
            generator.close();
            return this;
        }

        private void writeObject(String key, Object obj) {
            if (ignoreKeys.contains(key)) {
                return;
            }

            // switch type
            String type = typeKeys.get(key);
            if (type != null) {
                switch (type) {
                    case "long":
                        obj = Long.valueOf((String) obj);
                        break;
                    case "int":
                        obj = Integer.valueOf((String) obj);
                        break;
                    case "double":
                        obj = Double.valueOf((String) obj);
                        break;
                    default:
                        System.out.println("tkit-quarkus-log-json wrong field format override key: "  + key + " type: " + type);
                }
            }

            // override key name
            key = overrideKeys.getOrDefault(key, key);


            if (obj == null) {
                if (key == null) {
                    generator.writeNull();
                } else {
                    generator.writeNull(key);
                }
            } else if (obj instanceof Boolean) {
                final Boolean value = (Boolean) obj;
                if (key == null) {
                    generator.write(value);
                } else {
                    generator.write(key, value);
                }
            } else if (obj instanceof Integer) {
                final Integer value = (Integer) obj;
                if (key == null) {
                    generator.write(value);
                } else {
                    generator.write(key, value);
                }
            } else if (obj instanceof Long) {
                final Long value = (Long) obj;
                if (key == null) {
                    generator.write(value);
                } else {
                    generator.write(key, value);
                }
            } else if (obj instanceof Double) {
                final Double value = (Double) obj;
                if (key == null) {
                    generator.write(value);
                } else {
                    generator.write(key, value);
                }
            } else if (obj instanceof BigInteger) {
                final BigInteger value = (BigInteger) obj;
                if (key == null) {
                    generator.write(value);
                } else {
                    generator.write(key, value);
                }
            } else if (obj instanceof BigDecimal) {
                final BigDecimal value = (BigDecimal) obj;
                if (key == null) {
                    generator.write(value);
                } else {
                    generator.write(key, value);
                }
            } else if (obj instanceof String) {
                final String value = (String) obj;
                if (key == null) {
                    generator.write(value);
                } else {
                    generator.write(key, value);
                }
            } else if (obj instanceof JsonValue) {
                final JsonValue value = (JsonValue) obj;
                if (key == null) {
                    generator.write(value);
                } else {
                    generator.write(key, value);
                }
            } else {
                final String value = String.valueOf(obj);
                if (key == null) {
                    generator.write(value);
                } else {
                    generator.write(key, value);
                }
            }
        }
    }
}
