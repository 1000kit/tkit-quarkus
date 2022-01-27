package org.tkit.quarkus.test.dbunit;

import org.tkit.quarkus.test.WithDBData;

public interface Database {

    void deleteData(WithDBData ano, FileType type, String file) throws Exception;

    void insertData(WithDBData ano, FileType type, String file) throws Exception;

}
