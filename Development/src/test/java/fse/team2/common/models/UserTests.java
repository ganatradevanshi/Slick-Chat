package fse.team2.common.models;

import fse.team2.common.models.mongomodels.UserModel;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class UserTests {
    private UserModel user;
    private String username;

    @Before
    public void setUp() {
        username = "ABC";
        user = UserModel.userBuilder().setUsername(username).build();
    }

    @Test
    public void basicUserTest() {
        assertEquals(username, user.getUsername());
    }

    @Test
    public void sameUserTest1() {
        UserModel user2 = UserModel.userBuilder().setUsername(username).build();
        assertEquals(user.hashCode(), user2.hashCode());
    }

    @Test
    public void sameUserTest2() {
        UserModel user2 = UserModel.userBuilder().setUsername(username).build();
        assertEquals(user, user2);
    }

    @Test
    public void sameUserTestFail1() {
        UserModel user2 = UserModel.userBuilder().setUsername("XYZ").build();
        assertNotEquals(user, user2);
    }

    @Test
    public void sameUserTestFail2() {
        assertNotEquals("ABC", user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCheck() {
        username = null;
        user = UserModel.userBuilder().setUsername(null).build();
    }

    @Test
    public void createUserObjectTest() {
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
        assertEquals(username, user.getUsername());
        assertEquals(name, user.getName());
        assertEquals(0, user.getFollowers().size());
        assertEquals(0, user.getFollowing().size());
        assertEquals(0, user.getGroups().size());
        assertEquals(0, user.getPreferences().size());
        assertEquals(id, user.getId());
        assertFalse(user.isDeleted());
        assertFalse(user.isHidden());
    }

    @Test(expected = IllegalArgumentException.class)
    public void userObjectNull1() {
        UserModel user = UserModel.userBuilder()
                .setId(null).build();
    }


    @Test(expected = IllegalArgumentException.class)
    public void userObjectNull2() {
        UserModel user = UserModel.userBuilder()
                .setPreferences(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void userObjectNull3() {
        UserModel user = UserModel.userBuilder()
                .setGroups(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void userObjectNull4() {
        UserModel user = UserModel.userBuilder()
                .setFollowing(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void userObjectNull5() {
        UserModel user = UserModel.userBuilder()
                .setFollowers(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void userObjectNull6() {
        UserModel user = UserModel.userBuilder()
                .setName(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void userObjectNull7() {
        UserModel user = UserModel.userBuilder()
                .setUsername(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void userObjectNull8() {
        UserModel user = UserModel.userBuilder()
                .setMessages(null).build();
    }

    @Test
    public void notEqualsTest() {
      UserModel user = UserModel.userBuilder()
              .setUsername("Hi").build();
      assertFalse(user.equals("s"));
    }

  @Test
  public void equalsTest() {
    UserModel user1 = UserModel.userBuilder()
            .setUsername("Hi").build();
    UserModel user2 = UserModel.userBuilder()
            .setUsername("Hi").build();
    assertTrue(user1.equals(user2));
  }
}
