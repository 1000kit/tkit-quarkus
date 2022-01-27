package org.tkit.quarkus.log.json.tagging;
/**
 * Supported message types used as mainTag attribute of {@link TaggedMessage}
 */


public enum MessageTypeTag {
    /**
     * Log message containing json structure serialized in log record as string which should be written under some custom key
     * to json log
     * example: {@code <jsonStringMessage><nameOfAttribute>{"exampleAttr":"exampleValue"}}
     */
    CUSTOM_ATTRIBUTE_JSON_STRING_MESSAGE("jsonStringMessage"),
    /**
     * Log message which should be stored as String value under some custom key in json log
     * example: {@code <stringMessage><nameOfAttribute>Regular string message}
     */
    CUSTOM_ATTRIBUTE_STRING_MESSAGE("stringMessage"),
    /**
     * Regular log message, will be written as string under key 'message'
     */
    REGULAR_MESSAGE("message");

    private String tagName;

    MessageTypeTag(String name) {
        this.tagName = name;
    }

    public static MessageTypeTag fromString(String val) {
        MessageTypeTag[] values = MessageTypeTag.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].tagName.equals(val)) {
                return values[i];
            }
        }
        return null;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
