package org.tkit.quarkus.log.json.tagging;

import static org.tkit.quarkus.log.json.tagging.MessageTypeTag.CUSTOM_ATTRIBUTE_JSON_STRING_MESSAGE;
import static org.tkit.quarkus.log.json.tagging.MessageTypeTag.CUSTOM_ATTRIBUTE_STRING_MESSAGE;


/**
 * Log record message wrapper, which parses (if correctly structured)
 * 2 tags from very beginning of string message. First tag is interpreted as
 * type of message, second as name of attribute which is written as key to json
 * log output.
 * If message does not contain tags or tags are not correctly structured,
 * this is interpreted as Regular message type with name of attribute 'message'.
 * @author brano kalas
 */
public class TaggedMessage {
    private MessageTypeTag messageTypeTag;
    private String nameOfAttributeTag;
    private String message;
    private String messageAfterTagsRemoval;

    public TaggedMessage(String logRecordMessage) {
        this.message = logRecordMessage;
        boolean tagsFound = false;
        if (logRecordMessage != null) {
            this.messageTypeTag = MessageTypeTag.fromString(parseBeginningTagFromString(logRecordMessage));
            if (CUSTOM_ATTRIBUTE_JSON_STRING_MESSAGE == messageTypeTag ||
                    CUSTOM_ATTRIBUTE_STRING_MESSAGE == messageTypeTag) {
                this.nameOfAttributeTag = parseBeginningTagFromString(logRecordMessage.substring(messageTypeTag.getTagName().length() + 2));
                if (this.nameOfAttributeTag != null) {
                    tagsFound = true;
                    this.messageAfterTagsRemoval = logRecordMessage.substring(messageTypeTag.getTagName().length() + nameOfAttributeTag.length() + 4);
                }

            }
        }
        if (!tagsFound) {
            this.messageTypeTag = MessageTypeTag.REGULAR_MESSAGE;
            this.nameOfAttributeTag = this.messageTypeTag.getTagName();
            this.messageAfterTagsRemoval = this.message;
        }
    }

    public String parseBeginningTagFromString(String val) {
        String result = null;
        int startIndex = val.indexOf("<");
        if (startIndex == 0) {
            int endIndex = val.indexOf(">");
            if ((endIndex > startIndex + 1) && endIndex < 30) {
                result = val.substring(val.indexOf("<") + 1, val.indexOf(">"));
            }
        }
        return result;
    }

    public MessageTypeTag getMessageTypeTag() {
        return messageTypeTag;
    }

    public void setMessageTypeTag(MessageTypeTag messageTypeTag) {
        this.messageTypeTag = messageTypeTag;
    }

    public String getNameOfAttributeTag() {
        return nameOfAttributeTag;
    }

    public void setNameOfAttributeTag(String nameOfAttributeTag) {
        this.nameOfAttributeTag = nameOfAttributeTag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageAfterTagsRemoval() {
        return messageAfterTagsRemoval;
    }

    public void setMessageAfterTagsRemoval(String messageAfterTagsRemoval) {
        this.messageAfterTagsRemoval = messageAfterTagsRemoval;
    }
}
