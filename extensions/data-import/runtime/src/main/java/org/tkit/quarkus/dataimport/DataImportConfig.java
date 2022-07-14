package org.tkit.quarkus.dataimport;

import java.nio.file.Path;
import java.util.Map;

public class DataImportConfig {

    String key;
    byte[] data;
    Path file;
    String md5;
    Map<String, String> metadata;

    public String getKey() {
        return key;
    }

    public byte[] getData() {
        return data;
    }

    public Path getFile() {
        return file;
    }

    public String getMD5() {
        return md5;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "Config{" +
                "key='" + key + '\'' +
                ",file=" + file +
                ",md5='" + md5 + '\'' +
                ",metadata=" + metadata +
                '}';
    }
}
