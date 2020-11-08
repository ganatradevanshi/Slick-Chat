package fse.team2.common.models.mongomodels;

import org.bson.types.ObjectId;

/**
 * This class represents a model to store specific profile pictures
 * of a user for some other user.
 */
public class SpecificProfilePicture {

    public static final String COLLECTION_NAME = "specific_profile_pictures";
    public static final String ID_FIELD = "_id";
    public static final String USER_ID_FIELD = "userId";
    public static final String FOR_USER_FIELD = "forUser";

    ObjectId id;
    ObjectId userId;
    ObjectId forUser;
    String url;

    public SpecificProfilePicture() {

    }

    public SpecificProfilePicture(ObjectId id, ObjectId userId, ObjectId forUser, String url) {
        this.id = id;
        this.forUser = forUser;
        this.userId = userId;
        this.url = url;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public ObjectId getForUser() {
        return forUser;
    }

    public void setForUser(ObjectId forUser) {
        this.forUser = forUser;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
