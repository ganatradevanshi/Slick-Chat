package com.neu.prattle.controllertests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import com.mongodb.client.model.Filters;
import com.neu.prattle.controller.UserController;
import com.neu.prattle.service.dbservice.AuthenticationDataService;
import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.MessageService;
import com.neu.prattle.service.dbservice.UserService;
import com.neu.prattle.utils.BCryptUtils;
import com.neu.prattle.utils.JWTUtils;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import fse.team2.common.models.controllermodels.ProfilePictureParams;
import fse.team2.common.models.controllermodels.UserCredentialsParams;
import fse.team2.common.models.mongomodels.AuthenticationData;
import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.UserModel;
import fse.team2.common.models.mongomodels.preferences.DoNotDisturb;
import fse.team2.common.models.mongomodels.preferences.Preference;
import fse.team2.common.utils.adapters.ObjectIdTypeAdapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class UserControllerTests {
  private UserController controller;
  private DatabaseService<UserModel> userService;
  private DatabaseService<AuthenticationData> authenticationDataService;
  private DatabaseService<Message> messageService;
  private String username;
  private String password;
  private String garbageToken;

  private String getRandomString() {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; //  letter 'z'
    int targetStringLength = 10;
    Random random = new Random();

    return random.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  @Before
  public void setUp() {
    controller = new UserController();
    userService = UserService.getInstance();
    authenticationDataService = AuthenticationDataService.getInstance();
    messageService = MessageService.getInstance();
    garbageToken = "Bearer garbageToken";
    username = "UserName" + getRandomString();
    password = "PassWord" + getRandomString();

  }

  private String getAuthTokenFromUserId(String userId) {
    return "Bearer " + JWTUtils.generateJWToken(userId);
  }

  @Test
  public void testFindUser() {
    UserModel user = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername(username)
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .build();
    userService.add(user);

    UserCredentialsParams credentials = new UserCredentialsParams();
    credentials.setUsername(username);
    Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
    String json = gson.toJson(user);

    Response response = controller.searchUser(credentials.getUsername());
    userService.deleteById(user.getId());
    String foundUserJson = response.getEntity().toString();

    JsonArray jsonArray = new JsonParser().parse(foundUserJson).getAsJsonArray();
    assertEquals(json, jsonArray.get(0).toString());
    assertEquals(Response.Status.OK, response.getStatusInfo());
  }

  @Test
  public void searchUserTestWhenNoUserInDb() {
    UserCredentialsParams credentials = new UserCredentialsParams();
    credentials.setUsername("stark");
    credentials.setName("Tony Stark");
    Response response = controller.searchUser(credentials.getUsername());
    assertEquals(Response.Status.NO_CONTENT, response.getStatusInfo());
  }

  @Test
  public void userAlreadyPresent() {
    String username = "userAlreadyPresent";
    UserCredentialsParams credentials = new UserCredentialsParams();
    credentials.setUsername(username);
    credentials.setPassword(password);
    Response response = controller.signup(credentials);
    assertEquals(Response.Status.OK, response.getStatusInfo());
    response = controller.signup(credentials);
    assertEquals(Response.Status.CONFLICT, response.getStatusInfo());
    UserModel foundUser = userService.findBy(Filters.eq(UserModel.USERNAME_FIELD, username)).next();
    response = controller.deleteUserAccount(foundUser.getId().toString());
    assertEquals(Response.Status.OK, response.getStatusInfo());

  }

  @Test
  public void signUpAndThenDeleteTest() {
    UserCredentialsParams credentials = new UserCredentialsParams();
    credentials.setUsername(username);
    credentials.setPassword(password);
    Response response = controller.signup(credentials);
    assertEquals(Response.Status.OK, response.getStatusInfo());
    UserModel foundUser = userService.findBy(Filters.eq(UserModel.USERNAME_FIELD, username)).next();
    AuthenticationData foundAuthenticationData = authenticationDataService.findBy(Filters.eq(AuthenticationData.ENTITY_ID, foundUser.getId())).next();
    assertEquals(username, foundUser.getUsername());
    assertTrue(BCryptUtils.verifyHash(password, foundAuthenticationData.getPassword()));
    response = controller.deleteUserAccount(foundUser.getId().toString());
    assertEquals(Response.Status.OK, response.getStatusInfo());
  }

  @Test
  public void signUpOAuthUserAndLogin() {
    UserModel user = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername(username)
        .setIsOAuthUser(true)
        .build();
    userService.add(user);
    UserCredentialsParams credentials = new UserCredentialsParams();
    credentials.setUsername(username);
    credentials.setPassword(password);

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    Response response = controller.login(credentials, mockRequest);

    assertEquals(Response.Status.OK, response.getStatusInfo());
    //cleaning uo the DB
    userService.deleteById(user.getId());
  }

  @Test
  public void loginTest() {
    UserCredentialsParams credentials = new UserCredentialsParams();
    credentials.setUsername(username);
    credentials.setPassword(password);
    credentials.setName("User");
    Response response = controller.signup(credentials);
    assertEquals(Response.Status.OK, response.getStatusInfo());

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    response = controller.login(credentials, mockRequest);

    assertEquals(Response.Status.OK, response.getStatusInfo());
    UserModel foundUser = userService.findBy(Filters.eq(UserModel.USERNAME_FIELD, username)).next();
    response = controller.deleteUserAccount(foundUser.getId().toString());
    assertEquals(Response.Status.OK, response.getStatusInfo());
  }

  @Test
  public void failedLogin() {
    UserCredentialsParams credentials = new UserCredentialsParams();
    credentials.setUsername(username);
    credentials.setPassword(password);

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    Response response = controller.login(credentials, mockRequest);

    assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
    response = controller.deleteUserAccount(new ObjectId().toString());
    assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
  }

  @Test
  public void incorrectPasswordTest() {
    UserCredentialsParams credentials = new UserCredentialsParams();
    credentials.setUsername(username);
    credentials.setPassword(password);
    Response response = controller.signup(credentials);
    assertEquals(Response.Status.OK, response.getStatusInfo());
    credentials.setPassword("PASSWORD");

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    response = controller.login(credentials, mockRequest);

    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
    UserModel foundUser = userService.findBy(Filters.eq(UserModel.USERNAME_FIELD, username)).next();
    response = controller.deleteUserAccount(foundUser.getId().toString());
    assertEquals(Response.Status.OK, response.getStatusInfo());
  }

  @Test
  public void testFindUserById() {
    String username = "testFindUserById";
    UserModel user = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername(username)
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .build();
    userService.add(user);
    Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
    String json = gson.toJson(user);

    String Id = user.getId().toString();
    Response response = controller.findUserById(getAuthTokenFromUserId(Id.toString()), Id);
    String foundUser = response.getEntity().toString();
    assertEquals(json, foundUser);

    userService.deleteById(user.getId());
  }

  @Test
  public void testFindUserGivesNotFound() {
    ObjectId id = new ObjectId();
    Response response = controller.findUserById(getAuthTokenFromUserId(id.toString()), id.toString());
    assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
  }

  @Test
  public void followUnfollowTest() {
    UserModel user1 = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("tStark")
        .setName("Tony Stark")
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .build();
    UserModel user2 = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("sRogers")
        .setName("Steve Rogers")
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .build();

    UserModel user3 = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("tOdinson")
        .setName("Thor Odinson")
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .build();

    userService.add(user1);
    userService.add(user2);
    userService.add(user3);

    String tokenForUser1 = getAuthTokenFromUserId(user1.getId().toString());
    String tokenForUser3 = getAuthTokenFromUserId(user3.getId().toString());

    // user1 and user 3 follows user2
    Response response = controller.follow(user2.getId().toString(), garbageToken);
    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

    controller.follow(user2.getId().toString(), tokenForUser1);
    controller.follow(user2.getId().toString(), tokenForUser3);

    // fetch the latest user objects from DB
    user1 = userService.findById(user1.getId());
    user2 = userService.findById(user2.getId());
    user3 = userService.findById(user3.getId());


    List<ObjectId> followersOfUser2 = new ArrayList<>();
    followersOfUser2.add(user1.getId());
    followersOfUser2.add(user3.getId());

    List<ObjectId> listOfFollowers = user2.getFollowers();

    for (int i = 0; i < listOfFollowers.size(); i++) {
      assertEquals(listOfFollowers.get(i), followersOfUser2.get(i));
    }

    // user1 and user 3 unfollows user2
    response = controller.unfollow(user2.getId().toString(), garbageToken);
    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
    controller.unfollow(user2.getId().toString(), tokenForUser1);
    controller.unfollow(user2.getId().toString(), tokenForUser3);

    // fetch the latest user objects from DB
    user2 = userService.findById(user2.getId());

    listOfFollowers = user2.getFollowers();
    assertEquals(Collections.emptyList(), listOfFollowers);

    userService.deleteById(user1.getId());
    userService.deleteById(user2.getId());
    userService.deleteById(user3.getId());
  }

  @Test
  public void invalidUsersFollowUnfollow() {
    ObjectId user1_id = new ObjectId();
    ObjectId user2_id = new ObjectId();
    Response response = controller.follow(user2_id.toString(), getAuthTokenFromUserId(user1_id.toString()));
    assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
    response = controller.unfollow(user2_id.toString(), getAuthTokenFromUserId(user1_id.toString()));
    assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
  }

  @Test
  public void testUserUpdate() {
    UserModel user = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("temp_username")
        .build();

    userService.add(user);

    UserModel updatedUser = UserModel.userBuilder()
        .setId(user.getId())
        .setUsername(username)
        .build();

    Response response = controller.updateUser(updatedUser);
    assertEquals(Response.Status.OK, response.getStatusInfo());

    UserModel foundUser = userService.findById(user.getId());
    assertEquals(updatedUser.getUsername(), foundUser.getUsername());

    userService.deleteById(updatedUser.getId());
  }

  @Test
  public void testFailedUserUpdate() {
    UserModel user = UserModel.userBuilder()
        .setUsername(username)
        .setId(new ObjectId())
        .build();

    Response response = controller.updateUser(user);
    assertEquals(Response.Status.NOT_MODIFIED, response.getStatusInfo());
  }

  @Test
  public void testGetRecentlyInteractedUsers() {
    UserModel user1 = UserModel.userBuilder().setId(new ObjectId()).setUsername("User1").build();
    UserModel user2 = UserModel.userBuilder().setId(new ObjectId()).setUsername("User2").build();
    UserModel user3 = UserModel.userBuilder().setId(new ObjectId()).setUsername("User3").build();


    Message message1_2 = Message.messageBuilder().setId(new ObjectId())
        .setSenderId(user1.getId())
        .setReceiverId(user2.getId())
        .build();
    Message message3_1 = Message.messageBuilder().setId(new ObjectId())
        .setSenderId(user3.getId())
        .setReceiverId(user1.getId())
        .build();
    Message message3_2 = Message.messageBuilder().setId(new ObjectId())
        .setSenderId(user3.getId())
        .setReceiverId(user2.getId())
        .build();

    List<ObjectId> recentContacts = new ArrayList<>();
    recentContacts.add(user2.getId());
    recentContacts.add(user3.getId());

    messageService.add(message1_2);
    messageService.add(message3_1);
    messageService.add(message3_2);

    String token = getAuthTokenFromUserId(user1.getId().toString());
    Response response = controller.getRecentlyInteractedUsers(garbageToken);
    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

    response = controller.getRecentlyInteractedUsers(token);

    assertTrue(response.getEntity().toString().contains(recentContacts.get(0).toString()));
    assertTrue(response.getEntity().toString().contains(recentContacts.get(1).toString()));

    messageService.deleteById(message1_2.getId());
    messageService.deleteById(message3_1.getId());
    messageService.deleteById(message3_2.getId());
  }

//  @Test
//  public void testGetChats() {
//    UserModel user1 = UserModel.userBuilder().setId(new ObjectId()).setUsername("User1").build();
//    UserModel user2 = UserModel.userBuilder().setId(new ObjectId()).setUsername("User2").build();
//    UserModel user3 = UserModel.userBuilder().setId(new ObjectId()).setUsername("User3").build();
//
//    UserCredentialsParams credentials = new UserCredentialsParams();
//    credentials.setUsername(username);
//    credentials.setPassword(password);
//    credentials.setName("User");
//    Response response = controller.signup(credentials);
//    assertEquals(Response.Status.OK, response.getStatusInfo());
//    response = controller.login(credentials);
//    assertEquals(Response.Status.OK, response.getStatusInfo());
//    UserModel foundUser = userService.findBy(Filters.eq(UserModel.USERNAME_FIELD, username)).next();
//    response = controller.deleteUserAccount(foundUser.getId().toString());
//
//
//    Message message1_2 = Message.messageBuilder().setId(new ObjectId())
//            .setSenderId(user1.getId())
//            .setReceiverId(user2.getId())
//            .build();
//    Message message3_1 = Message.messageBuilder().setId(new ObjectId())
//            .setSenderId(user3.getId())
//            .setReceiverId(user1.getId())
//            .build();
//    Message message3_2 = Message.messageBuilder().setId(new ObjectId())
//            .setSenderId(user3.getId())
//            .setReceiverId(user2.getId())
//            .build();
//
//    List<UserModel> recentContacts = new ArrayList<>();
//    recentContacts.add(user2);
//    recentContacts.add(user3);
//
//    messageService.add(message1_2);
//    messageService.add(message3_1);
//    messageService.add(message3_2);
//
//    String token = getAuthTokenFromUserId(user1.getId().toString());
//    Response response = controller.getUserChats(garbageToken);
//    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
//
//    response = controller.getUserChats(token);
//
//    assertTrue(response.getEntity().equals(recentContacts.get(0)));
//    assertTrue(response.getEntity().equals(recentContacts.get(1)));
//
//    messageService.deleteById(message1_2.getId());
//    messageService.deleteById(message3_1.getId());
//    messageService.deleteById(message3_2.getId());
//  }

  @Test
  // update an already set preference - success!
  public void testSetPreferenceUpdate() {

    DatabaseService<UserModel> mockService = spy(UserService.getInstance());
    UserController controller = new UserController(mockService);

    UserModel user = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("set_pref_update_test")
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .build();
    mockService.add(user);

    List<Preference> mypref = new ArrayList<>();
    Preference dnd = new DoNotDisturb(true);
    mypref.add(dnd);

    user.setPreferences(mypref);

    String tokenForUser = getAuthTokenFromUserId(user.getId().toString());

    String userPref = "DND";
    String userPrefValue = "false";

    when(mockService.findById(user.getId())).thenReturn(user);
    Response response = controller.setPreference(garbageToken, userPref, userPrefValue);
    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
    response = controller.setPreference(tokenForUser, userPref, userPrefValue);

    assertEquals("Preference updated successfully.", response.getEntity());
    mockService.deleteById(user.getId());
  }

  // set a preference - success!
  @Test
  public void testSetPreferenceSetnew() {

    DatabaseService<UserModel> mockService = spy(UserService.getInstance());
    UserController controller = new UserController(mockService);

    UserModel user = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("set_preferences_test")
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .build();
    mockService.add(user);

    List<Preference> mypref = new ArrayList<>();

    user.setPreferences(mypref);

    String tokenForUser = getAuthTokenFromUserId(user.getId().toString());

    String userPref = "DND";
    String userPrefValue = "true";

    when(mockService.findById(user.getId())).thenReturn(user);
    Response response = controller.setPreference(garbageToken, userPref, userPrefValue);
    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
    response = controller.setPreference(tokenForUser, userPref, userPrefValue);

    assertEquals("Preference set successfully.", response.getEntity());
    mockService.deleteById(user.getId());
  }

  // get the value of a preference - success!
  @Test
  public void testGetPreferenceExists() {

    DatabaseService<UserModel> mockService = spy(UserService.getInstance());
    UserController controller = new UserController(mockService);

    UserModel user = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("username")
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .build();
    mockService.add(user);

    List<Preference> mypref = new ArrayList<>();
    Preference dnd = new DoNotDisturb(true);
    mypref.add(dnd);

    user.setPreferences(mypref);

    String tokenForUser = getAuthTokenFromUserId(user.getId().toString());

    String userPref = "DND";

    when(mockService.findById(user.getId())).thenReturn(user);
    final Response response = controller.getPreference(tokenForUser, userPref);

    assertEquals("The set preference is - true", response.getEntity());
    mockService.deleteById(user.getId());
  }

  // attempt to get the value of a preference, that is not set - unsuccessful!
  @Test
  public void testGetPreferenceDoesNotExists() {

    DatabaseService<UserModel> mockService = mock(UserService.class);
    UserController controller = new UserController(mockService);

    UserModel user = UserModel.userBuilder()
        .setId(new ObjectId("507f1f77bcf86cd799439011"))
        .setUsername("username")
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .build();
    mockService.add(user);

    List<Preference> mypref = new ArrayList<>();

    user.setPreferences(mypref);

    String tokenForUser = getAuthTokenFromUserId(user.getId().toString());

    String userPref = "DND";

    when(mockService.findById(user.getId())).thenReturn(user);
    Response response = controller.getPreference(garbageToken, userPref);
    assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
    response = controller.getPreference(tokenForUser, userPref);

    assertEquals("No preference(s) set.", response.getEntity());
  }

  @Test
  public void testSetProfilePictureDefault() {
    DatabaseService<UserModel> mockService = spy(UserService.getInstance());
    UserController controller = new UserController(mockService);

    UserModel user = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("testSetProfilePicture")
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .setPreferences(new ArrayList<>())
        .build();

    mockService.add(user);

    String tokenForUser = getAuthTokenFromUserId(user.getId().toString());
    when(mockService.findById(user.getId())).thenReturn(user);

    String encodedString = "cRH9qeL3Xy";
    ProfilePictureParams params = new ProfilePictureParams("default", encodedString, "asvg.txt");

    final Response response = controller.setProfilePicture(tokenForUser, params);
    mockService.deleteById(user.getId());
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  public void testSetProfilePictureSpecific() {
    DatabaseService<UserModel> mockService = spy(UserService.getInstance());
    UserController controller = new UserController(mockService);

    UserModel user = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("testSetProfilePicture")
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .setPreferences(new ArrayList<>())
        .build();

    UserModel user2 = UserModel.userBuilder()
        .setId(new ObjectId())
        .setUsername("testSetProfilePicture2")
        .setFollowers(Collections.emptyList())
        .setFollowing(Collections.emptyList())
        .setPreferences(new ArrayList<>())
        .build();

    mockService.add(user);
    mockService.add(user2);

    String tokenForUser = getAuthTokenFromUserId(user.getId().toString());
    when(mockService.findById(user.getId())).thenReturn(user);

    String encodedString = "cRH9qeL3Xy";
    ProfilePictureParams params = new ProfilePictureParams(user2.getId().toString(), encodedString, "asfb.txt");

    final Response response = controller.setProfilePicture(tokenForUser, params);
    mockService.deleteById(user.getId());
    mockService.deleteById(user2.getId());
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }
}
