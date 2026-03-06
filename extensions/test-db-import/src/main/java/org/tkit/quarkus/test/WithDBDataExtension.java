package org.tkit.quarkus.test;

import java.lang.reflect.Method;
import java.net.URL;

import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.test.dbunit.Database;
import org.tkit.quarkus.test.dbunit.FileType;
import org.tkit.quarkus.test.dbunit.LocalDatabase;

import io.quarkus.test.junit.callback.*;

/**
 * This Quarkus Junit5 extension is using the db-import service to import data in the database.
 * The data to for the import needs to be in the class-path.
 * Only the xml data formats are supported.
 *
 * @see WithDBData
 */
public class WithDBDataExtension
        implements QuarkusTestBeforeClassCallback, QuarkusTestAfterAllCallback,
        QuarkusTestBeforeTestExecutionCallback, QuarkusTestAfterTestExecutionCallback {

    /**
     * The logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(WithDBDataExtension.class);

    private static Class<?> TEST_CLASS;

    private static WithDBData TEST_CLASS_ANNOTATION;

    @Override
    public void beforeClass(Class<?> testClass) {

        TEST_CLASS = null;
        TEST_CLASS_ANNOTATION = null;

        WithDBData an = testClass.getAnnotation(WithDBData.class);
        if (an == null) {
            return;
        }

        TEST_CLASS = testClass;
        TEST_CLASS_ANNOTATION = an;

        log.info("[DB-IMPORT] Init class level data for {}", testClass.getName());
        try {
            importAllData(an);
        } catch (Exception ex) {
            throw new RuntimeException("Error with data extension before class error", ex);
        }
    }

    @Override
    public void afterAll(QuarkusTestContext context) {
        if (TEST_CLASS == null || TEST_CLASS_ANNOTATION == null) {
            return;
        }
        try {
            log.debug("[DB-IMPORT] After class level data for {}", TEST_CLASS.getSimpleName());
            deleteAllData(TEST_CLASS_ANNOTATION);
        } catch (Exception ex) {
            throw new RuntimeException("Error with data extension after all error", ex);
        }
    }

    @Override
    public void afterTestExecution(QuarkusTestMethodContext context) {
        Method method = context.getTestMethod();
        try {
            // method annotation
            WithDBData an = method.getAnnotation(WithDBData.class);
            if (an != null) {
                log.debug("[DB-IMPORT] After method level data for {}.{} data-source",
                        method.getDeclaringClass().getSimpleName(), method.getName());
                deleteAllData(an);
                return;
            }
            // class annotation
            WithDBData can = context.getTestInstance().getClass().getAnnotation(WithDBData.class);
            if (can != null && can.rinseAndRepeat()) {
                log.info("[DB-IMPORT] After class level data(Rinse and Repeat) for {}.{}",
                        method.getDeclaringClass().getSimpleName(), method.getName());
                deleteAllData(can);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error with data extension after test execution error", ex);
        }
    }

    @Override
    public void beforeTestExecution(QuarkusTestMethodContext context) {
        try {
            Method method = context.getTestMethod();

            // class import
            WithDBData can = context.getTestInstance().getClass().getAnnotation(WithDBData.class);
            if (can != null && can.rinseAndRepeat()) {
                log.info("[DB-IMPORT] Init class level data(Rinse and Repeat) for {}.{}",
                        method.getDeclaringClass().getSimpleName(), method.getName());
                importAllData(can);
            }

            // method import

            WithDBData an = method.getAnnotation(WithDBData.class);
            if (an != null) {
                log.debug("[DB-IMPORT] Init method level data for {}.{}", method.getDeclaringClass().getSimpleName(),
                        method.getName());
                importAllData(an);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error with data extension before test execution error", ex);
        }
    }

    /**
     * Imports all data defined in the annotation.
     *
     * @param an the with db data annotation.
     */
    private void importAllData(WithDBData an) throws Exception {
        Database db = createDatabase();
        for (int i = 0; i < an.value().length; i++) {

            Database.Request request = create(an, i);
            if (request == null) {
                continue;
            }

            log.debug("[DB-IMPORT] Import data type {} file {} url {}", request.type(), request.path(), request.fileUrl());
            db.insertData(request);
            log.info("[DB-IMPORT] Import data successfully type {} file {}", request.type(), request.path());
        }
    }

    /**
     * Deletes all data defined in the annotation.
     *
     * @param an the with db data annotation.
     */
    private void deleteAllData(WithDBData an) throws Exception {
        Database db = createDatabase();
        for (int i = 0; i < an.value().length; i++) {
            if (!an.deleteAfterTest()) {
                log.debug("[DB-IMPORT] no data deleted after test due to annotation value");
                continue;
            }

            Database.Request request = create(an, i);
            if (request == null) {
                continue;
            }

            log.debug("[DB-IMPORT] Truncate data via DBImport type {} file {} url {}", request.type(), request.path(),
                    request.fileUrl());
            db.deleteData(request);
            log.info("[DB-IMPORT] Truncate data successfully type {} file {}", request.type(), request.path());
        }
    }

    private Database.Request create(WithDBData an, int i) {
        String path = an.value()[i];

        URL fileUrl = this.getClass().getClassLoader().getResource(path);
        if (fileUrl == null) {
            log.warn("[DB-IMPORT] Missing database data resource {} in the class-path.", path);
            return null;
        }
        FileType type = getDataType(path);
        if (type == null) {
            log.warn("[DB-IMPORT] Not supported type for file {}.", path);
            return null;
        }
        return new Database.Request(an, type, path, fileUrl);
    }

    /**
     * Returns {@code DataType} of the path.
     *
     * @param file the url of the file.
     * @return {@code DataType} of the path.
     */
    private static FileType getDataType(String file) {
        if (file == null) {
            return null;
        }
        if (file.endsWith(".xml")) {
            return FileType.XML;
        }
        throw new RuntimeException("Not supported file type!");
    }

    private Database createDatabase() {
        return new LocalDatabase();
    }
}
