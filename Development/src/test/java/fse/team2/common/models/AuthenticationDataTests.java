package fse.team2.common.models;

import fse.team2.common.models.mongomodels.AuthenticationData;
import org.bson.types.ObjectId;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AuthenticationDataTests {

    @Test
    public void basicCreationTest() {
        ObjectId id = new ObjectId();
        ObjectId entityId = new ObjectId();
        String password = "123";
        AuthenticationData obj = new AuthenticationData(id, entityId, password);
        assertEquals(password, obj.getPassword());
        assertEquals(id, obj.getId());
        assertEquals(entityId, obj.getEntityId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCreationTest1() {
        ObjectId id = new ObjectId();
        ObjectId entityId = new ObjectId();
        String password = "123";
        AuthenticationData obj = new AuthenticationData(null, entityId, password);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCreationTest2() {
        ObjectId id = new ObjectId();
        ObjectId entityId = new ObjectId();
        String password = "123";
        AuthenticationData obj = new AuthenticationData(id, null, password);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCreationTest3() {
        ObjectId id = new ObjectId();
        ObjectId entityId = new ObjectId();
        String password = "123";
        AuthenticationData obj = new AuthenticationData(id, entityId, null);
    }
}
