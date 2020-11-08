package com.neu.prattle.servicetests;

import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.GroupService;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import fse.team2.common.models.mongomodels.Group;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class GroupServiceTests {
  private DatabaseService<Group> service;
  private String name;

  @Before
  public void setUp() {
    name = "Avengers";
    service = GroupService.getInstance();
  }

  @Test
  public void testCreateAndDeleteGroup() {
    ObjectId id = new ObjectId();
    Group group = Group.groupBuilder()
        .setId(id)
        .setName(name)
        .setUsers(Collections.emptyList())
        .setModerator(Collections.emptyList())
        .setPreferences(Collections.emptyList())
        .setMessages(Collections.emptyList())
        .build();
    service.add(group);
    Group foundGroup = service.findById(id);
    assertEquals(group.getId(), foundGroup.getId());
    service.deleteById(id);
    assertNull(service.findById(id));
  }

  @Test
  public void testDuplicateGroup() {
    ObjectId id = new ObjectId();
    Group group = Group.groupBuilder()
        .setId(id)
        .setName(name)
        .setUsers(Collections.emptyList())
        .setModerator(Collections.emptyList())
        .setPreferences(Collections.emptyList())
        .setMessages(Collections.emptyList())
        .build();
    Group group2 = Group.groupBuilder()
        .setId(new ObjectId())
        .setName(name)
        .setUsers(Collections.emptyList())
        .setModerator(Collections.emptyList())
        .setPreferences(Collections.emptyList())
        .setMessages(Collections.emptyList())
        .build();

    service.add(group);
    try {
      service.add(group2);
      fail("Should have thrown exception on creating a group with same name as existing group");
    } catch (MongoWriteException e) {
      service.deleteById(group.getId());
      assertNull(service.findById(id));
    }
  }

  @Test
  public void findBy() {
    ObjectId id = new ObjectId();
    Group group = Group.groupBuilder()
        .setId(id)
        .setName(name)
        .setUsers(Collections.emptyList())
        .setModerator(Collections.emptyList())
        .setPreferences(Collections.emptyList())
        .setMessages(Collections.emptyList())
        .build();
    service.add(group);
    Group foundGroup = service.findBy(Filters.eq(Group.GROUPNAME_FIELD, name)).next();
    assertEquals(name, foundGroup.getName());
    service.deleteById(group.getId());
  }

  @Test
  public void replaceOne() {
    String nameOfGroup2 = "Justice League";
    ObjectId id = new ObjectId();
    Group marvel = Group.groupBuilder()
        .setId(id)
        .setName(name)
        .setUsers(Collections.emptyList())
        .setModerator(Collections.emptyList())
        .setPreferences(Collections.emptyList())
        .setMessages(Collections.emptyList())
        .build();

    Group dc = Group.groupBuilder()
        .setId(id)
        .setName(nameOfGroup2)
        .setUsers(Collections.emptyList())
        .setModerator(Collections.emptyList())
        .setPreferences(Collections.emptyList())
        .setMessages(Collections.emptyList())
        .build();

    // adding just marvel in DB
    service.add(marvel);
    //replacing marvel with dc in the DB
    service.replaceOne(Filters.eq("_id", id), dc);
    Group foundGroup = service.findById(id);
    assertEquals(nameOfGroup2, foundGroup.getName());
    service.deleteById(dc.getId());
  }


}
