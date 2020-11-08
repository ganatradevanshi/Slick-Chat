package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.service.dbservice.AuthenticationDataService;
import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.MessageService;
import com.neu.prattle.service.dbservice.SpecificProfilePictureService;
import com.neu.prattle.service.dbservice.UserActivityService;
import com.neu.prattle.service.dbservice.UserService;
import com.neu.prattle.service.mediaservice.AWSMediaService;
import com.neu.prattle.service.mediaservice.MediaService;
import com.neu.prattle.utils.BCryptUtils;
import com.neu.prattle.utils.JWTUtils;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fse.team2.common.models.controllermodels.ProfilePictureParams;
import fse.team2.common.models.controllermodels.UserCredentialsParams;
import fse.team2.common.models.mongomodels.AuthenticationData;
import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.SpecificProfilePicture;
import fse.team2.common.models.mongomodels.UserActivityData;
import fse.team2.common.models.mongomodels.UserModel;
import fse.team2.common.models.mongomodels.enums.PreferenceType;
import fse.team2.common.models.mongomodels.preferences.DefaultProfilePicture;
import fse.team2.common.models.mongomodels.preferences.DoNotDisturb;
import fse.team2.common.models.mongomodels.preferences.Preference;
import fse.team2.common.utils.adapters.ObjectIdTypeAdapter;

/**
 * This class exposes REST APIs for {@link UserModel} resource.
 */
@Path(value = "/user")
public class UserController {

    private DatabaseService<UserModel> userService;
    private DatabaseService<AuthenticationData> authenticationDataService;
    private DatabaseService<Message> messageService;
    private DatabaseService<UserActivityData> userActivityService;
    private MediaService mediaService;
    private DatabaseService<SpecificProfilePicture> specificProfilePictureDatabaseService;
    private Logger logger;
    private DateTimeFormatter dtf;
    private LocalDateTime currentTime;
    private static final String BEARER_CONST = "Bearer ";

    /**
     * Creates a {@link UserController} instance with the default {@link UserService}, {@link
     * AuthenticationDataService}, {@link MessageService}.
     */
    public UserController() {
        userService = UserService.getInstance();
        authenticationDataService = AuthenticationDataService.getInstance();
        messageService = MessageService.getInstance();
        mediaService = AWSMediaService.getInstance();
        userActivityService = UserActivityService.getInstance();
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        currentTime = LocalDateTime.now();
        this.specificProfilePictureDatabaseService = SpecificProfilePictureService.getInstance();
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Creates a {@link UserController} instance with the default {@link UserService}, {@link
     * AuthenticationDataService}, {@link MessageService}.
     */
    public UserController(DatabaseService<UserModel> userService) {
        this.userService = userService;
        authenticationDataService = AuthenticationDataService.getInstance();
        messageService = MessageService.getInstance();
        this.mediaService = AWSMediaService.getInstance();
        this.specificProfilePictureDatabaseService = SpecificProfilePictureService.getInstance();
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * This helper function takes in a string which is username and returns the corresponding user
     * object.
     *
     * @param username -> The username of the user we want.
     * @return -> User object.
     */
    private UserModel findUserByUsername(String username) {
        try {
            return userService.findBy(Filters.eq(UserModel.USERNAME_FIELD, username)).next();
        } catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a list of {@link UserModel} which contain the provided sequence of characters in
     * their username.
     *
     * @param sequence - sequence of characters that should be part of the user's username.
     * @return - list of {@link UserModel} which contain the provided sequence of characters in their
     * username.
     */
    private List<UserModel> searchUsersByUsername(String sequence) {
        List<UserModel> listOfUsers = new ArrayList<>();
        try {
            Iterator<UserModel> users = userService.findBy(Filters.regex(UserModel.USERNAME_FIELD, ".*" + sequence + ".*"));
            while (users.hasNext()) {
                listOfUsers.add(users.next());
            }
            return listOfUsers;
        } catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return listOfUsers;
    }

    /**
     * This function uses to the DatabaseService to add a new User to the Database.
     *
     * @param user     -> The User object which we want to add to the Database.
     * @param password -> The password for the User Account which we would store in Authentication
     *                 Data.
     * @return -> A Response indicating the outcome of the insert operation.
     */
    private Response createUserAccount(UserModel user, String password) {
        try {
            userService.add(user);
            ObjectId authenticationDataId = new ObjectId();
            AuthenticationData authenticationData = new AuthenticationData(authenticationDataId, user.getId(), BCryptUtils.hash(password));
            authenticationDataService.add(authenticationData);

        } catch (UserAlreadyPresentException | MongoWriteException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.ok().build();
    }

    /**
     * This helper method takes in an Authentication Header and gives corresponding userId.
     *
     * @param authHeader The authentication header passed from the HTTP Request
     * @return userId if token is decoded successfully
     */
    private String getIdfromAuthHeader(String authHeader) {
        return JWTUtils.validateJWToken(authHeader.replace(BEARER_CONST, ""));
    }

    private void trackUserWhileLogin(UserModel user, String ipAddress) {
        UserActivityData userActivityData = UserActivityData.userActivityBuilder()
                .setId(new ObjectId())
                .setUser(user)
                .setLoggedInTime(dtf.format(currentTime))
                .setIpAddress(ipAddress)
                .build();
        userActivityService.add(userActivityData);
    }

    /**
     * Handles HTTP POST request to login an existing user.
     *
     * @param credentials -> The UserCredentialsParams object decoded from the payload of POST
     *                    request.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @POST
    @Path(value = "/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(UserCredentialsParams credentials, @Context HttpServletRequest request) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        UserModel user = findUserByUsername(username);

        if (user != null) {
            if (user.isOAuthUser()) {
                Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
                String json = gson.toJson(user);

                //track user if isTracked true
                if (user.isTracked()) {
                    String ipAddress = request.getRemoteAddr();
                    trackUserWhileLogin(user, ipAddress);
                }
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            }
            AuthenticationData authenticationData = authenticationDataService
                    .findBy(Filters.eq(AuthenticationData.ENTITY_ID, user.getId()))
                    .next();
            if (BCryptUtils.verifyHash(password, authenticationData.getPassword())) {
                logger.log(Level.INFO, "LOGIN SUCCESSFUL!");
                String authToken = JWTUtils.generateJWToken(user.getId().toString());
                user.setToken(authToken);

                //track user if isTracked true
                if (user.isTracked()) {
                    String ipAddress = request.getRemoteAddr();
                    trackUserWhileLogin(user, ipAddress);
                }
                Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
                String json = gson.toJson(user);
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            } else {
                logger.log(Level.INFO, "INCORRECT PASSWORD!");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } else {
            logger.log(Level.INFO, "NO SUCH RECORD FOUND, SIGN UP FIRST.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path(value = "/account/logout")
    public Response logout(@HeaderParam("Authorization") String authHeader, @Context HttpServletRequest request) {
        String userId = getIdfromAuthHeader(authHeader);

        // the token was not decoded successfully
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        UserModel user = userService.findById(new ObjectId(userId));
        if (user != null && user.isTracked()) {
            //track user if isTracked true
            String ipAddress = request.getRemoteAddr();
            UserActivityData userActivityData = UserActivityData.userActivityBuilder()
                    .setId(new ObjectId())
                    .setUser(user)
                    .setLogOutTime(dtf.format(currentTime))
                    .setIpAddress(ipAddress)
                    .build();
            userActivityService.add(userActivityData);
        }
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Handles a HTTP POST request to create a new user.
     *
     * @param credentials -> The UserCredentialsParams object decoded from the payload of POST
     *                    request.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @POST
    @Path(value = "/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signup(UserCredentialsParams credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        ObjectId userId = credentials.getId();
        String name;
        if (credentials.getName() == null) {
            name = "";
        } else {
            name = credentials.getName();
        }

        boolean isOAuth = false;
        if (credentials.getOAuth() != null && credentials.getOAuth().equalsIgnoreCase("true")) {
            isOAuth = true;
        }


        UserModel user = UserModel.userBuilder()
                .setId(userId)
                .setUsername(username)
                .setName(name)
                .setIsOAuthUser(isOAuth)
                .setFollowers(Collections.emptyList())
                .setFollowing(Collections.emptyList())
                .setMessages(Collections.emptyList())
                .setGroups(Collections.emptyList())
                .setPreferences(Collections.emptyList())
                .build();

        return createUserAccount(user, password);
    }

    /**
     * Handles a HTTP POST request to delete a User Data from User model and Authentication Data from
     * Authentication Data Model. [TO BE CHANGED -> It should accept UserCredentialsParams]
     *
     * @param id -> The UserId decoded from the the URL of POST request.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @DELETE
    @Path("/account/remove/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUserAccount(@PathParam("id") String id) {
        UserModel user = userService.findById(new ObjectId(id));
        if (user != null) {
            AuthenticationData authenticationData = authenticationDataService.findBy(Filters.eq(AuthenticationData.ENTITY_ID, user.getId())).next();
            userService.deleteById(user.getId());
            authenticationDataService.deleteById(authenticationData.getId());
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /**
     * Handles a HTTP GET request and returns the corresponding User/Users from the username given.
     *
     * @param username -> The username of the pattern of username we want to search users for.
     * @return -> A Response with body containing all the information regarding the present user.
     */
    @GET
    @Path(value = "/search/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchUser(@PathParam("username") String username) {
        List<UserModel> listOfUsers = searchUsersByUsername(username);
        if (!listOfUsers.isEmpty()) {
            Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
            String json = gson.toJson(listOfUsers);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Handles a HTTP POST request and returns a response as a User is following another User.
     *
     * @param followeeId -> The id of the User we are supposed to follow
     * @param authHeader -> The Authentication Header from which we would decode the token to get the
     *                   User who sent this request
     * @return -> A Response indicating whether user was successful in following other User
     */
    @POST
    @Path(value = "/account/follow/{followeeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response follow(@PathParam("followeeId") String followeeId, @HeaderParam("Authorization") String authHeader) {
        String userId = getIdfromAuthHeader(authHeader);

        // the token was not decoded successfully
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserModel follower = userService.findById(new ObjectId(userId));
        UserModel followee = userService.findById(new ObjectId(followeeId));

        if (follower != null && followee != null) {
            List<ObjectId> following = follower.getFollowing();
            if (!following.contains(followee.getId())) {
                following.add(followee.getId());
                follower.setFollowing(following);
            }

            List<ObjectId> followers = followee.getFollowers();
            if (!followers.contains(follower.getId())) {
                followers.add(follower.getId());
                followee.setFollowers(followers);
            }

            userService.replaceOne(Filters.eq(UserModel.ID_FIELD, follower.getId()), follower);
            userService.replaceOne(Filters.eq(UserModel.ID_FIELD, followee.getId()), followee);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /**
     * Handles a HTTP POST request and returns a response as a User is unfollowing another User.
     *
     * @param followeeId -> The id of the User we are supposed to unfollow
     * @param authHeader -> The Authentication Header from which we would decode the token to get the
     *                   User who sent this request
     * @return -> A Response indicating whether user was successful in following other User.
     */
    @POST
    @Path(value = "/account/unfollow/{followeeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response unfollow(@PathParam("followeeId") String followeeId, @HeaderParam("Authorization") String authHeader) {
        String userId = getIdfromAuthHeader(authHeader);

        // the token was not decoded successfully
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserModel follower = userService.findById(new ObjectId(userId));
        UserModel followee = userService.findById(new ObjectId(followeeId));

        if (follower != null && followee != null) {
            List<ObjectId> following = follower.getFollowing();
            if (following.contains(followee.getId())) {
                following.remove(followee.getId());
                follower.setFollowing(following);
            }

            List<ObjectId> followers = followee.getFollowers();
            if (followers.contains(follower.getId())) {
                followers.remove(follower.getId());
                followee.setFollowers(followers);
            }

            userService.replaceOne(Filters.eq(UserModel.ID_FIELD, follower.getId()), follower);
            userService.replaceOne(Filters.eq(UserModel.ID_FIELD, followee.getId()), followee);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /**
     * Handles a HTTP GET request and returns a response which has User whose Id we passed in the URL
     * to find.
     *
     * @param authHeader -> The Authentication Header from which we would decode the token to get the
     *                   User who sent this request
     * @param id         -> The Id of the User we want to search in the Database.
     * @return -> A Response indicating whether the user was found or not along with the found User.
     */
    @GET
    @Path(value = "/account/v1/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserById(@HeaderParam("Authorization") String authHeader, @PathParam("id") String id) {
        String userId = getIdfromAuthHeader(authHeader);

        UserModel user = userService.findById(new ObjectId(id));
        if (user != null) {
            Iterator<SpecificProfilePicture> it = specificProfilePictureDatabaseService.findBy(Filters.and(
                    Filters.eq(SpecificProfilePicture.FOR_USER_FIELD, new ObjectId(userId)),
                    Filters.eq(SpecificProfilePicture.USER_ID_FIELD, new ObjectId(id))));

            SpecificProfilePicture specificProfilePicture = null;
            if (it.hasNext()) {
                specificProfilePicture = it.next();
            }
            if (specificProfilePicture != null) {
                List<Preference> preferences = user.getPreferences();
                Iterator<Preference> preferenceIterator = preferences.iterator();
                Preference profilePicturePref = null;
                while (preferenceIterator.hasNext()) {
                    Preference p = preferenceIterator.next();
                    if (p.getPreferenceType() == PreferenceType.DEFAULT_PROFILE_PICTURE) {
                        profilePicturePref = p;
                    }
                }

                if (profilePicturePref != null) {
                    preferences.remove(profilePicturePref);
                }

                preferences.add(new DefaultProfilePicture(specificProfilePicture.getUrl()));
            }

            Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
            String json = gson.toJson(user);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    /**
     * Handles a HTTP GET request and returns a response which has User whose Id we passed in the URL
     * to find.
     *
     * @param id         -> The Id of the User we want to search in the Database.
     * @param authHeader -> The Authentication Header from which we would decode the token to get the
     *                   User who sent this request
     * @return -> A Response indicating whether the user was found or not along with the found User.
     */
    @GET
    @Path(value = "/account/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserById2(@PathParam("id") String id, @HeaderParam("Authorization") String authHeader) {
        String token = authHeader.replace(BEARER_CONST, "");
        String userId = JWTUtils.validateJWToken(token);

        // the token was not decoded successfully
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserModel user = userService.findById(new ObjectId(id));
        if (user != null) {
            Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
            String json = gson.toJson(user);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }


    /**
     * Handles a HTTP PUT request which would take in an User Object and replace that object with the
     * one already present.
     *
     * @param updatedUser The updated User object which we would like to replace with the one already
     *                    present in DB.
     * @return A Response indicating whether the user user was updated successfully or not.
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(UserModel updatedUser) {
        UserModel userToUpdate = userService.findById(updatedUser.getId());
        if (userToUpdate != null) {
            userService.replaceOne(Filters.eq(UserModel.ID_FIELD, userToUpdate.getId()), updatedUser);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    /**
     * Handles a HTTP GET request and returns a response which has List of recently contacted users.
     *
     * @param authHeader -> The Authentication Header from which we would decode the token to get the
     *                   User who sent this request
     * @return -> A Response containing the list of contacts.
     */
    @GET
    @Path(value = "/account/recent-contacts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecentlyInteractedUsers(@HeaderParam("Authorization") String authHeader) {
        String id = getIdfromAuthHeader(authHeader);

        // the token was not decoded successfully
        if (id == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        ObjectId userId = new ObjectId(id);
        Set<ObjectId> recentContacts = new HashSet<>();

        Iterator<Message> sentMessages = messageService.findBy(Filters.eq(Message.SENDER_ID_FIELD, userId));
        Iterator<Message> receivedMessages = messageService.findBy(Filters.eq(Message.RECEIVER_ID_FIELD, userId));

        while (sentMessages.hasNext()) {
            recentContacts.add(sentMessages.next().getReceiverId());
        }

        while (receivedMessages.hasNext()) {
            recentContacts.add(receivedMessages.next().getSenderId());
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
        String json = gson.toJson(new ArrayList<>(recentContacts));

        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    /**
     * Handles a HTTP GET request and returns a response which has List of recently contacted users.
     *
     * @param authHeader -> The Authentication Header from which we would decode the token to get the
     *                   User who sent this request
     * @return -> A Response containing the list of contacts.
     */
    @GET
    @Path(value = "/account/chats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserChats(@HeaderParam("Authorization") String authHeader) {
        String token = authHeader.replace(BEARER_CONST, "");
        String id = JWTUtils.validateJWToken(token);

        // the token was not decoded successfully
        if (id == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        ObjectId userId = new ObjectId(id);
        Set<UserModel> recentChats = new HashSet<>();

        Iterator<Message> sentMessages = messageService.aggregate(Filters.and(
                Filters.eq(Message.SENDER_ID_FIELD, userId),
                Filters.eq(Message.IS_GROUP_MESSAGE_FIELD, false)),
                "users", "receiverId", "_id", "userData");

        Iterator<Message> receivedMessages = messageService.aggregate(Filters.and(
                Filters.eq(Message.SENDER_ID_FIELD, userId),
                Filters.eq(Message.IS_GROUP_MESSAGE_FIELD, false)),
                "users", "senderId", "_id", "userData");

        while (sentMessages.hasNext()) {
            recentChats.add(sentMessages.next().getUserData().get(0));
        }

        while (receivedMessages.hasNext()) {
            recentChats.add(receivedMessages.next().getUserData().get(0));
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
        String json = gson.toJson(new ArrayList<>(recentChats));

        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    /**
     * Handles an HTTP GET request which takes in userid and preference type to get the value set for
     * that particular preference.
     *
     * @param authHeader -> The Authentication Header from which we would decode the token to get the
     *                   User who sent this request
     * @param prefType   The type of preference for which the value needs to be retrieved.
     * @return A Response indicating whether the preference value was retrieved successfully or not.
     */
    @GET
    @Path(value = "/account/preference/{prefType}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPreference(@HeaderParam("Authorization") String authHeader, @PathParam("prefType") String prefType) {
        String id = getIdfromAuthHeader(authHeader);

        // the token was not decoded successfully
        if (id == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        ObjectId userId = new ObjectId(id);

        PreferenceType preferenceT = PreferenceType.valueOf(prefType);
        UserModel targetUser = userService.findById(userId);
        List<Preference> list = targetUser.getPreferences();
        Response res = null;

        if (list.isEmpty()) {
            String resp = "No preference(s) set.";
            res = Response.ok(resp).build();
        } else {
            Iterator<Preference> it = list.iterator();
            while (it.hasNext()) {
                Preference p = it.next();
                if (p.getPreferenceType() == preferenceT) {
                    String resp = "The set preference is - " + p.getValue();
                    res = Response.ok(resp).build();
                } else {
                    res = Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
        }
        return res;

    }

    /**
     * Handles an HTTP POST request which takes in userid, preference to be set and value for that
     * preference, and sets that preference for the user with the given userid.
     *
     * @param authHeader -> The Authentication Header from which we would decode the token to get the
     *                   User who sent this request
     * @param prefType   The type of preference that needs to be set
     * @param prefValue  The value of the preference type
     * @return A Response indicating the preference was set or updated successfully.
     */
    @POST
    @Path(value = "/account/preference/{prefType}/{prefValue}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setPreference(@HeaderParam("Authorization") String authHeader, @PathParam("prefType") String prefType,
                                  @PathParam("prefValue") String prefValue) {

        String id = getIdfromAuthHeader(authHeader);

        // the token was not decoded successfully
        if (id == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        ObjectId userId = new ObjectId(id);

        PreferenceType preferenceT = PreferenceType.valueOf(prefType);
        Preference pref = null;
        String resp = null;

        if (preferenceT == PreferenceType.DND) {
            pref = new DoNotDisturb(prefValue);
        }
        if (preferenceT == PreferenceType.DEFAULT_PROFILE_PICTURE) {
            pref = new DefaultProfilePicture(prefValue);
        }
        if (pref == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        UserModel targetUser = userService.findById(userId);
        List<Preference> list = targetUser.getPreferences();

        if (list.isEmpty()) {
            list.add(pref);
            resp = "Preference set successfully.";
        } else {
            Iterator<Preference> it = list.iterator();
            while (it.hasNext()) {
                Preference p = it.next();
                if (p.getPreferenceType() == preferenceT) {
                    p.setValue(prefValue);
                    resp = "Preference updated successfully.";
                }
            }
        }

        targetUser.setPreferences(list);
        BasicDBObject updateQuery = new BasicDBObject();
        updateQuery.append(UserModel.ID_FIELD, targetUser.getId());
        userService.updateOne(updateQuery, new BasicDBObject("$set",
                new BasicDBObject(UserModel.PREFERENCES_FIELD, targetUser.getPreferences())));

        return Response.ok(resp).build();
    }

    /**
     * API to set profile picture for a user.
     *
     * @param authHeader - auth header of the logged in user.
     * @param params     - params to include profile picture contents and user id of the user.
     */
    @POST
    @Path(value = "/account/profile-picture")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setProfilePicture(@HeaderParam("Authorization") String authHeader, ProfilePictureParams params) {
        String userId = getIdfromAuthHeader(authHeader);
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(params.getProfilePictureContents());
            if (params.getForUser().equals(userId) || params.getForUser().equals(ProfilePictureParams.DEFAULT_KEYWORD_FOR_NOW)) {
                String url = mediaService.upload(decodedBytes, userId + "_" + "profile_picture" + "_" + params.getFileName());
                return setPreference(authHeader, PreferenceType.DEFAULT_PROFILE_PICTURE.toString(), url);
            } else {
                String url = mediaService.upload(decodedBytes, userId + "_" + params.getForUser() + "profile_picture" + "_" + params.getFileName());
                // Handling Specific user profile picture
                this.specificProfilePictureDatabaseService.add(
                        new SpecificProfilePicture(new ObjectId(),
                                new ObjectId(userId),
                                new ObjectId(params.getForUser()), url));

                return Response.status(Response.Status.OK).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
