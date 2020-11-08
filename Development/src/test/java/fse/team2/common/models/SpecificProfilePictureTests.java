package fse.team2.common.models;

import fse.team2.common.models.mongomodels.SpecificProfilePicture;
import org.bson.types.ObjectId;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpecificProfilePictureTests {

    @Test
    public void simpleModelTest() {
        ObjectId id = new ObjectId();
        ObjectId userId = new ObjectId();
        ObjectId forId = new ObjectId();
        String url = "http:///.png";
        SpecificProfilePicture model = new SpecificProfilePicture(id, userId, forId, url);
        model.setForUser(forId);
        model.setId(id);
        model.setUrl(url);
        model.setUserId(userId);
        assertEquals(id, model.getId());
        assertEquals(forId, model.getForUser());
        assertEquals(url, model.getUrl());
        assertEquals(userId, model.getUserId());
    }
}
