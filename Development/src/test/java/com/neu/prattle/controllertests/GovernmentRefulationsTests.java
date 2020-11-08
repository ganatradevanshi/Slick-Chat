package com.neu.prattle.controllertests;

import com.neu.prattle.controller.GovernmentRegulationController;
import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.UserService;
import com.neu.prattle.utils.JWTUtils;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import fse.team2.common.models.mongomodels.UserModel;
import fse.team2.common.models.mongomodels.enums.UserType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GovernmentRefulationsTests {
  private DatabaseService<UserModel> userService;
  private UserModel governmentUser;
  private UserModel normaluser;
  private GovernmentRegulationController govtController;
  private String token;

  private String getAuthTokenFromUserId(String userId) {
    return "Bearer " + JWTUtils.generateJWToken(userId);
  }

  @Before
  public void setUp() {
    userService = UserService.getInstance();
    governmentUser = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("Govt. User")
        .setUserType(UserType.GOVERNMENT)
        .build();
    normaluser = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("Normal User")
        .build();
    govtController = new GovernmentRegulationController();
  }

  @Test
  public void trackUserTest() {
    userService.add(governmentUser);
    userService.add(normaluser);

    // Unauthorized token
    token = getAuthTokenFromUserId(new ObjectId().toString());
    Response response = govtController.trackUser(normaluser.getId().toString(), token);
    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

    // not a government user
    token = getAuthTokenFromUserId(normaluser.getId().toString());
    response = govtController.trackUser(normaluser.getId().toString(), token);
    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

    // no user to track
    token = getAuthTokenFromUserId(governmentUser.getId().toString());
    response = govtController.trackUser(new ObjectId().toString(), token);
    assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

    token = getAuthTokenFromUserId(governmentUser.getId().toString());
    response = govtController.trackUser(normaluser.getId().toString(), token);
    assertEquals(Response.Status.OK, response.getStatusInfo());
    // get the refreshed user
    normaluser = userService.findById(normaluser.getId());
    assertTrue(normaluser.isTracked());

    // tracked user info

    // no such user
    response = govtController.getTrackedUserInfo(new ObjectId().toString());
    assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

    // the user who is tracked
    response = govtController.getTrackedUserInfo(normaluser.getId().toString());
    assertEquals(Response.Status.OK, response.getStatusInfo());

    // untrack the user

    // not a government user
    token = getAuthTokenFromUserId(normaluser.getId().toString());
    response = govtController.untrackUser(normaluser.getId().toString(), token);
    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

    token = getAuthTokenFromUserId(governmentUser.getId().toString());
    response = govtController.untrackUser(normaluser.getId().toString(), token);
    assertEquals(Response.Status.OK, response.getStatusInfo());
    // get the refreshed user
    normaluser = userService.findById(normaluser.getId());
    assertFalse(normaluser.isTracked());

    //cleaning up DB
    userService.deleteById(governmentUser.getId());
    userService.deleteById(normaluser.getId());
  }

  @Test
  public void getAllInformationTest() {
    userService.add(governmentUser);

    // not govt user
    token = getAuthTokenFromUserId(new ObjectId().toString());
    Response response = govtController.getAllInformation(token);
    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

    token = getAuthTokenFromUserId(governmentUser.getId().toString());
    response = govtController.getAllInformation(token);
    assertEquals(Response.Status.OK, response.getStatusInfo());

    //cleaning up DB
    userService.deleteById(governmentUser.getId());
  }

  @Test
  public void getMessagesTest() {
    userService.add(governmentUser);
    userService.add(normaluser);

    // not govt user
    token = getAuthTokenFromUserId(new ObjectId().toString());
    Response response = govtController.getMessageInfoForUser(normaluser.getId().toString(), token);
    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

    token = getAuthTokenFromUserId(governmentUser.getId().toString());
    response = govtController.getMessageInfoForUser(normaluser.getId().toString(), token);
    assertEquals(Response.Status.OK, response.getStatusInfo());

    //cleaning up DB
    userService.deleteById(governmentUser.getId());
    userService.deleteById(normaluser.getId());
  }
}
