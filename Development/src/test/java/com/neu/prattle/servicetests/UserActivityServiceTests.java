package com.neu.prattle.servicetests;

import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.UserActivityService;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import fse.team2.common.models.mongomodels.UserActivityData;
import fse.team2.common.models.mongomodels.UserModel;

import static org.junit.Assert.assertEquals;

public class UserActivityServiceTests {
  private DatabaseService<UserActivityData> userActivityService;
  private UserModel user;
  private UserActivityData userActivityData;
  private DateTimeFormatter dtf;
  private LocalDateTime loginTime;
  private LocalDateTime logoutTime;


  @Before
  public void setUp() {
    dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    loginTime = LocalDateTime.now();
    logoutTime = loginTime.plusHours(1);
    userActivityService = UserActivityService.getInstance();
    user = UserModel.userBuilder().setId(new ObjectId())
        .setUsername("DemoUser")
        .setTracked(true)
        .build();
    userActivityData = UserActivityData.userActivityBuilder()
        .setId(new ObjectId())
        .setUser(user)
        .setLoggedInTime(dtf.format(loginTime))
        .setLogOutTime(dtf.format(logoutTime))
        .build();
  }

  @Test
  public void basicAddTest() {
    userActivityService.add(userActivityData);
    UserActivityData uad = userActivityService.findById(userActivityData.getId());
    assertEquals(uad, userActivityData);

    //cleaning up the DB
    userActivityService.deleteById(userActivityData.getId());
  }
}
