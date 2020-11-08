package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mongodb.client.model.Filters;
import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.GroupService;
import com.neu.prattle.service.dbservice.MessageService;
import com.neu.prattle.service.dbservice.UserActivityService;
import com.neu.prattle.service.dbservice.UserService;
import com.neu.prattle.utils.JWTUtils;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fse.team2.common.models.controllermodels.UserInformation;
import fse.team2.common.models.controllermodels.UserMessagesInformation;
import fse.team2.common.models.mongomodels.Group;
import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.UserActivityData;
import fse.team2.common.models.mongomodels.UserModel;
import fse.team2.common.models.mongomodels.enums.UserType;
import fse.team2.common.utils.adapters.ObjectIdTypeAdapter;

/**
 * This class exposes REST APIs for Government related uses.
 */
@Path(value = "/govt")
public class GovernmentRegulationController {
    private DatabaseService<UserModel> userService;
    private DatabaseService<Group> groupService;
    private DatabaseService<Message> messageService;
    private DatabaseService<UserActivityData> userActivityService;
    private static final String BEARER_CONST = "Bearer ";

    public GovernmentRegulationController() {
        userService = UserService.getInstance();
        groupService = GroupService.getInstance();
        messageService = MessageService.getInstance();
        userActivityService = UserActivityService.getInstance();
    }

    /**
     * Retrieve username of the user defined by the provided user id.
     *
     * @param userId - id of the user object.
     * @return - username of the user corresponding to the user id.
     */
    private String getUsernameFromUserId(ObjectId userId) {
        UserModel user = userService.findById(userId);
        if (user != null) {
            return user.getUsername();
        } else {
            return "";
        }
    }

    /**
     * Retrieve name of the group defined by the provided group id.
     *
     * @param groupId - id of the group object.
     * @return - name of the group corresponding to the group id.
     */
    private String getGroupNameFromGroupId(ObjectId groupId) {
        Group group = groupService.findById(groupId);
        if (group != null) {
            return group.getName();
        } else {
            return "";
        }
    }

    /**
     * Retrieve id from the auth header.
     *
     * @param authHeader - auth header in form of the string.
     * @return - id of the user.
     */
    private String getIdfromAuthHeader(String authHeader) {
        return JWTUtils.validateJWToken(authHeader.replace(BEARER_CONST, ""));
    }

    /**
     * Return true if the user is a government type of a user.
     *
     * @param userId - id of the user.
     * @return - Return true if the user is a government type of a user, false otherwise.
     */
    private boolean checkGovernmentUser(String userId) {
        ObjectId id = new ObjectId(userId);
        UserModel user = userService.findById(id);
        if (user != null) {
            return user.getTypeOfUser().equals(UserType.GOVERNMENT);
        } else {
            return false;
        }
    }

    /**
     * Turn tracking on for the user specified.
     *
     * @param userId     - id of the user.
     * @param authHeader - authheader corresponding to the user.
     */
    @POST
    @Path(value = "/track/{userId}")
    public Response trackUser(@PathParam("userId") String userId, @HeaderParam("Authorization") String authHeader) {
        String id = getIdfromAuthHeader(authHeader);
        // the token was not decoded successfully
        if (id == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (!checkGovernmentUser(id)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserModel user = userService.findById(new ObjectId(userId));
        if (user != null) {
            user.setTracked(true);
            // modify the instance in the DB
            userService.replaceOne(Filters.eq(UserModel.ID_FIELD, user.getId()), user);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /**
     * Retrieve tracked user info for the user specified.
     *
     * @param userId - id of the user.
     */
    @GET
    @Path(value = "/tracked-user-info/{userId}")
    public Response getTrackedUserInfo(@PathParam("userId") String userId) {
        UserModel user = userService.findById(new ObjectId(userId));
        if (user != null) {
            Iterator<UserActivityData> userActivityData = userActivityService.findBy(Filters.eq(UserActivityData.USERNAME_FIELD, user.getUsername()));
            List<UserActivityData> data = new ArrayList<>();

            while (userActivityData.hasNext()) {
                data.add(userActivityData.next());
            }

            Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
            String json = gson.toJson(data);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /**
     * Turn tracking off for the user specified.
     *
     * @param userId     - id of the user.
     * @param authHeader - authheader corresponding to the user.
     */
    @POST
    @Path(value = "/untrack/{userId}")
    public Response untrackUser(@PathParam("userId") String userId, @HeaderParam("Authorization") String authHeader) {
        String id = getIdfromAuthHeader(authHeader);
        // the token was not decoded successfully
        if (id == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (!checkGovernmentUser(id)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserModel user = userService.findById(new ObjectId(userId));
        if (user != null) {
            user.setTracked(false);
            // modify the instance in the DB
            userService.replaceOne(Filters.eq(UserModel.ID_FIELD, user.getId()), user);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /**
     * Retrieve all the information
     *
     * @param authHeader - authheader corresponding to the government user.
     */
    @GET
    @Path(value = "/all-information")
    public Response getAllInformation(@HeaderParam("Authorization") String authHeader) {
        String govtId = getIdfromAuthHeader(authHeader);
        // the token was not decoded successfully
        if (govtId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (!checkGovernmentUser(govtId)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Iterator<UserModel> allUsers = userService.findAll();
        UserInformation info;
        List<UserInformation> userInformation = new ArrayList<>();

        while (allUsers.hasNext()) {
            UserModel user = allUsers.next();
            info = new UserInformation();
            info.setId(user.getId().toString());
            info.setUsername(user.getUsername());
            if (user.getName() != null) {
                info.setName(user.getName());
            }


            if (user.getFollowers() != null) {
                List<String> userFollowers = new ArrayList<>();
                for (ObjectId id : user.getFollowers()) {
                    userFollowers.add(getUsernameFromUserId(id));
                }
                info.setFollowers(userFollowers);
            }


            if (user.getFollowing() != null) {
                List<String> userFollowings = new ArrayList<>();
                for (ObjectId id : user.getFollowing()) {
                    userFollowings.add(getUsernameFromUserId(id));
                }
                info.setFollowing(userFollowings);
            }


            if (user.getGroups() != null) {
                List<String> groups = new ArrayList<>();
                for (ObjectId id : user.getGroups()) {
                    groups.add(getGroupNameFromGroupId(id));
                }
                info.setGroups(groups);
            }

            userInformation.add(info);
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
        String json = gson.toJson(userInformation);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    /**
     * Retrieve messages of the specified user.
     *
     * @param userId     - id of the user.
     * @param authHeader - authheader corresponding to the user.
     */
    @GET
    @Path(value = "/get-messages/{userId}")
    public Response getMessageInfoForUser(@PathParam("userId") String userId, @HeaderParam("Authorization") String authHeader) {
        String govtId = getIdfromAuthHeader(authHeader);
        // the token was not decoded successfully
        if (govtId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (!checkGovernmentUser(govtId)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserModel user = userService.findById(new ObjectId(userId));
        if (user != null) {
            UserMessagesInformation info;

            List<Message> allMessages = new ArrayList<>();

            Iterator<Message> sentMessages = messageService.findBy(Filters.eq(Message.SENDER_ID_FIELD, user.getId()));
            Iterator<Message> receivedMessages = messageService.findBy(Filters.eq(Message.RECEIVER_ID_FIELD, user.getId()));

            while (sentMessages.hasNext()) {
                allMessages.add(sentMessages.next());
            }

            while (receivedMessages.hasNext()) {
                allMessages.add(receivedMessages.next());
            }

            List<UserMessagesInformation> userMessagesInformation = new ArrayList<>();

            for (Message message : allMessages) {
                info = new UserMessagesInformation();
                info.setUsername(user.getUsername());
                info.setSender(getUsernameFromUserId(message.getSenderId()));
                info.setMessage(message.getContent());
                if (message.isGroupMessage()) {
                    info.setGroup(getUsernameFromUserId(message.getReceiverId()));
                } else {
                    info.setReceiver(getUsernameFromUserId(message.getReceiverId()));
                }

                userMessagesInformation.add(info);
            }

            Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
            String json = gson.toJson(userMessagesInformation);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
