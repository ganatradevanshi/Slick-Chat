package com.neu.prattle.servicetests;

import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import fse.team2.common.models.mongomodels.UserModel;
import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.UserService;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class UserServiceTests {
    DatabaseService<UserModel> service;

    @Before
    public void setUp() {
        service = UserService.getInstance();
    }

    @Test
    public void addUserAndDelete() {
        String username = "A";
        String name = "ABC";
        ObjectId id = new ObjectId();
        UserModel user = UserModel.userBuilder()
                .setId(id)
                .setUsername(username)
                .setName(name)
                .setDelete(false)
                .setFollowers(new ArrayList<>())
                .setFollowing(new ArrayList<>())
                .setGroups(new ArrayList<>())
                .setHidden(false)
                .setPreferences(new ArrayList<>())
                .setMessages(new ArrayList<>())
                .build();
        service.add(user);
        UserModel foundUser = service.findById(id);
        assertEquals(foundUser.getId(), user.getId());
        service.deleteById(user.getId());
        assertTrue(service.findById(id) == null);
    }

    @Test
    public void addDuplicateUser() {
        String username = "JohnCena";
        String name = "ABC";
        ObjectId id = new ObjectId();
        UserModel user = UserModel.userBuilder()
                .setId(id)
                .setUsername(username)
                .setName(name)
                .setDelete(false)
                .setFollowers(new ArrayList<>())
                .setFollowing(new ArrayList<>())
                .setGroups(new ArrayList<>())
                .setHidden(false)
                .setPreferences(new ArrayList<>())
                .setMessages(new ArrayList<>())
                .build();

        UserModel user2 = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername(username)
                .setName(name)
                .setDelete(false)
                .setFollowers(new ArrayList<>())
                .setFollowing(new ArrayList<>())
                .setGroups(new ArrayList<>())
                .setHidden(false)
                .setPreferences(new ArrayList<>())
                .setMessages(new ArrayList<>())
                .build();

        service.add(user);
        try {
            service.add(user2);
            fail("Should have thrown exception on adding a user with existing username");
        } catch (MongoWriteException e) {
            service.deleteById(user.getId());
            assertTrue(service.findById(id) == null);
        }
    }

    @Test
    public void findBy() {
        String username = "A";
        String name = "ABC";
        ObjectId id = new ObjectId();
        UserModel user = UserModel.userBuilder()
                .setId(id)
                .setUsername(username)
                .setName(name)
                .setDelete(false)
                .setFollowers(new ArrayList<>())
                .setFollowing(new ArrayList<>())
                .setGroups(new ArrayList<>())
                .setHidden(false)
                .setPreferences(new ArrayList<>())
                .setMessages(new ArrayList<>())
                .build();
        service.add(user);
        UserModel foundUser = service.findBy(Filters.eq(UserModel.USERNAME_FIELD, "A")).next();
        assertEquals(username, foundUser.getUsername());
        service.deleteById(user.getId());
    }

    @Test
    public void replaceOne() {
        String username = "A";
        String name = "ABC";
        String anotherName = "BBC";
        ObjectId id = new ObjectId();
        UserModel user = UserModel.userBuilder()
                .setId(id)
                .setUsername(username)
                .setName(name)
                .setDelete(false)
                .setFollowers(new ArrayList<>())
                .setFollowing(new ArrayList<>())
                .setGroups(new ArrayList<>())
                .setHidden(false)
                .setPreferences(new ArrayList<>())
                .setMessages(new ArrayList<>())
                .build();

        UserModel anotherUser = UserModel.userBuilder()
                .setId(id)
                .setUsername(username)
                .setName(anotherName)
                .setDelete(false)
                .setFollowers(new ArrayList<>())
                .setFollowing(new ArrayList<>())
                .setGroups(new ArrayList<>())
                .setHidden(false)
                .setPreferences(new ArrayList<>())
                .setMessages(new ArrayList<>())
                .build();

        service.add(user);
        service.replaceOne(Filters.eq("_id", id), anotherUser);
        UserModel foundUser = service.findById(id);
        assertEquals(username, foundUser.getUsername());
        assertEquals(anotherName, foundUser.getName());
        service.deleteById(user.getId());
    }
}
