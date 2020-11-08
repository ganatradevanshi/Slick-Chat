package fse.team2.slickclient.model;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import fse.team2.common.models.mongomodels.preferences.Preference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientGroupPojoTests {
  private ClientGroupPojo clientGroupPojo;

  @Before
  public void setUp() {
    clientGroupPojo = new ClientGroupPojo();
  }

  @Test
  public void testId() {
    String id = new ObjectId().toString();
    this.clientGroupPojo.setId(id);
    assertEquals(id, this.clientGroupPojo.getId());
  }

  @Test
  public void testName() {
    String name = "DemoGroup";
    this.clientGroupPojo.setName(name);
    assertEquals(name, this.clientGroupPojo.getName());
  }

  @Test
  public void testPreferences() {
    List<Preference> preferences = new ArrayList<>();
    this.clientGroupPojo.setPreferences(preferences);
    assertEquals(preferences, this.clientGroupPojo.getPreferences());
  }

  @Test
  public void testModerators() {
    List<String> moderators = new ArrayList<>();
    this.clientGroupPojo.setModerators(moderators);
    assertEquals(moderators, this.clientGroupPojo.getModerators());
  }

  @Test
  public void testMessages() {
    List<String> messages = new ArrayList<>();
    this.clientGroupPojo.setMessages(messages);
    assertEquals(messages, this.clientGroupPojo.getMessages());
  }

  @Test
  public void testUsers() {
    List<String> users = new ArrayList<>();
    this.clientGroupPojo.setUsers(users);
    assertEquals(users, this.clientGroupPojo.getUsers());
  }


  @Test
  public void testToString() {
    this.clientGroupPojo.setId("123");
    this.clientGroupPojo.setName("John");
    assertEquals("ClientGroupPojo{id='123', name='John'}", this.clientGroupPojo.toString());
  }

  @Test
  public void testEquals() {
    this.clientGroupPojo.setId("123");
    this.clientGroupPojo.setName("John");

    ClientGroupPojo client2 = new ClientGroupPojo();
    client2.setId("123");
    client2.setName("John");

    assertTrue(this.clientGroupPojo.equals(client2));
  }
}
