package fse.team2.common.models;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import fse.team2.common.models.mongomodels.UserActivityData;
import fse.team2.common.models.mongomodels.UserModel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class UserActivityDataTests {
  private UserModel user;
  private UserActivityData userActivityData;

  @Before
  public void setUp() {
    user = UserModel.userBuilder().setId(new ObjectId())
        .setUsername("DemoUser")
        .setTracked(true)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void IdNotNullTest() {
    userActivityData = UserActivityData.userActivityBuilder().setId(null).build();
  }

  @Test
  public void NoEqualTest() {
    userActivityData = UserActivityData.userActivityBuilder().setId(new ObjectId()).build();
    UserActivityData userActivityData2 = UserActivityData.userActivityBuilder().setId(new ObjectId()).build();
    assertNotEquals(userActivityData2, userActivityData);
    assertFalse(userActivityData.equals(userActivityData2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void setUsernameNotNull(){
    userActivityData = UserActivityData.userActivityBuilder().setId(new ObjectId()).build();
    userActivityData.setUsername(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void loginTimeNotNull(){
    userActivityData = UserActivityData.userActivityBuilder().setId(new ObjectId())
        .setLoggedInTime(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void logoutTimeNotNull(){
    userActivityData = UserActivityData.userActivityBuilder().setId(new ObjectId())
        .setLogOutTime(null).build();
  }


}
