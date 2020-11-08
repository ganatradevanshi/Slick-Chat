package fse.team2.common.models.mongomodels;

import org.bson.types.ObjectId;

public class UserResponse {
    public static final String USER_ID_FIELD = "userId";
    public static final String RESPONSE_FIELD = "response";

    private ObjectId userId;
    private String response;

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
