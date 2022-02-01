package org.tkit.quarkus.test;


import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.test.dbunit.FileType;
import org.tkit.quarkus.test.dbunit.Database;
import org.tkit.quarkus.test.dbunit.LocalDatabase;

import java.lang.reflect.Method;
import java.net.URL;

/**
 * This junit5 extension is using the db-import service to import data in the database.
 * The data to for the import needs to be in the class-path.
 * Only the excel and xml data formats are supported.
 * The db-import service configuration property: tkit.test.dbimport.url, default value: http://docker:8811/
 *
 * @see WithDBData
 */
public class WithDBDataExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, BeforeAllCallback, AfterAllCallback {

    /**
     * The logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(WithDBDataExtension.class);

    /**
     * {@inheritDoc }
     */
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Method method = context.getRequiredTestMethod();
        // method annotation
        WithDBData an = method.getAnnotation(WithDBData.class);
        if (an != null) {
            log.debug("[DB-IMPORT] After method level data for {} data-source", method.getName());
            deleteAllData(an);
            return;
        }
        // class annotation
        WithDBData can = context.getRequiredTestClass().getAnnotation(WithDBData.class);
        if (can == null || !can.rinseAndRepeat()) {
            log.debug("[DB-IMPORT] No WithDBData annotation found on class level {}", context.getRequiredTestClass().getName());
            return;
        }

        log.info("[DB-IMPORT] After class level data(Rinse and Repeat) for {}", method.getName());
        deleteAllData(can);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        Object importAll = context.getStore(ExtensionContext.Namespace.GLOBAL).remove(WithDBData.class.getName());
        if (importAll != null) {
            Class<?> clazz = context.getRequiredTestClass();
            WithDBData an = clazz.getAnnotation(WithDBData.class);
            if (an != null) {
                log.info("[DB-IMPORT] Init class level data for {}", clazz.getName());
                importAllData(an);
            }
        }

        // method import
        Method method = context.getRequiredTestMethod();
        WithDBData an = method.getAnnotation(WithDBData.class);
        if (an != null) {
            log.debug("[DB-IMPORT] Init method level data for {}", method.getName());
            importAllData(an);
            return;
        }

        // we import all before
        if (importAll != null) {
            return;
        }

        // class import
        WithDBData can = context.getRequiredTestClass().getAnnotation(WithDBData.class);
        if (can == null || !can.rinseAndRepeat()) {
            log.debug("[DB-IMPORT] No WithDBData annotation found on class level {}", context.getRequiredTestClass().getName());
            return;
        }

        log.info("[DB-IMPORT] Init class level data(Rinse and Repeat) for {}", method.getName());
        importAllData(can);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        Class<?> clazz = context.getRequiredTestClass();
        WithDBData an = clazz.getAnnotation(WithDBData.class);
        if (an == null) {
            return;
        }
        log.debug("[DB-IMPORT] After class level data for {}", clazz.getName());
        deleteAllData(an);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Object tmp = context.getStore(ExtensionContext.Namespace.GLOBAL).get(WithDBDataExtension.class.getName());
        if (tmp == null) {
            context.getStore(ExtensionContext.Namespace.GLOBAL).put(WithDBDataExtension.class.getName(), Boolean.TRUE.toString());
            context.getStore(ExtensionContext.Namespace.GLOBAL).put(WithDBData.class.getName(), Boolean.TRUE.toString());
            return;
        }

        Class<?> clazz = context.getRequiredTestClass();
        WithDBData an = clazz.getAnnotation(WithDBData.class);
        if (an == null) {
            return;
        }

        log.info("[DB-IMPORT] Init class level data for {}", clazz.getName());
        importAllData(an);
    }

    private Database createDatabase(WithDBData an) {
        return new LocalDatabase();
    }

    /**
     * Imports all data defined in the annotation.
     *
     * @param an the with db data annotation.
     */
    private void importAllData(WithDBData an) throws Exception {
        Database db = createDatabase(an);
        for (int i = 0; i < an.value().length; i++) {
            String path = an.value()[i];

            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL fileUrl = cl.getResource(path);
            if (fileUrl == null) {
                log.warn("[DB-IMPORT] Missing database data resource {} in the class-path.", path);
                continue;
            }

            FileType type = FileType.getDataType(path);

            log.info("[DB-IMPORT] Import data type {} file {}", type, path);
            log.debug("[DB-IMPORT] File URL: {}", fileUrl);
            db.insertData(an, type, path);
            log.info("[DB-IMPORT] Import data successfully type {} file {}", type, path);
        }
    }

    /**
     * Deletes all data defined in the annotation.
     *
     * @param an the with db data annotation.
     */
    private void deleteAllData(WithDBData an) throws Exception {
        Database db = createDatabase(an);
        for (int i = 0; i < an.value().length; i++) {
            if (!an.deleteAfterTest()) {
                log.debug("[DB-IMPORT] no data deleted after test due to annotation value");
                continue;
            }

            String path = an.value()[i];

            URL fileUrl = this.getClass().getClassLoader().getResource(path);
            if (fileUrl == null) {
                log.warn("[DB-IMPORT] Missing database data resource {} in the class-path.", path);
                continue;
            }

            FileType type = FileType.getDataType(path);
            if (type == null) {
                log.warn("[DB-IMPORT] Not supported type for file {}.", path);
                continue;
            }

            log.info("[DB-IMPORT] Truncate data via DBImport type {} file {}", type, fileUrl);
            db.deleteData(an, type, path);
            log.info("[DB-IMPORT] Truncate data successfully type {} file {}", type, fileUrl);
        }
    }

}
