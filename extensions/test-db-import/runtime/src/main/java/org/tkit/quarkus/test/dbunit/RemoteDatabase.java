package org.tkit.quarkus.test.dbunit;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;
import org.tkit.quarkus.test.WithDBData;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

public class RemoteDatabase implements Database {

    private static final Logger log = Logger.getLogger(RemoteDatabase.class);

    @Override
    public void deleteData(WithDBData ano, FileType type, String file) throws Exception {
        request("db/teardown/", ano, type, file, Map.of());
    }

    @Override
    public void insertData(WithDBData ano, FileType type, String file) throws Exception {
        request("db/import/", ano, type, file, Map.of("cleanBefore", "" + ano.deleteBeforeInsert()));
    }

    private void request(String path, WithDBData ano, FileType type, String file, Map<String, String> query) {
        RequestSpecification reqSpec = new RequestSpecBuilder()
                .setBaseUri(System.getProperty("quarkus.tkit.db-import.url"))
                .build();

        URL fileUrl = this.getClass().getClassLoader().getResource(file);
        if (fileUrl == null) {
            log.warnf("[DB-IMPORT] Missing database data resource %s in the class-path.", file);
            return;
        }

        RemoteType remoteType = RemoteType.from(type);

        RestAssured.given().spec(reqSpec)
                .contentType(remoteType.getContentType())
                .body(createFile(fileUrl))
                .log().ifValidationFails()
                .queryParams(query)
                .when()
                .post(path + remoteType.path)
                .then()
                .log().ifValidationFails()
                .statusCode(200);
    }

    /**
     * Creates the file object from the URL.
     *
     * @param fileUrl the file URL.
     * @return the corresponding file object.
     */
    private static File createFile(URL fileUrl) {
        try {
            return new File(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Wrong URI format. " + fileUrl, e);
        }
    }

    public enum RemoteType {

        XLS("application/excel", "excel"),

        XML("application/xml", "xml");

        private String contentType;

        private String path;

        RemoteType(String contentType, String path) {
            this.contentType = contentType;
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public String getContentType() {
            return contentType;
        }

        public static RemoteType from(FileType type) {
            return RemoteType.valueOf(type.name());
        }
    }
}
