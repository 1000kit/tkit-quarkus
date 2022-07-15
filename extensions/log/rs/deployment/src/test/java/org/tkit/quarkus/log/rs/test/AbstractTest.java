package org.tkit.quarkus.log.rs.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.util.logging.Formatter;

import org.jboss.logmanager.handlers.WriterHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.quarkus.bootstrap.logging.InitialConfigurator;

public class AbstractTest {

    private static StringWriter writer = new StringWriter();
    private static WriterHandler handler;

    @BeforeAll
    static void setUp() {
        Formatter formatter = InitialConfigurator.DELAYED_HANDLER.getHandlers()[0].getFormatter();
        handler = new WriterHandler();
        handler.setFormatter(formatter);
        handler.setWriter(writer);
        InitialConfigurator.DELAYED_HANDLER.addHandler(handler);
    }

    @BeforeEach
    private void resetLog() {
        writer = new StringWriter();
        handler.setWriter(writer);
    }

    protected String[] logLines() {
        handler.flush();
        String tmp = writer.toString();
        if (tmp.isBlank()) {
            return new String[] {};
        }
        return tmp.split("\n");
    }

    protected AssertLogs assertLogs() {
        return AssertLogs.build(logLines());
    }

    public static class AssertLogs {

        String[] data;

        AssertLogs(String[] data) {
            this.data = data;
        }

        public static AssertLogs build(String[] data) {
            return new AssertLogs(data);
        }

        public AssertLogs assertLines(int count) {
            assertEquals(count, data.length, "Log lines count " + data.length + " expected " + count);
            return this;
        }

        public AssertLogs assertNoEmpty() {
            assertTrue(data.length > 0, "Log is empty. Lines count " + data.length);
            return this;
        }

        public AssertLogs assertNoLogs() {
            assertEquals(0, data.length,
                    "Log is not empty. Lines count " + data.length + ". Lines: \n" + String.join("\t\n", data));
            return this;
        }

        public AssertLogs assertContains(int line, String text) {
            assertTrue(data[line].contains(text),
                    "Assert log line contains \n ==> text: `" + text + "` \n ==> line[" + line + "]: `" + data[line] + "`\n");
            return this;
        }

        public AssertLogs assertMatches(int line, String regex) {
            assertTrue(data[line].matches(regex),
                    "Assert log line does not match \n ==> regex: `" + regex + "` \n ==> line[" + line + "]: `" + data[line]
                            + "`\n");
            return this;
        }
    }
}
