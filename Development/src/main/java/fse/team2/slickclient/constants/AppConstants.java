package fse.team2.slickclient.constants;

import okhttp3.MediaType;

/**
 * Defines constants for API hosts and endpoints used across client application.
 */
public class AppConstants {
  public static final String HOSTNAME = "http://128.31.25.60:8080/prattle/rest";

  /* ----------- User Endpoints ---------*/
  public static final String WS_CONNECT_ENDPOINT = "ws://128.31.25.60:8080/prattle/main/{username}/";
  public static final String LOGIN_ENDPOINT = "/user/login";
  public static final String REGISTER_ENDPOINT = "/user/signup";
  public static final String FOLLOW_ENDPOINT = "/user/account/follow/{followeeId}";
  public static final String UNFOLLOW_ENDPOINT = "/user/account/unfollow/{followeeId}";
  public static final String SEARCH_USER_ENDPOINT = "/user/search/{username}";
  public static final String GET_CHATS_ENDPOINT = "/user/account/recent-contacts";
  public static final String DELETE_ACCOUNT_ENDPOINT = "/user/account/remove";
  public static final String SET_PROFILE_PICTURE_ENDPOINT = "/user/account/profile-picture";
  public static final String GET_PROFILE_ENDPOINT = "/user/account/{id}";

  /* ---------- Message Endpoints ---------- */
  public static final String GET_MESSAGES_ENDPOINT = "/message/account/get-messages/user/{userId}";
  public static final String DELETE_MESSAGE_ENDPOINT = "/message/account/delete-message//{messageId}";
  public static final String CREATE_MEDIA_MESSAGE_ENDPOINT = "/message/media";

  /* ------------ Group Endpoints ---------- */
  public static final String CREATE_GROUP_ENDPOINT = "/group/create/{name}";
  public static final String DELETE_GROUP_ENDPOINT = "/group/delete/{groupId}";
  public static final String SEARCH_GROUP_ENDPOINT = "/group/search/{id}";
  public static final String ADD_USER_TO_GROUP_ENDPOINT = "/group/addUser/group/{groupId}/user/{userId}";
  public static final String REMOVE_USER_FROM_GROUP_ENDPOINT = "/removeUser/group/{groupId}/user/{userId}";
  public static final String ASSIGN_MODERATOR_TO_GROUP_ENDPOINT = "/group/assignModerator/group/{groupId}/user/{userId}";

  public static final MediaType JSON = MediaType.parse("application/json");
}
