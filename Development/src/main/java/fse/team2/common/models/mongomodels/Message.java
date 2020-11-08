package fse.team2.common.models.mongomodels;

import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.models.mongomodels.enums.MessageType;
import fse.team2.common.utils.Utils;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * This class represents a Message model.
 */
public class Message {

    public static final String COLLECTION_NAME = "messages";

    public static final String ID_FIELD = "_id";
    public static final String SENDER_ID_FIELD = "senderId";
    public static final String RECEIVER_ID_FIELD = "receiverId";
    public static final String IS_GROUP_MESSAGE_FIELD = "groupMessage";
    public static final String GROUP_ID_FIELD = "groupId";
    public static final String DELETED_FIELD = "deleted";

    private ObjectId id;
    private ObjectId senderId;
    // If the message is a group message, receiver id indicates the id of the receiving group.
    private ObjectId receiverId;
    private Date timestamp;
    private MessageType messageType;
    private boolean deleted;
    private boolean hidden;
    private boolean forwarded;
    private Date expiryDate;
    private List<String> tags;
    private EncrpytionLevel encryptionLevel;
    private String content;
    private List<UserModel> userData;
    private String senderName;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public ObjectId getThreadHead() {
        return threadHead;
    }

    public void setThreadHead(ObjectId threadHead) {
        this.threadHead = threadHead;
    }

    public boolean isThread() {
        return id != threadHead;
    }

    private ObjectId threadHead;


    // Is the message a group message?
    private boolean groupMessage;

    public boolean isGroupMessage() {
        return groupMessage;
    }

    public void setGroupMessage(boolean groupMessage) {
        this.groupMessage = groupMessage;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        if (Utils.checkForNull(id)) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        if (Utils.checkForNull(timestamp)) {
            this.timestamp = new Date();
            return;
        }
        this.timestamp = timestamp;
    }

    public ObjectId getSenderId() {
        return senderId;
    }

    public void setSenderId(ObjectId senderId) {
        if (Utils.checkForNull(senderId)) {
            throw new IllegalArgumentException("senderId cannot be null");
        }
        this.senderId = senderId;
    }

    public ObjectId getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(ObjectId receiverId) {
        if (Utils.checkForNull(receiverId)) {
            throw new IllegalArgumentException("receiver Id cannot be null");
        }
        this.receiverId = receiverId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        if (Utils.checkForNull(messageType)) {
            throw new IllegalArgumentException("MessageType cannot be null");
        }
        this.messageType = messageType;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isForwarded() {
        return forwarded;
    }

    public void setForwarded(boolean forwarded) {
        this.forwarded = forwarded;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        if (Utils.checkForNull(expiryDate)) {
            this.expiryDate = new Date();
            return;
        }
        this.expiryDate = expiryDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        if (Utils.checkForNull(tags)) {
            this.tags = new ArrayList<>();
            return;
        }
        this.tags = tags;
    }

    public EncrpytionLevel getEncryptionLevel() {
        return encryptionLevel;
    }

    public void setEncryptionLevel(EncrpytionLevel encryptionLevel) {
        if (Utils.checkForNull(encryptionLevel)) {
            throw new IllegalArgumentException("encryption level cannot be null");
        }
        this.encryptionLevel = encryptionLevel;
    }

    public Message() {
        // This public constructor is needed by the mongoDB driver to map documents to message POJO.
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (Utils.checkForNull(content)) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        this.content = content;
    }

    public List<UserModel> getUserData() {
        return userData;
    }

    public void setUserData(List<UserModel> userData) {
        this.userData = userData;
    }

    public static MessageBuilder messageBuilder() {
        return new MessageBuilder();
    }

    /***
     * A Builder helper class to create instances of {@link Message}
     */
    public static class MessageBuilder {
        /***
         * Invoking the build method will return this message object.
         */
        Message message;

        public MessageBuilder() {
            message = new Message();
        }

        public MessageBuilder setMessageContent(String content) {
            message.setContent(content);
            return this;
        }

        public MessageBuilder setId(ObjectId id) {
            message.setId(id);
            return this;
        }

        public MessageBuilder setDeleted(boolean deleted) {
            message.setDeleted(deleted);
            return this;
        }

        public MessageBuilder setHidden(boolean hidden) {
            message.setHidden(hidden);
            return this;
        }

        public MessageBuilder setForwarded(boolean forwarded) {
            message.setForwarded(forwarded);
            return this;
        }

        public MessageBuilder setEncryptionLevel(EncrpytionLevel encryptionLevel) {
            message.setEncryptionLevel(encryptionLevel);
            return this;
        }

        public MessageBuilder setMessageType(MessageType messageType) {
            message.setMessageType(messageType);
            return this;
        }

        public MessageBuilder setExpiryDate(Date expiry) {
            message.setExpiryDate(expiry);
            return this;
        }

        public MessageBuilder setReceiverId(ObjectId id) {
            message.setReceiverId(id);
            return this;
        }

        public MessageBuilder setSenderId(ObjectId id) {
            message.setSenderId(id);
            return this;
        }

        public MessageBuilder setTimestamp(Date timestamp) {
            message.setTimestamp(timestamp);
            return this;
        }

        public MessageBuilder setTags(List<String> tags) {
            message.setTags(tags);
            return this;
        }

        public MessageBuilder setGroupMessage(boolean groupMessage) {
            message.setGroupMessage(groupMessage);
            return this;
        }

        public MessageBuilder setThreadHead(ObjectId objectId) {
            message.setThreadHead(objectId);
            return this;
        }

        public Message build() {
            // Set thread head to id of the message itself if it is null.
            if (message.getThreadHead() == null) {
                message.setThreadHead(message.getId());
            }
            return message;
        }
    }
}
