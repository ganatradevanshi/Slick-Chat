package fse.team2.common.models.controllermodels;

import java.util.List;

public class CreatePollMessageParams {

    private String pollQuestion;
    private List<String> options;
    private String senderId;
    private String receiverId;

    public CreatePollMessageParams() {
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

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getPollQuestion() {
        return pollQuestion;
    }

    public void setPollQuestion(String pollQuestion) {
        this.pollQuestion = pollQuestion;
    }

}
