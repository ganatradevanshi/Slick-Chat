package fse.team2.common.models.controllermodels;

public class UserMessagesInformation {
  private String username;
  private String sender;
  private String receiver;
  private String group;
  private String message;

  public UserMessagesInformation() {
    // This public constructor is needed by the mongoDB driver to map documents to user POJO.
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
