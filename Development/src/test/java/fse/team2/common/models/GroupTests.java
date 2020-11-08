package fse.team2.common.models;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import fse.team2.common.models.mongomodels.Group;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class GroupTests {
  private Group group;
  private String name;

  @Before
  public void setUp() {
    name = "Avengers";
    group = Group.groupBuilder()
        .setId(new ObjectId())
        .setName(name)
        .build();
  }

  @Test
  public void testSimpleGroupCreation() {
    assertEquals(name, group.getName());
  }

  @Test
  public void sameGroup() {
    Group group2 = Group.groupBuilder()
        .setId(new ObjectId())
        .setName(name)
        .build();

    assertEquals(group.hashCode(), group2.hashCode());
  }

  @Test
  public void uniqueGroup() {
    Group group_dc = Group.groupBuilder()
        .setId(new ObjectId())
        .setName("Justice League")
        .build();

    assertNotEquals(group.hashCode(), group_dc.hashCode());
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullIdTest() {
    group = Group.groupBuilder().setId(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullNameTest() {
    group = Group.groupBuilder().setName(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullPreferencesTest() {
    group = Group.groupBuilder().setPreferences(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullModeratorTest() {
    group = Group.groupBuilder().setModerator(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullUsersTest() {
    group = Group.groupBuilder().setUsers(null).build();
  }

  @Test
  public void createGroupTest() {
    ObjectId id = new ObjectId();
    Group group = Group.groupBuilder()
        .setId(id)
        .setName(name)
        .setUsers(Collections.emptyList())
        .setModerator(Collections.emptyList())
        .setPreferences(Collections.emptyList())
        .setMessages(Collections.emptyList())
        .build();
    assertEquals(name, group.getName());
    assertEquals(id, group.getId());
    assertEquals(0, group.getMessages().size());
    assertEquals(0, group.getModerators().size());
    assertEquals(0, group.getPreferences().size());
    assertEquals(0, group.getUsers().size());
  }

  @Test
  public void notEqualTest() {
    assertFalse(group.equals("group"));
  }

}
