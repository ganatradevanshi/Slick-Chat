package fse.team2.common.models.controllermodels;

/**
 * This is the inner class which we would use to decode the payload from JSON body for the functions
 * which would involve interaction between two users such as deleteMessage.
 */
public class UserMessageParams {
    private String userId;
    private String messageId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
