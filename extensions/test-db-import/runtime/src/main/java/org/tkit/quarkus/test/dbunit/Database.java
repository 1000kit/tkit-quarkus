package org.tkit.quarkus.test.dbunit;

import java.net.URL;

import org.tkit.quarkus.test.WithDBData;

public interface Database {

    void deleteData(Request request) throws Exception;

    void insertData(Request request) throws Exception;

    record Request(WithDBData ano, FileType type, String path, URL fileUrl) {
    }
}
