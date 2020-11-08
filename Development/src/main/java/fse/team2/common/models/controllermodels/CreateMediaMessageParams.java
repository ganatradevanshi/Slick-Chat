package fse.team2.common.models.controllermodels;

/**
 * Params class for MediaController. JSON will be passed in this form along with the request.
 */
public class CreateMediaMessageParams {

    private String fileName;
    private String fileContents;
    private String senderId;
    private String receiverId;

    public CreateMediaMessageParams() {
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContents() {
        return fileContents;
    }

    public void setFileContents(String fileContents) {
        this.fileContents = fileContents;
    }
}
