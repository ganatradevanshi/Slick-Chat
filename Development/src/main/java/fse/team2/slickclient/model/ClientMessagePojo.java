package fse.team2.slickclient.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.models.mongomodels.enums.MessageType;

public class ClientMessagePojo {

  private String id;
  private String senderId;
  private String receiverId;

  private Date timestamp;
  private MessageType messageType;
  private boolean deleted;
  private boolean hidden;
  private boolean forwarded;
  private Date expiryDate;
  private List<String> tags;
  private EncrpytionLevel encryptionLevel;
  private String content;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  public String getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(String receiverId) {
    this.receiverId = receiverId;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public MessageType getMessageType() {
    return messageType;
  }

  public void setMessageType(MessageType messageType) {
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
    this.expiryDate = expiryDate;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public EncrpytionLevel getEncryptionLevel() {
    return encryptionLevel;
  }

  public void setEncryptionLevel(EncrpytionLevel encryptionLevel) {
    this.encryptionLevel = encryptionLevel;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "ClientMessagePojo{" +
            "id='" + id + '\'' +
            ", senderId='" + senderId + '\'' +
            ", receiverId='" + receiverId + '\'' +
            ", timestamp=" + timestamp +
            ", messageType=" + messageType +
            ", tags=" + tags +
            ", content='" + content + '\'' +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientMessagePojo that = (ClientMessagePojo) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
