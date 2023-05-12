package org.tkit.quarkus.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Declares that this test execution requires data import before it is started. This requires a test deployment with DBImport
 * enabled - @link DeploymentBuilder#withDBImportFeature()
 *
 * If you put this annotation on class, then by default the import will be executed before 1st test in this class, and cleanup
 * after last test. If you specify the <code>rinseAndRepeat</code> flag, then it will be executed for every test method.
 *
 * Value should be arrayed of string representing paths to {@code .xls} files with DBUnit data or directories with {@code .csv}
 * files, relative to maven class root(src/test/resources|src/test/java).
 *
 * Example: <code>@WithDBData("data/test.xls")</code> would try to find a file
 * <code>PROJECT_ROOT/src/test/resources/data/test.xls</code> or
 * <code>PROJECT_ROOT/src/test/java/data/test.xls</code>
 *
 * If you want to import csv files, they must be stored in folder named "/csv". Apart form this remember about creating
 * table-ordering.txt file in "/csv" where you specify an order in which data should be imported
 *
 */
@ExtendWith(WithDBDataExtension.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
@Inherited
public @interface WithDBData {

    /**
     * Paths to DBUnit xml, xls files or csv directories
     *
     * @return relative path to the import file
     */
    String[] value();

    /**
     * Import data to datasource. As default the default datasource will be use.
     * Will be ignored for remote import.
     *
     * @return the datasource for the import
     */
    String datasource() default "default";

    /**
     * Should the existing data be deleted in the table that is too imported?
     *
     * @return true if data should be deleted, false otherwise
     */
    boolean deleteBeforeInsert() default false;

    /**
     * Should the test data in all affected tables be deleted after test?
     *
     * @return true if data should be deleted, false otherwise
     */
    boolean deleteAfterTest() default false;

    /**
     * Should the DB data import be executed for every test method in this class? Same as annotating every method with this
     * annotation.
     *
     * @return true if db import should be executed for every test case method again, false otherwise
     */
    boolean rinseAndRepeat() default false;

    /**
     * "column sensing" which basically reads in the whole XML into a buffer and dynamically adds new columns as they appear.
     *
     * @return true if column sensing is activated.
     */
    boolean columnSensing() default true;
}
