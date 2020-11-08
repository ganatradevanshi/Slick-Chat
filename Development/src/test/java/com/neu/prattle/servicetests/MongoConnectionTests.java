package com.neu.prattle.servicetests;

import com.mongodb.client.MongoCollection;
import fse.team2.common.models.mongomodels.UserModel;
import com.neu.prattle.service.dbservice.MongoConnection;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class MongoConnectionTests {

    @Test
    public void testRetrieveCollection() {
        MongoCollection<UserModel> usersCollection = MongoConnection.getCollection(UserModel.COLLECTION_NAME, UserModel.class, UserModel.getValidationOptions());
        assertFalse(usersCollection.equals(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveCollectionInvalidCollectionName() {
        MongoCollection<UserModel> usersCollection = MongoConnection.getCollection(null, UserModel.class, UserModel.getValidationOptions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveCollectionInvalidClass() {
        MongoCollection<UserModel> usersCollection = MongoConnection.getCollection(UserModel.COLLECTION_NAME, null, UserModel.getValidationOptions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveCollectionNullValidationOptions() {
        MongoCollection<UserModel> usersCollection = MongoConnection.getCollection("sample_user_db_test", UserModel.class, null);
    }

    @Test
    public void testRetrieveCollection2() {
        MongoCollection<UserModel> usersCollection = MongoConnection.getCollection(UserModel.COLLECTION_NAME, UserModel.class);
        assertFalse(usersCollection.equals(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveCollectionInvalidCollectionName2() {
        MongoCollection<UserModel> usersCollection = MongoConnection.getCollection(null, UserModel.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveCollectionInvalidClass2() {
        MongoCollection<UserModel> usersCollection = MongoConnection.getCollection(UserModel.COLLECTION_NAME, null);
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Constructor constructor = MongoConnection.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
