package com.neu.prattle.servicetests;

import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import fse.team2.common.models.mongomodels.AuthenticationData;
import com.neu.prattle.service.dbservice.AuthenticationDataService;
import com.neu.prattle.service.dbservice.DatabaseService;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class AuthenticationDataServiceTests {

    DatabaseService<AuthenticationData> service;

    @Before
    public void setUp() {
        service = AuthenticationDataService.getInstance();
    }

    @Test
    public void addDataAndDelete() {
        ObjectId id = new ObjectId();
        ObjectId entityId = new ObjectId();
        String password = "123";
        AuthenticationData obj = new AuthenticationData(id, entityId, password);
        service.add(obj);

        AuthenticationData foundData = service.findById(id);
        assertEquals(password, foundData.getPassword());
        assertEquals(id, foundData.getId());
        assertEquals(entityId, foundData.getEntityId());
        service.deleteById(id);
    }

    @Test
    public void findBy() {
        ObjectId id = new ObjectId();
        ObjectId entityId = new ObjectId();
        String password = "123";
        AuthenticationData obj = new AuthenticationData(id, entityId, password);
        service.add(obj);

        AuthenticationData foundData = service.findBy(Filters.eq(AuthenticationData.ENTITY_ID, entityId)).next();
        assertEquals(password, foundData.getPassword());
        assertEquals(id, foundData.getId());
        assertEquals(entityId, foundData.getEntityId());
        service.deleteById(id);
    }

    @Test
    public void replaceOne() {
        ObjectId id = new ObjectId();
        ObjectId entityId = new ObjectId();
        String password = "123";
        String changedPassword = "abc";
        AuthenticationData obj = new AuthenticationData(id, entityId, password);
        AuthenticationData newObj = new AuthenticationData(id, entityId, changedPassword);
        service.add(obj);
        service.replaceOne(Filters.eq("_id", AuthenticationData.ENTITY_ID), newObj);

        AuthenticationData foundData = service.findById(id);
        assertEquals(password, foundData.getPassword());
        assertEquals(id, foundData.getId());
        assertEquals(entityId, foundData.getEntityId());
        service.deleteById(id);
    }

    @Test
    public void addDuplicateEntry() {
        ObjectId id = new ObjectId();
        ObjectId entityId = new ObjectId();
        String password = "123";
        AuthenticationData obj = new AuthenticationData(id, entityId, password);
        AuthenticationData obj2 = new AuthenticationData(new ObjectId(), entityId, password);

        service.add(obj);
        try {
            service.add(obj2);
            fail("Should have thrown exception on adding a user with existing username");
        } catch (MongoWriteException e) {
            service.deleteById(obj.getId());
            assertTrue(service.findById(id) == null);
        }
    }

}
