package org.tkit.quarkus.log.json;

import java.io.PrintWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.LogRecord;

import org.jboss.logmanager.ExtFormatter;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.PropertyValues;
import org.jboss.logmanager.formatters.StructuredFormatter;
import org.tkit.quarkus.log.json.tagging.TaggedMessage;
import org.tkit.quarkus.log.json.util.ClassNameAbbreviationUtil;

/**
 * Custom structured formatter because the original {@link StructuredFormatter} has final format methods, sigh
 *
 */
@SuppressWarnings({ "unused", "WeakerAccess" })
public abstract class ExtendedStructureFormatter extends ExtFormatter {

    /**
     * The key used for the structured log record data.
     */
    public enum Key {
        EXCEPTION("exception"),
        EXCEPTION_CAUSED_BY("causedBy"),
        EXCEPTION_CIRCULAR_REFERENCE("circularReference"),
        EXCEPTION_TYPE("exceptionType"),
        EXCEPTION_FRAME("frame"),
        EXCEPTION_FRAME_CLASS("class"),
        EXCEPTION_FRAME_LINE("line"),
        EXCEPTION_FRAME_METHOD("method"),
        EXCEPTION_FRAMES("frames"),
        EXCEPTION_MESSAGE("message"),
        EXCEPTION_REFERENCE_ID("refId"),
        EXCEPTION_SUPPRESSED("suppressed"),
        HOST_NAME("hostName"),
        LEVEL("level"),
        LOGGER_CLASS_NAME("loggerClassName"),
        LOGGER_NAME("loggerName"),
        MDC("mdc"),
        MESSAGE("message"),
        NDC("ndc"),
        PROCESS_ID("processId"),
        PROCESS_NAME("processName"),
        RECORD("record"),
        SEQUENCE("sequence"),
        SOURCE_CLASS_NAME("sourceClassName"),
        SOURCE_FILE_NAME("sourceFileName"),
        SOURCE_LINE_NUMBER("sourceLineNumber"),
        SOURCE_METHOD_NAME("sourceMethodName"),
        SOURCE_MODULE_NAME("sourceModuleName"),
        SOURCE_MODULE_VERSION("sourceModuleVersion"),
        STACK_TRACE("stackTrace"),
        THREAD_ID("threadId"),
        THREAD_NAME("threadName"),
        TIMESTAMP("timestamp");

        private final String key;

        Key(final String key) {
            this.key = key;
        }

        /**
         * Returns the name of the key for the structure.
         *
         * @return the name of they key
         */
        public String getKey() {
            return key;
        }
    }

    /**
     * Defines the way a cause will be formatted.
     */
    public enum ExceptionOutputType {
        /**
         * The cause, if present, will be an array of stack trace elements. This will include suppressed exceptions and
         * the {@linkplain Throwable#getCause() cause} of the exception.
         */
        DETAILED,
        /**
         * The cause, if present, will be a string representation of the stack trace in a {@code stackTrace} property.
         * The property value is a string created by {@link Throwable#printStackTrace()}.
         */
        FORMATTED,
        /**
         * The cause, if present, will be a string representation of the stack trace in a {@code stackTrace} property, with
         * classnames abbreviated.
         * The property value is a string created by {@link Throwable#printStackTrace() with classnames abbreviated}.
         */
        FORMATTED_BRIEF,
        /**
         * Adds both the {@link #DETAILED} and {@link #FORMATTED}
         */
        DETAILED_AND_FORMATTED
    }

    private final Map<Key, String> keyOverrides;
    private final String keyOverridesValue;
    private volatile boolean printDetails;
    private volatile String eorDelimiter = "\n";
    // Guarded by this
    private DateTimeFormatter dateTimeFormatter;
    // Guarded by this
    private ZoneId zoneId;
    private volatile ExceptionOutputType exceptionOutputType;
    private final StringBuilderWriter writer = new StringBuilderWriter();
    // Guarded by this
    private int refId;

    //tkit-quarkus-log custom fields
    //Should env vars be printed
    private String printEnvVars;
    //Which keys we print
    private Set<String> printEnvVarKeys = new HashSet<>();
    //number of characters, after which we split the stacktraces and create linked messages
    private int splitStacktracesAfter;

    protected ExtendedStructureFormatter() {
        this(null, null);
    }

    protected ExtendedStructureFormatter(final Map<Key, String> keyOverrides) {
        this(keyOverrides, PropertyValues.mapToString(keyOverrides));
    }

    protected ExtendedStructureFormatter(final String keyOverrides) {
        this(PropertyValues.stringToEnumMap(Key.class, keyOverrides), keyOverrides);
    }

    private ExtendedStructureFormatter(final Map<Key, String> keyOverrides, final String keyOverridesValue) {
        this.keyOverridesValue = keyOverridesValue;
        this.printDetails = false;
        zoneId = ZoneId.systemDefault();
        dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zoneId);
        this.keyOverrides = (keyOverrides == null ? Collections.emptyMap() : new EnumMap<>(keyOverrides));
        exceptionOutputType = ExceptionOutputType.DETAILED;
    }

    /**
     * Creates the generator used to create the structured data.
     *
     * @return the generator to use
     *
     * @throws Exception if an error occurs creating the generator
     */
    protected abstract Generator createGenerator(Writer writer) throws Exception;

    /**
     * Invoked before the structured data is added to the generator.
     *
     * @param generator the generator to use
     * @param record the log record
     */
    protected void before(final Generator generator, final ExtLogRecord record) throws Exception {
        // do nothing
    }

    /**
     * Invoked after the structured data has been added to the generator.
     *
     * @param generator the generator to use
     * @param record the log record
     */
    protected void after(final Generator generator, final ExtLogRecord record) throws Exception {
        // do nothing
    }

    /**
     * Checks to see if the key should be overridden.
     *
     * @param defaultKey the default key
     *
     * @return the overridden key or the default key if no override exists
     */
    protected final String getKey(final Key defaultKey) {
        if (keyOverrides.containsKey(defaultKey)) {
            return keyOverrides.get(defaultKey);
        }
        return defaultKey.getKey();
    }

    @Override
    public final synchronized String format(final ExtLogRecord record) {
        final boolean details = printDetails;
        try {
            String[] stacktraceChunks = null;
            final Generator generator = createGenerator(writer).begin();
            before(generator, record);
            outputBaseKeys(record, generator);

            // Add the cause of the log message if applicable
            final Throwable thrown = record.getThrown();
            if (thrown != null) {
                if (isDetailedExceptionOutputType()) {
                    refId = 0;
                    final Map<Throwable, Integer> seen = new IdentityHashMap<>();
                    generator.startObject(getKey(Key.EXCEPTION));
                    addException(generator, thrown, seen);
                    generator.endObject();
                }

                if (isFormattedExceptionOutputType()) {
                    final StringBuilderWriter w = new StringBuilderWriter();
                    thrown.printStackTrace(new PrintWriter(w));
                    stacktraceChunks = outputStacktrace(generator, w);
                }

                if (isFormattedBriefExceptionType()) {
                    final Map<Throwable, Integer> seen = new IdentityHashMap<>();
                    final StringBuilderWriter w = new StringBuilderWriter();
                    printStackTraceWithAbbreviatedClasses(thrown, w, seen);
                    stacktraceChunks = outputStacktrace(generator, w);
                }
            }
            if (details) {
                generator.add(getKey(Key.SOURCE_CLASS_NAME), record.getSourceClassName())
                        .add(getKey(Key.SOURCE_FILE_NAME), record.getSourceFileName())
                        .add(getKey(Key.SOURCE_METHOD_NAME), record.getSourceMethodName())
                        .add(getKey(Key.SOURCE_LINE_NUMBER), record.getSourceLineNumber());
            }

            after(generator, record);
            generator.end();

            // Append an EOL character if desired
            if (getRecordDelimiter() != null) {
                writer.append(getRecordDelimiter());
            }

            if (stacktraceChunks != null) {
                for (int i = 1; i < stacktraceChunks.length; i++) {
                    final Generator partialGenerator = createGenerator(writer).begin();
                    outputBaseKeys(record, partialGenerator);
                    partialGenerator.add(getKey(Key.STACK_TRACE), stacktraceChunks[i]);
                    partialGenerator.end();
                    // Append an EOL character if desired
                    if (getRecordDelimiter() != null) {
                        writer.append(getRecordDelimiter());
                    }
                }
            }

            return writer.toString();
        } catch (Exception e) {
            // Wrap and rethrow
            throw new RuntimeException(e);
        } finally {
            // Clear the writer for the next format
            writer.clear();
        }
    }

    private String[] outputStacktrace(Generator generator, StringBuilderWriter writer) throws Exception {
        String[] stacktraceChunks = null;
        String stacktrace = writer.toString();
        if (splitStacktracesAfter > 0) { //if spliting is enabled
            stacktraceChunks = splitStacktrace(stacktrace);
            stacktrace = stacktraceChunks[0];
        }
        generator.add(getKey(Key.STACK_TRACE), stacktrace);
        return stacktraceChunks;
    }

    private String[] splitStacktrace(String stacktrace) {
        if (stacktrace.length() > splitStacktracesAfter) {
            int read = 0;
            int partCount = (stacktrace.length() / splitStacktracesAfter) + 1;
            String[] result = new String[partCount];
            for (int i = 0; i < partCount; i++) {
                int upperBound = (i + 1) * splitStacktracesAfter;
                if (upperBound > stacktrace.length()) {
                    upperBound = stacktrace.length();
                }
                result[i] = "PART " + (i + 1) + " of " + partCount + " : "
                        + stacktrace.substring(i * splitStacktracesAfter, upperBound);
            }
            return result;
        } else {
            return new String[] { stacktrace };
        }
    }

    private void outputBaseKeys(ExtLogRecord record, Generator generator) throws Exception {
        // Add the default structure
        generator.add(getKey(Key.TIMESTAMP), dateTimeFormatter.format(Instant.ofEpochMilli(record.getMillis())))
                .add(getKey(Key.SEQUENCE), record.getSequenceNumber())
                .add(getKey(Key.LOGGER_CLASS_NAME), record.getLoggerClassName())
                .add(getKey(Key.LOGGER_NAME), record.getLoggerName())
                .add(getKey(Key.LEVEL), record.getLevel().getName())
                .add(getKey(Key.THREAD_NAME), record.getThreadName())
                .add(getKey(Key.THREAD_ID), record.getThreadID())
                .add(getKey(Key.MDC), record.getMdcCopy())
                .add(getKey(Key.NDC), record.getNdc());

        TaggedMessage taggedMessage = new TaggedMessage(record.getMessage());
        switch (taggedMessage.getMessageTypeTag()) {
            case REGULAR_MESSAGE:
                generator.add(getKey(Key.MESSAGE), record.getFormattedMessage());
                break;
            case CUSTOM_ATTRIBUTE_JSON_STRING_MESSAGE:
                generator.addJsonString(taggedMessage.getNameOfAttributeTag(), taggedMessage.getMessageAfterTagsRemoval());
                break;
            case CUSTOM_ATTRIBUTE_STRING_MESSAGE:
                generator.add(taggedMessage.getNameOfAttributeTag(), taggedMessage.getMessageAfterTagsRemoval());
                break;
        }
        //not nice, but not sure how else to do it
        if (record.getMdcCopy().containsKey("TRACE_ID")) {
            generator.add("X-Span-Export", "true");
        }

        if (!printEnvVarKeys.isEmpty()) {
            for (String key : printEnvVarKeys) {
                generator.add(key, System.getenv(key));
            }
        }
    }

    private void printStackTraceWithAbbreviatedClasses(Throwable throwable, StringBuilderWriter w,
            final Map<Throwable, Integer> seen) {
        if (throwable == null) {
            return;
        }
        if (seen.containsKey(throwable)) {
            w.append("EXCEPTION_CIRCULAR_REFERENCE: " + seen.get(throwable) + "\n");
            w.append("EXCEPTION_MESSAGE" + throwable.getMessage());
        } else {
            final int id = ++refId;
            seen.put(throwable, id);
            w.append(throwable.getClass().getName());
            w.append(": " + throwable.getMessage());

            final StackTraceElement[] elements = throwable.getStackTrace();
            addAbbreviatedStackTraceElements(w, elements);

            // Render the suppressed messages
            final Throwable[] suppressed = throwable.getSuppressed();
            if (suppressed != null && suppressed.length > 0) {
                w.append("\nEXCEPTION_SUPPRESSED: ");
                for (Throwable s : suppressed) {
                    printStackTraceWithAbbreviatedClasses(s, w, seen);
                }

            }

            // Render the cause
            final Throwable cause = throwable.getCause();
            if (cause != null) {
                w.append("\nCaused by: ");
                printStackTraceWithAbbreviatedClasses(cause, w, seen);
            }
        }
    }

    private String formatStacktraceElementShort(StringBuilderWriter w, StackTraceElement e) {
        return (ClassNameAbbreviationUtil.abbreviateClassName(e.getClassName()) + "." + e.getMethodName() +
                (e.isNativeMethod() ? "(Native Method)" : "(" + e.getLineNumber() + ")"));
    }

    private boolean isFormattedBriefExceptionType() {
        return exceptionOutputType == ExceptionOutputType.FORMATTED_BRIEF;
    }

    protected void addAbbreviatedStackTraceElements(StringBuilderWriter w, StackTraceElement[] elements) {
        for (StackTraceElement e : elements) {
            w.append("\n\t" + formatStacktraceElementShort(w, e));
        }
    }

    @Override
    public boolean isCallerCalculationRequired() {
        return isPrintDetails();
    }

    /**
     * A string representation of the key overrides. The default is {@code null}.
     *
     * @return a string representation of the key overrides or {@code null} if no overrides were configured
     */
    public String getKeyOverrides() {
        return keyOverridesValue;
    }

    /**
     * Returns the character used to indicate the record has is complete. This defaults to {@code \n} and may be
     * {@code null} if no end of record character is desired.
     *
     * @return the end of record delimiter or {@code null} if no delimiter is desired
     */
    public String getRecordDelimiter() {
        return eorDelimiter;
    }

    /**
     * Sets the value to be used to indicate the end of a record. If set to {@code null} no delimiter will be used at
     * the end of the record.
     *
     * @param eorDelimiter the delimiter to be used or {@code null} to not use a delimiter
     */
    public void setRecordDelimiter(final String eorDelimiter) {
        this.eorDelimiter = eorDelimiter;
    }

    /**
     * Returns the current formatter used to format a records date and time.
     *
     * @return the current formatter
     */
    public synchronized DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    /**
     * Sets the pattern to use when formatting the date. The pattern must be a valid
     * {@link java.time.format.DateTimeFormatter#ofPattern(String)} pattern.
     * <p>
     * If the pattern is {@code null} a default {@linkplain DateTimeFormatter#ISO_OFFSET_DATE_TIME formatter} will be
     * used. The {@linkplain #setZoneId(String) zone id} will always be appended to the formatter. By default the zone
     * id will default to the {@linkplain ZoneId#systemDefault() systems zone id}.
     * </p>
     *
     * @param pattern the pattern to use or {@code null} to use a default pattern
     */
    public synchronized void setDateFormat(final String pattern) {
        if (pattern == null) {
            dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zoneId);
        } else {
            dateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withZone(zoneId);
        }
    }

    /**
     * Returns the current zone id used for the {@linkplain #getDateTimeFormatter() date and time formatter}.
     *
     * @return the current zone id
     */
    public synchronized ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * Sets the {@link ZoneId} to use when formatting the date and time from the {@link java.util.logging.LogRecord}.
     * <p>
     * The rules of the id must conform to the rules specified on {@link ZoneId#of(String)}.
     * </p>
     *
     * @param zoneId the zone id or {@code null} to use the {@linkplain ZoneId#systemDefault() system default}
     *
     * @see ZoneId#of(String)
     */
    public void setZoneId(final String zoneId) {
        final ZoneId changed;
        if (zoneId == null) {
            changed = ZoneId.systemDefault();
        } else {
            changed = ZoneId.of(zoneId);
        }
        synchronized (this) {
            this.zoneId = changed;
            dateTimeFormatter = dateTimeFormatter.withZone(changed);
        }
    }

    /**
     * Indicates whether or not details should be printed.
     *
     * @return {@code true} if details should be printed, otherwise {@code false}
     */
    public boolean isPrintDetails() {
        return printDetails;
    }

    /**
     * Sets whether or not details should be printed.
     * <p>
     * Printing the details can be expensive as the values are retrieved from the caller. The details include the
     * source class name, source file name, source method name and source line number.
     * </p>
     *
     * @param printDetails {@code true} if details should be printed
     */
    public void setPrintDetails(@SuppressWarnings("SameParameterValue") final boolean printDetails) {
        this.printDetails = printDetails;
    }

    /**
     * Get the current output type for exceptions.
     *
     * @return the output type for exceptions
     */
    public ExceptionOutputType getExceptionOutputType() {
        return exceptionOutputType;
    }

    public String getPrintEnvVars() {
        return printEnvVars;
    }

    public void setPrintEnvVars(String printEnvVars) {
        System.out.println("JSONLOGGER set printEnvVars: " + printEnvVars);
        this.printEnvVars = printEnvVars;
        if (printEnvVars != null) {
            printEnvVarKeys.addAll(Arrays.asList(printEnvVars.split(",")));
        }
    }

    public int getSplitStacktracesAfter() {
        return splitStacktracesAfter;
    }

    public void setSplitStacktracesAfter(int splitStacktracesAfter) {
        System.out.println("JSONLOGGER set stacktraceSplitting: " + splitStacktracesAfter);
        this.splitStacktracesAfter = splitStacktracesAfter;
    }

    /**
     * Set the output type for exceptions. The default is {@link ExceptionOutputType#DETAILED DETAILED}.
     *
     * @param exceptionOutputType the desired output type, if {@code null} {@link ExceptionOutputType#DETAILED} is used
     */
    public void setExceptionOutputType(final ExceptionOutputType exceptionOutputType) {
        if (exceptionOutputType == null) {
            this.exceptionOutputType = ExceptionOutputType.DETAILED;
        } else {
            this.exceptionOutputType = exceptionOutputType;
        }
    }

    /**
     * Checks the exception output type and determines if detailed output should be written.
     *
     * @return {@code true} if detailed output should be written, otherwise {@code false}
     */
    protected boolean isDetailedExceptionOutputType() {
        final ExceptionOutputType exceptionOutputType = this.exceptionOutputType;
        return exceptionOutputType == ExceptionOutputType.DETAILED ||
                exceptionOutputType == ExceptionOutputType.DETAILED_AND_FORMATTED;
    }

    /**
     * Checks the exception output type and determines if formatted output should be written. The formatted output is
     * equivalent to {@link Throwable#printStackTrace()}.
     *
     * @return {@code true} if formatted exception output should be written, otherwise {@code false}
     */
    protected boolean isFormattedExceptionOutputType() {
        final ExceptionOutputType exceptionOutputType = this.exceptionOutputType;
        return exceptionOutputType == ExceptionOutputType.FORMATTED ||
                exceptionOutputType == ExceptionOutputType.DETAILED_AND_FORMATTED;
    }

    private void addException(final Generator generator, final Throwable throwable, final Map<Throwable, Integer> seen)
            throws Exception {
        if (throwable == null) {
            return;
        }
        if (seen.containsKey(throwable)) {
            generator.addAttribute(getKey(Key.EXCEPTION_REFERENCE_ID), seen.get(throwable));
            generator.startObject(getKey(Key.EXCEPTION_CIRCULAR_REFERENCE));
            generator.add(getKey(Key.EXCEPTION_MESSAGE), throwable.getMessage());
            generator.endObject(); // end circular reference
        } else {
            final int id = ++refId;
            seen.put(throwable, id);
            generator.addAttribute(getKey(Key.EXCEPTION_REFERENCE_ID), id);
            generator.add(getKey(Key.EXCEPTION_TYPE), throwable.getClass().getName());
            generator.add(getKey(Key.EXCEPTION_MESSAGE), throwable.getMessage());

            final StackTraceElement[] elements = throwable.getStackTrace();
            addStackTraceElements(generator, elements);

            // Render the suppressed messages
            final Throwable[] suppressed = throwable.getSuppressed();
            if (suppressed != null && suppressed.length > 0) {
                generator.startArray(getKey(Key.EXCEPTION_SUPPRESSED));
                for (Throwable s : suppressed) {
                    if (generator.wrapArrays()) {
                        generator.startObject(getKey(Key.EXCEPTION));
                    } else {
                        generator.startObject(null);
                    }
                    addException(generator, s, seen);
                    generator.endObject(); // end exception
                }
                generator.endArray();
            }

            // Render the cause
            final Throwable cause = throwable.getCause();
            if (cause != null) {
                generator.startObject(getKey(Key.EXCEPTION_CAUSED_BY));
                generator.startObject(getKey(Key.EXCEPTION));
                addException(generator, cause, seen);
                generator.endObject();
                generator.endObject(); // end exception
            }
        }
    }

    private void addStackTraceElements(final Generator generator, final StackTraceElement[] elements) throws Exception {
        generator.startArray(getKey(Key.EXCEPTION_FRAMES));
        for (StackTraceElement e : elements) {
            if (generator.wrapArrays()) {
                generator.startObject(getKey(Key.EXCEPTION_FRAME));
            } else {
                generator.startObject(null);
            }
            generator.add(getKey(Key.EXCEPTION_FRAME_CLASS), e.getClassName());
            generator.add(getKey(Key.EXCEPTION_FRAME_METHOD), e.getMethodName());
            final int line = e.getLineNumber();
            if (line >= 0) {
                generator.add(getKey(Key.EXCEPTION_FRAME_LINE), e.getLineNumber());
            }
            generator.endObject(); // end exception object
        }
        generator.endArray(); // end array
    }

    private static boolean isNotNullOrEmpty(final String value) {
        return value != null && !value.isEmpty();
    }

    private static boolean isNotNullOrEmpty(final Collection<?> value) {
        return value != null && !value.isEmpty();
    }

    /**
     * A generator used to create the structured output.
     */
    @SuppressWarnings("UnusedReturnValue")
    protected interface Generator {

        /**
         * Initial method invoked at the start of the generation.
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        default Generator begin() throws Exception {
            return this;
        }

        /**
         * Writes an integer value.
         *
         * @param key they key
         * @param value the value
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        default Generator add(final String key, final int value) throws Exception {
            add(key, Integer.toString(value));
            return this;
        }

        /**
         * Writes a long value.
         *
         * @param key they key
         * @param value the value
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        default Generator add(final String key, final long value) throws Exception {
            add(key, Long.toString(value));
            return this;
        }

        /**
         * Writes a map value
         *
         * @param key the key for the map
         * @param value the map
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        Generator add(String key, Map<String, ?> value) throws Exception;

        /**
         * Writes a string value.
         *
         * @param key the key for the value
         * @param value the string value
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        Generator add(String key, String value) throws Exception;

        /**
         * Writes string value containing json structure as {@link jakarta.json.JsonValue}
         *
         * @param key the key for the value
         * @param value the value containing json structure
         * @return the generator
         * @throws Exception if an error occurs while adding the data
         */
        Generator addJsonString(String key, final String value) throws Exception;

        /**
         * Adds the meta data to the structured format.
         * <p>
         * By default this processes the map and uses {@link #add(String, String)} to add entries.
         * </p>
         *
         * @param metaData the matp of the meta data, cannot be {@code null}
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        default Generator addMetaData(final Map<String, String> metaData) throws Exception {
            for (String key : metaData.keySet()) {
                add(key, metaData.get(key));
            }
            return this;
        }

        /**
         * Writes the start of an object.
         * <p>
         * If the {@link #wrapArrays()} returns {@code false} the key may be {@code null} and implementations should
         * handle this.
         * </p>
         *
         * @param key they key for the object, or {@code null} if this object was
         *        {@linkplain #startArray(String) started in an array} and the {@link #wrapArrays()} is
         *        {@code false}
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        Generator startObject(String key) throws Exception;

        /**
         * Writes an end to the object.
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        Generator endObject() throws Exception;

        /**
         * Writes the start of an array. This defaults to {@link #startObject(String)} for convenience of generators
         * that don't have a specific type for arrays.
         *
         * @param key they key for the array
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        default Generator startArray(String key) throws Exception {
            return startObject(key);
        }

        /**
         * Writes an end for an array. This defaults to {@link #endObject()} for convenience of generators that don't
         * have a specific type for arrays.
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        default Generator endArray() throws Exception {
            return endObject();
        }

        /**
         * Writes an attribute.
         * <p>
         * By default this uses the {@link #add(String, int)} method to add the attribute. If a formatter requires
         * special handling for attributes, for example an attribute on an XML element, this method can be overridden.
         * </p>
         *
         * @param name the name of the attribute
         * @param value the value of the attribute
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        default Generator addAttribute(final String name, final int value) throws Exception {
            return add(name, value);
        }

        /**
         * Writes an attribute.
         * <p>
         * By default this uses the {@link #add(String, String)} method to add the attribute. If a formatter requires
         * special handling for attributes, for example an attribute on an XML element, this method can be overridden.
         * </p>
         *
         * @param name the name of the attribute
         * @param value the value of the attribute
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data
         */
        default Generator addAttribute(final String name, final String value) throws Exception {
            return add(name, value);
        }

        /**
         * Writes any trailing data that's needed.
         *
         * @return the generator
         *
         * @throws Exception if an error occurs while adding the data during the build
         */
        Generator end() throws Exception;

        /**
         * Indicates whether or not elements in an array should be wrapped or not. The default is {@code false}.
         *
         * @return {@code true} if elements should be wrapped, otherwise {@code false}
         */
        default boolean wrapArrays() {
            return false;
        }
    }

}
