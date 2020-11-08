package fse.team2.common.models.mongomodels;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.List;

public class Poll {
    public static final String COLLECTION_NAME = "poll";

    public static final String MESSAGE_ID_FIELD = "_id";
    public static final String OPTIONS_FIELD = "options";
    public static final String RESPONSES_FIELD = "responses";

    @BsonId
    private ObjectId messageId;
    private List<String> options;
    private List<UserResponse> responses;

    public ObjectId getMessageId() {
        return messageId;
    }

    public void setMessageId(ObjectId messageId) {
        this.messageId = messageId;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<UserResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<UserResponse> responses) {
        this.responses = responses;
    }
}
