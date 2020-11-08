package fse.team2.common.models.mongomodels;

import fse.team2.common.utils.Utils;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

/**
 * This model stores passwords for entities such as {@link UserModel} and Group.
 */
public class AuthenticationData {

    public static final String COLLECTION_NAME = "auth_data";
    public static final String ENTITY_ID = "entityId";

    private ObjectId id;
    @BsonProperty(ENTITY_ID)
    private ObjectId entityId;
    private String password;

    public AuthenticationData(ObjectId id, ObjectId entityId, String password) {
        setEntityId(entityId);
        setId(id);
        setPassword(password);
    }

    public AuthenticationData() {
        // This public constructor is needed by the mongoDB driver to map documents to user POJO.
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

    public ObjectId getEntityId() {
        return entityId;
    }

    public void setEntityId(ObjectId entityId) {
        if (Utils.checkForNull(entityId)) {
            throw new IllegalArgumentException("Entity id cannot be null");
        }
        this.entityId = entityId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (Utils.checkForNull(password)) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        this.password = password;
    }
}
