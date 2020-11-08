package fse.team2.common.models.controllermodels;

/**
 * This is the inner class which we would use to decode the payload from JSON body for the functions
 * which would involve interaction between two users such as getMessages.
 */
public class SenderReceiverParams {
  private String senderId;
  private String receiverId;

  public SenderReceiverParams() {
    // This public constructor is needed by the mongoDB driver to map documents to user POJO.
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
}
