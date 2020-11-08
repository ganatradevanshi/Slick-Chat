package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.service.dbservice.AuthenticationDataService;
import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.GroupService;
import com.neu.prattle.service.dbservice.UserService;
import com.neu.prattle.utils.BCryptUtils;
import com.neu.prattle.utils.JWTUtils;

import fse.team2.common.models.controllermodels.UserCredentialsParams;
import fse.team2.common.models.mongomodels.AuthenticationData;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fse.team2.common.models.mongomodels.Group;
import fse.team2.common.models.mongomodels.UserModel;
import fse.team2.common.utils.adapters.ObjectIdTypeAdapter;

/**
 * This class exposes REST APIs for {@link Group} resource.
 */
@Path(value = "/group")
public class GroupController {
  private static final String NO_GROUP_FOUND_MESSAGE = "NO SUCH GROUP FOUND! ";

  private DatabaseService<Group> groupService;
  private DatabaseService<UserModel> userService;
  private DatabaseService<AuthenticationData> authenticationDataService;
  private Logger logger;

  /**
   * Creates an instance of Group Controller with default value of {@link UserService} and {@link
   * GroupService}
   */
  public GroupController() {
    groupService = GroupService.getInstance();
    userService = UserService.getInstance();
    authenticationDataService = AuthenticationDataService.getInstance();
    logger = Logger.getLogger(this.getClass().getName());
  }

  /**
   * This helper method takes in an Authentication Header and gives corresponding userId.
   *
   * @param authHeader The authentication header passed from the HTTP Request
   * @return userId if token is decoded successfully
   */
  private String getIdfromAuthHeader(String authHeader) {
    return JWTUtils.validateJWToken(authHeader.replace("Bearer ", ""));
  }

  /**
   * Handles HTTP POST request for creating a group.
   *
   * @param name       -> The name of the group
   * @param authHeader -> The Authentication Header from which we would decode the token to get the
   *                   User who sent this request
   * @return -> A Response indicating the outcome of the POST request.
   */
  @POST
  @Path(value = "/account/create/{name}")
  public Response createGroup(@PathParam("name") String name,
                              @HeaderParam("Authorization") String authHeader) {
    String id = getIdfromAuthHeader(authHeader);

    // the token was not decoded successfully
    if (id == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    ObjectId userId = new ObjectId(id);
    UserModel user = userService.findById(userId);
    if (user != null) {
      List<ObjectId> usersInGroup = new ArrayList<>();
      List<ObjectId> moderators = new ArrayList<>();
      usersInGroup.add(userId);
      moderators.add(userId);
      Group group = Group.groupBuilder()
              .setId(new ObjectId())
              .setName(name)
              .setUsers(usersInGroup)
              .setModerator(moderators)
              .setPreferences(new ArrayList<>())
              .setCurrentInvitedUsers(new ArrayList<>())
              .setMessages(new ArrayList<>())
              .build();
      groupService.add(group);

      //add the group to list of Groups in User
      List<ObjectId> groupsOfUser = user.getGroups();
      groupsOfUser.add(group.getId());
      user.setGroups(groupsOfUser);
      userService.replaceOne(Filters.eq(UserModel.ID_FIELD, user.getId()), user);

      Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
      String json = gson.toJson(group);
      return Response.ok(json, MediaType.APPLICATION_JSON).build();

    } else {
      return Response.status(Response.Status.FORBIDDEN).build();
    }
  }

  /**
   * Handles HTTP Delete request to delete a group.
   *
   * @param groupId    -> id of the group to be deleted.
   * @param authHeader -> The Authentication Header from which we would decode the token to get the
   *                   User who sent this request
   * @return -> A Response indicating the outcome of the DELETE request.
   */
  @DELETE
  @Path("/account/delete/{groupId}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteGroup(@PathParam("groupId") String groupId,
                              @HeaderParam("Authorization") String authHeader) {
    ObjectId groupID = new ObjectId(groupId);
    Group group = groupService.findById(groupID);
    if (group != null) {
      List<ObjectId> moderators = group.getModerators();
      // Check that the user who calls this endpoint is a Moderator
      String token = authHeader.split(" ", 2)[1];
      String userId = JWTUtils.validateJWToken(token);
      // the token was not decoded successfully
      if (userId == null) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      // if the user who send this request is not a moderator, then return UNAUTHORIZED
      UserModel moderator = userService.findById(new ObjectId(userId));
      if (moderator != null && moderators.contains(moderator.getId())) {
        groupService.deleteById(groupID);
        //remove the group from list of Groups in User
        List<ObjectId> groups = moderator.getGroups();
        groups.remove(groupID);
        moderator.setGroups(groups);

        // replace the updated group and user in the database
        userService.replaceOne(Filters.eq(UserModel.ID_FIELD, moderator.getId()), moderator);
        return Response.ok().build();
      } else {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
    } else {
      return Response.status(Response.Status.FORBIDDEN).build();
    }
  }

  /**
   * Handles HTTP Get request to search a group by its id.
   *
   * @param name -> search query of the group.
   * @return -> A Response indicating the outcome of the GET request.
   */
  @GET
  @Path(value = "/search/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response searchGroup(@PathParam("name") String name) {

    List<Group> listOfGroups = new ArrayList<>();
    Iterator<Group> groups = groupService.findBy(Filters.regex(Group.GROUPNAME_FIELD, ".*" + name + ".*"));
    while (groups.hasNext()) {
      listOfGroups.add(groups.next());
    }

    Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
    String json = gson.toJson(listOfGroups);
    return Response.ok(json, MediaType.APPLICATION_JSON).build();
  }

  /**
   * Handles a HTTP request which would give us all thr group the user is a part of.
   *
   * @param authHeader The Authentication Header from which we would decode the token to get the
   *                   User who sent this request
   * @return Response which would have all the groups the user is a part of
   */
  @GET
  @Path(value = "/account/get-groups")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllGroups(@HeaderParam("Authorization") String authHeader) {
    String userId = getIdfromAuthHeader(authHeader);

    // the token was not decoded successfully
    if (userId == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    UserModel user = userService.findById(new ObjectId(userId));
    List<ObjectId> groupIds = user.getGroups();
    List<Group> groups = new ArrayList<>();
    for (ObjectId groupId : groupIds) {
      Group group = groupService.findById(groupId);
      groups.add(group);
    }
    Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
    String json = gson.toJson(groups);
    return Response.ok(json, MediaType.APPLICATION_JSON).build();
  }

  /**
   * Handles HTTP request which would give all the users present in the group
   *
   * @param groupId -> The id of the group we would like to get all the users of
   * @return -> A response with all the users inside that group
   */
  @GET
  @Path(value = "/get-all-users/{groupId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllUsersOfGroup(@PathParam("groupId") String groupId) {
    Group group = groupService.findById(new ObjectId(groupId));
    if (group != null) {
      List<ObjectId> userIds = group.getUsers();
      List<UserModel> users = new ArrayList<>();
      for (ObjectId userId : userIds) {
        UserModel user = userService.findById(userId);
        users.add(user);
      }
      Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
      String json = gson.toJson(users);
      return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path(value = "/invite-user/group/{groupId}/user/{userId}")
  public Response inviteUserToGroup(@PathParam("userId") String userId, @PathParam("groupId") String groupId) {
    Group group = groupService.findById(new ObjectId(groupId));
    UserModel user = userService.findById(new ObjectId(userId));

    if (user != null && group != null) {
      if (group.getCurrentInvitedUsers() != null) {
        List<ObjectId> invitedUsers = group.getCurrentInvitedUsers();
        // add the user if not already in invitation list
        if (!invitedUsers.contains(user.getId())) {
          invitedUsers.add(user.getId());
          group.setCurrentInvitedUsers(invitedUsers);
          groupService.replaceOne(Filters.eq(Group.ID_FIELD, group.getId()), group);
          return Response.ok().build();
        } else {
          return Response.status(Response.Status.FORBIDDEN).build();
        }
      }
    } else {
      return Response.status(Response.Status.FORBIDDEN).build();
    }
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  /**
   * Handles HTTP Get request to get the list of currently Invited Users a group by its id.
   *
   * @param groupId    -> id of the group
   * @param authHeader -> The Authentication Header from which we would decode the token to get the
   *                   User who sent this request
   * @return -> A Response indicating the outcome of the GET request.
   */
  @GET
  @Path(value = "/account/current-invited-users/{groupId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCurrentInvitedUsers(@PathParam("groupId") String groupId, @HeaderParam("Authorization") String authHeader) {

    ObjectId groupID = new ObjectId(groupId);
    Group group = groupService.findById(groupID);

    if (group != null) {
      List<ObjectId> moderators = group.getModerators();
      List<ObjectId> currentInvitedUserIds = group.getCurrentInvitedUsers();
      List<UserModel> currentInvitedUsers = new ArrayList<>();

      for (ObjectId userId : currentInvitedUserIds) {
        UserModel user = userService.findById(userId);
        currentInvitedUsers.add(user);
      }

      // Check that the user who calls this endpoint is a Moderator
      String userId = getIdfromAuthHeader(authHeader);

      // the token was not decoded successfully
      if (userId == null) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      // if the user who send this request is not a moderator, then return UNAUTHORIZED
      if (!moderators.contains(new ObjectId(userId))) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      } else {
        Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
        String json = gson.toJson(currentInvitedUsers);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
      }
    }
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  /**
   * Handles HTTP Post request to reject one of the users from the list of currently Invited Users
   * of a  group by its id.
   *
   * @param groupId    -> id of the group.
   * @param userId     -> id of the user whose request needs to be rejected.
   * @param authHeader -> The Authentication Header from which we would decode the token to get the
   *                   User who sent this request
   * @return -> A Response indicating the outcome of the POST request.
   */
  @POST
  @Path(value = "/account/reject-invited-user/{groupId}/user/{userId}")
  public Response rejectInvitedUser(@PathParam("groupId") String groupId, @PathParam("userId") String userId, @HeaderParam("Authorization") String authHeader) {

    ObjectId groupID = new ObjectId(groupId);
    ObjectId userID = new ObjectId(userId);
    Group group = groupService.findById(groupID);
    UserModel user = userService.findById(userID);

    if (user != null && group != null) {
      List<ObjectId> moderators = group.getModerators();
      List<ObjectId> currentInvitedUsers = group.getCurrentInvitedUsers();
      // Check that the user who calls this endpoint is a Moderator
      String id = getIdfromAuthHeader(authHeader);

      // the token was not decoded successfully
      if (id == null) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      // if the user who send this request is not a moderator, then return UNAUTHORIZED
      if (!moderators.contains(new ObjectId(id))) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      // if the list of currentInvitedUsers has that user in the list
      if (currentInvitedUsers.contains(userID)) {
        //delete that user from the List of currentInvitedUsers in the database
        currentInvitedUsers.remove(userID);
        group.setCurrentInvitedUsers(currentInvitedUsers);

        // replace the updated group in the database
        groupService.replaceOne(Filters.eq(Group.ID_FIELD, groupID), group);

        return Response.ok().build();
      }
      // if user is not present in the list, then we return failure
      return Response.status(Response.Status.FORBIDDEN).build();

    } else {
      return Response.status(Response.Status.FORBIDDEN).build();
    }
  }


  /**
   * Handles HTTP POST request to add a user to the group.
   *
   * @param groupId    -> id of the group to which we would add the user.
   * @param userId     -> id of the user who is supposed to be added.
   * @param authHeader -> The Authentication Header from which we would decode the token to get the
   *                   User who sent this request.
   * @return -> A Response indicating the outcome of this request.
   */
  @POST
  @Path(value = "/account/add-user/group/{groupId}/user/{userId}")
  public Response addUserToGroup(@PathParam("groupId") String groupId,
                                 @PathParam("userId") String userId,
                                 @HeaderParam("Authorization") String authHeader) {
    // Check that the user who calls this endpoint is a Moderator or not after JWT is integrated
    ObjectId userID = new ObjectId(userId);
    ObjectId groupID = new ObjectId(groupId);
    UserModel user = userService.findById(userID);
    Group group = groupService.findById(groupID);

    if (user != null && group != null) {
      List<ObjectId> usersInGroup = group.getUsers();
      List<ObjectId> moderators = group.getModerators();

      String id = getIdfromAuthHeader(authHeader);

      // the token was not decoded successfully
      if (id == null) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      // if the user who send this request is not a moderator, then return UNAUTHORIZED
      if (!moderators.contains(new ObjectId(id))) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      if (!usersInGroup.contains(userID)) {
        //add the user to the List of users in the database
        usersInGroup.add(userID);
        group.setUsers(usersInGroup);

        //add the group to list of Groups in User
        List<ObjectId> groups = user.getGroups();
        groups.add(groupID);
        user.setGroups(groups);

        if (group.getCurrentInvitedUsers() != null) {
          List<ObjectId> currentInvitedUsers = group.getCurrentInvitedUsers();

//        delete that user from the List of currentInvitedUsers if present
          if (currentInvitedUsers.contains(userID)) {
            currentInvitedUsers.remove(userID);
            group.setCurrentInvitedUsers(currentInvitedUsers);
          }
        }

        // replace the updated group and user in the database
        userService.replaceOne(Filters.eq(UserModel.ID_FIELD, userID), user);
        groupService.replaceOne(Filters.eq(Group.ID_FIELD, groupID), group);

        return Response.ok().build();
      }
      // if user already present in group, then we return failure

    }
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  /**
   * Handles HTTP POST request to remove a user from the group.
   *
   * @param groupId    -> id of the group from which we would remove the user.
   * @param userId     -> id of the user who is supposed to be removed.
   * @param authHeader -> The Authentication Header from which we would decode the token to get the
   *                   User who sent this request.
   * @return -> A Response indicating the outcome of this request.
   */
  @POST
  @Path(value = "/account/remove-user/group/{groupId}/user/{userId}")
  public Response removeUserFromGroup(@PathParam("groupId") String groupId,
                                      @PathParam("userId") String userId,
                                      @HeaderParam("Authorization") String authHeader) {
    ObjectId userID = new ObjectId(userId);
    ObjectId groupID = new ObjectId(groupId);
    UserModel user = userService.findById(userID);
    Group group = groupService.findById(groupID);

    if (user != null && group != null) {
      List<ObjectId> usersInGroup = group.getUsers();
      List<ObjectId> moderators = group.getModerators();

      // Check that the user who calls this endpoint is a Moderator
      String token = authHeader.split(" ", 2)[1];
      String id = JWTUtils.validateJWToken(token);
      // the token was not decoded successfully
      if (id == null) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      // if the user who send this request is not a moderator, then return UNAUTHORIZED
      if (!moderators.contains(new ObjectId(id))) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      if (usersInGroup.contains(userID)) {
        usersInGroup.remove(userID);
        group.setUsers(usersInGroup);

        //remove the group from list of Groups in User
        List<ObjectId> groups = user.getGroups();
        groups.remove(groupID);
        user.setGroups(groups);

        // replace the updated group and user in the database
        userService.replaceOne(Filters.eq(UserModel.ID_FIELD, userID), user);
        groupService.replaceOne(Filters.eq(Group.ID_FIELD, groupID), group);
        return Response.ok().build();
      } else {
        return Response.status(Response.Status.FORBIDDEN).build();
      }
    } else {
      return Response.status(Response.Status.FORBIDDEN).build();
    }

  }

  /**
   * Handles HTTP POST request to assign a moderator for the group.
   *
   * @param groupId    -> id of the group to which we would assign a moderator.
   * @param userId     -> id of the user who is supposed to be assigned as moderator.
   * @param authHeader -> The Authentication Header from which we would decode the token to get the
   *                   User who sent this request.
   * @return -> A Response indicating the outcome of this request.
   */
  @POST
  @Path(value = "/account/assign-moderator/group/{groupId}/user/{userId}")
  public Response assignModerator(@PathParam("groupId") String groupId,
                                  @PathParam("userId") String userId,
                                  @HeaderParam("Authorization") String authHeader) {
    ObjectId userID = new ObjectId(userId);
    ObjectId groupID = new ObjectId(groupId);
    Group group = groupService.findById(groupID);
    UserModel user = userService.findById(userID);

    if (group != null && user != null) {
      List<ObjectId> usersInGroup = group.getUsers();
      List<ObjectId> moderators = group.getModerators();

      // Check that the user who calls this endpoint is a Moderator
      String token = authHeader.split(" ", 2)[1];
      String id = JWTUtils.validateJWToken(token);

      // the token was not decoded successfully
      if (id == null) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      // if the user who send this request is not a moderator, then return UNAUTHORIZED
      if (!moderators.contains(new ObjectId(id))) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      if (usersInGroup.contains(userID) && !moderators.contains(userID)) {
        moderators.add(userID);
        group.setModerators(moderators);
        // replace the updated group in the database
        groupService.replaceOne(Filters.eq(Group.ID_FIELD, groupID), group);
        return Response.ok().build();
      } else {
        return Response.status(Response.Status.FORBIDDEN).build();
      }
    } else {
      return Response.status(Response.Status.FORBIDDEN).build();
    }
  }

  /**
   * Handles HTTP POST request to create group with password
   * @param credentials -> The UserCredentialsParams object decoded from the payload of POST
   *                   request.
   * @param authHeader -> The authentication header passed from the HTTP Request
   * @return -> A Response indicating the outcome of the requested operation.
   */
  @POST
  @Path(value = "/account/create-secure")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createSecureGroup(UserCredentialsParams credentials, @HeaderParam("Authorization") String authHeader) {
    String groupname = credentials.getGroupname();
    String password = credentials.getPassword();
    ObjectId groupId = credentials.getId();

    String id = getIdfromAuthHeader(authHeader);

    // the token was not decoded successfully
    if (id == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    ObjectId userId = new ObjectId(id);
    UserModel user = userService.findById(userId);
    if (user != null) {
      List<ObjectId> usersInGroup = new ArrayList<>();
      List<ObjectId> moderators = new ArrayList<>();
      usersInGroup.add(userId);
      moderators.add(userId);

      Group group = Group.groupBuilder()
              .setId(groupId)
              .setName(groupname)
              .setUsers(usersInGroup)
              .setModerator(moderators)
              .setPreferences(new ArrayList<>())
              .setCurrentInvitedUsers(new ArrayList<>())
              .setMessages(new ArrayList<>())
              .setIsSecure(true)
              .build();

      //add the group to list of Groups in User
      List<ObjectId> groupsOfUser = user.getGroups();
      groupsOfUser.add(group.getId());
      user.setGroups(groupsOfUser);
      userService.replaceOne(Filters.eq(UserModel.ID_FIELD, user.getId()), user);

      groupService.add(group);
      return createGroupAccount(group, password);

    } else {
      return Response.status(Response.Status.FORBIDDEN).build();
    }
  }

  /**
   * Handles HTTP POST request to access a group with password.
   * @param credentials -> The UserCredentialsParams object decoded from the payload of POST
   *                   request.
   * @param authHeader -> The authentication header passed from the HTTP Request
   * @return -> A Response indicating the outcome of the requested operation.
   */
  @POST
  @Path(value="/account/access-group")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response accessGroup(UserCredentialsParams credentials, @HeaderParam("Authorization") String authHeader ) {

    ObjectId groupId = credentials.getId();
    String password = credentials.getPassword();
    Group group = groupService.findById(groupId);

    String id = getIdfromAuthHeader(authHeader);
    // the token was not decoded successfully
    if (id == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    ObjectId userId = new ObjectId(id);
    UserModel user = userService.findById(userId);

    if (group != null && user != null) {
      AuthenticationData authenticationData = authenticationDataService
              .findBy(Filters.eq(AuthenticationData.ENTITY_ID, group.getId()))
              .next();
      if (BCryptUtils.verifyHash(password, authenticationData.getPassword())) {
        List<ObjectId> usersInGroup = group.getUsers();

        if (!usersInGroup.contains(userId)) {
          logger.log(Level.INFO, "ACCESS DENIED, USER NOT A MEMBER OF THE GROUP!");
          return Response.status(Response.Status.FORBIDDEN).build();
        }else{
          logger.log(Level.INFO, "GROUP ACCESS SUCCESSFUL!");
          return Response.ok().build();
        }

      } else {
        return Response.status(Response.Status.FORBIDDEN).build();
      }
    } else {
      logger.log(Level.INFO, NO_GROUP_FOUND_MESSAGE);
      return Response.status(Response.Status.FORBIDDEN).build();
    }
  }

  /**
   * Handles HTTP POST request to set a password for an already created open group.
   *
   * @param credentials -> The UserCredentialsParams object decoded from the payload of POST
   *                   request.
   * @param authHeader -> The authentication header passed from the HTTP Request
   * @return -> A Response indicating the outcome of the requested operation.
   */
  @POST
  @Path(value="/account/set-password")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response setGroupPassword (UserCredentialsParams credentials, @HeaderParam("Authorization") String authHeader ){
    ObjectId groupId = credentials.getId();
    String password = credentials.getPassword();
    Group group = groupService.findById(groupId);

    String id = getIdfromAuthHeader(authHeader);
    // the token was not decoded successfully
    if (id == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    ObjectId userId = new ObjectId(id);
    UserModel user = userService.findById(userId);

    if (group != null && user != null) {
      List<ObjectId> moderators = group.getModerators();
      if(!moderators.contains(userId)){
        logger.log(Level.INFO, "ACCESS DENIED, ONLY THE MODERATORS CAN SET A PASSWORD!");
        return Response.status(Response.Status.FORBIDDEN).build();
      }else {
        group.setIsSecure(true);
        groupService.replaceOne(Filters.eq(Group.ID_FIELD, group.getId()), group);
        logger.log(Level.INFO, "PASSWORD SET SUCCESSFULLY!");
        return createGroupAccount(group, password);
      }
    } else {
      logger.log(Level.INFO, NO_GROUP_FOUND_MESSAGE);
      return Response.status(Response.Status.FORBIDDEN).build();
    }

  }

  /**
   * Handles HTTP POST request to change/modify existing  password.
   *
   * @param credentials -> The UserCredentialsParams object decoded from the payload of POST
   *                   request.
   * @param authHeader -> The authentication header passed from the HTTP Request
   * @return -> A Response indicating the outcome of the requested operation.
   */
  @POST
  @Path(value="/account/change-password")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response changePassword (UserCredentialsParams credentials, @HeaderParam("Authorization") String authHeader){
    ObjectId groupId = credentials.getId();
    String oldPassword = credentials.getOldPassword();
    String newPassword = credentials.getNewPassword();
    Group group = groupService.findById(groupId);

    String id = getIdfromAuthHeader(authHeader);
    // the token was not decoded successfully
    if (id == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    ObjectId userId = new ObjectId(id);
    UserModel user = userService.findById(userId);

    if (group != null && user != null) {
      List<ObjectId> moderators = group.getModerators();
      if(!moderators.contains(userId)){
        logger.log(Level.INFO, "ACCESS DENIED, ONLY THE MODERATORS CAN CHANGE/MODIFY A PASSWORD!");
        return Response.status(Response.Status.FORBIDDEN).build();
      }else {
        return modifyGroupAccount(group, oldPassword, newPassword);
      }
    } else {
      logger.log(Level.INFO, NO_GROUP_FOUND_MESSAGE);
      return Response.status(Response.Status.FORBIDDEN).build();
    }
  }

  @GET
  @Path(value="account/all-groups")
  public Response fetchAllGroups(@HeaderParam("Authorization") String authHeader){
    String id = getIdfromAuthHeader(authHeader);
    // the token was not decoded successfully
    if (id == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

      List<Group> groups = new ArrayList<>();
        Iterator<Group> itr = groupService.findAll();

        while(itr.hasNext()){
          groups.add(itr.next());
        }

      Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
      String json = gson.toJson(groups);
      return Response.ok(json, MediaType.APPLICATION_JSON).build();

  }


  /**
   * This function uses the DatabaseService to add new group to the database.
   *
   * @param group -> The group object which we want to add to the Database.
   * @param password -> The password for the Group which we would store in Authentication Database.
   * @return -> A Response indicating the outcome of the insert operation.
   */
  private Response createGroupAccount(Group group, String password){
    try {
      ObjectId authenticationDataId = new ObjectId();
      AuthenticationData authenticationData = new AuthenticationData(authenticationDataId, group.getId(), BCryptUtils.hash(password));
      authenticationDataService.add(authenticationData);
    } catch (UserAlreadyPresentException | MongoWriteException e) {
      logger.log(Level.SEVERE, e.getMessage());
      return Response.status(Response.Status.CONFLICT).build();
    }
    Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
    String json = gson.toJson(group);
    return Response.ok(json, MediaType.APPLICATION_JSON).build();

  }

  /**
   * This function uses the DatabaseService to modify exsiting group to the database.
   *
   * @param group -> The group object which we want to add to the Database.
   * @param oldPassword -> The oldPassword set for the Group which we would be in Authentication Database.
   * @param newPassword -> The newPassword for the Group which we would update in Authentication Database.
   * @return -> A Response indicating the outcome of the insert operation.
   */
  private Response modifyGroupAccount(Group group, String oldPassword, String newPassword){

      // find the authDataEntry by group id
      AuthenticationData authGroup = authenticationDataService.findBy(Filters.eq(AuthenticationData.ENTITY_ID, group.getId())).next();
      //if oldPassword matches the password in the database
      if (BCryptUtils.verifyHash(oldPassword, authGroup.getPassword())) {
        //set the password to new password
        authGroup.setPassword(BCryptUtils.hash(newPassword));
        //replace that entry with new

        authenticationDataService.replaceOne(Filters.eq(AuthenticationData.ENTITY_ID, group.getId()), authGroup);
        logger.log(Level.INFO, "PASSWORD CHANGED SUCCESSFULLY!");
      } else {
        logger.log(Level.INFO, "INCORRECT OLD PASSWORD!");
        return Response.status(Response.Status.FORBIDDEN).build();
      }

    Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
    String json = gson.toJson(group);
    return Response.ok(json, MediaType.APPLICATION_JSON).build();

  }

}


