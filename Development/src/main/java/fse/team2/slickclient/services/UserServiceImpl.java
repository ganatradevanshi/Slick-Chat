package fse.team2.slickclient.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import fse.team2.slickclient.model.ClientMessagePojo;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

import javax.websocket.ContainerProvider;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.websockets.commands.ServerCommand;
import fse.team2.common.websockets.commands.enums.ServerCommandType;
import fse.team2.slickclient.constants.AppConstants;
import fse.team2.slickclient.model.ClientUserPojo;
import fse.team2.slickclient.utils.LoggerService;
import fse.team2.slickclient.websockets.MainClientEndpoint;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.bson.types.ObjectId;

public class UserServiceImpl implements UserService {
    private static UserServiceImpl singleInstance = null;
    private HttpService httpService;
    private Map<String, String> headers;
    private Map<String, String> bodyParams;
    private Session session;
    private ClientUserPojo user;

    private UserServiceImpl(HttpService httpService) {
        this.httpService = httpService;
    }

    public static UserServiceImpl getInstance(HttpService httpService) {
        if (singleInstance == null) {
            singleInstance = new UserServiceImpl(httpService);
        }
        return singleInstance;
    }

    // Only for testing purposes
    public static UserServiceImpl getInstanceNonSingleton(HttpService httpService) {
        return new UserServiceImpl(httpService);
    }

    // Only for testing purposes
    public UserServiceImpl(HttpService httpService, Session session) {
        this.httpService = httpService;
        this.session = session;
    }

    // Only for testing purposes
    public UserServiceImpl(HttpService httpService, Session session, ClientUserPojo user) {
        this.httpService = httpService;
        this.session = session;
        this.user = user;
    }

    @Override
    public boolean login(String username, String password) {
        this.bodyParams = new HashMap<>();
        this.bodyParams.put("username", username);
        this.bodyParams.put("password", password);
        Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.LOGIN_ENDPOINT, headers, this.bodyParams);
        ResponseBody responseBody = null;
        String userJSON;

        try {
            if (response != null) {
                responseBody = response.body();
                userJSON = responseBody.string();
                setUser(userJSON);
                LoggerService.log(Level.INFO, "AuthToken: " + this.user.getToken());
                connect(this.user.getUsername());
                return response.code() == HttpStatus.SC_OK;
            }
        } catch (IOException | IllegalStateException | JsonSyntaxException e) {
            LoggerService.log(Level.SEVERE, "Failed to login user: " + e.getMessage());
            return false;
        }
        return false;
    }

    @Override
    public boolean register(String name, String username, String password) {
        this.bodyParams = new HashMap<>();
        this.bodyParams.put("name", name);
        this.bodyParams.put("password", password);
        this.bodyParams.put("username", username);
        Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.REGISTER_ENDPOINT, headers, this.bodyParams);
        if (response == null) {
            return false;
        }
        return response.code() == HttpStatus.SC_OK;
    }

    @Override
    public boolean follow(String followee) {
        this.bodyParams = new HashMap<>();
        Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.FOLLOW_ENDPOINT.replace("{followeeId}", followee), headers, this.bodyParams);
        if (response == null) {
            return false;
        }
        return response.code() == HttpStatus.SC_OK;
    }

    @Override
    public boolean unfollow(String followee) {
        this.bodyParams = new HashMap<>();
        Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.UNFOLLOW_ENDPOINT.replace("{followeeId}", followee), headers, this.bodyParams);
        return response.code() == HttpStatus.SC_OK;
    }

    @Override
    public boolean sendMessage(String receiverId, Message message) {
        ServerCommand command = ServerCommand.getCommandBuilder()
                .setCommandType(ServerCommandType.SEND_MESSAGE)
                .setMessage(message)
                .build();

        try {
            if (null != session) {
                session.getBasicRemote().sendObject(command);
                return true;
            }
            return false;
        } catch (IOException | EncodeException e) {
            LoggerService.log(Level.SEVERE, e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public boolean sendFile(String receiverId, String filePath) {
        File file;
        byte[] bytes;

        // Convert file to bytes
        try {
            file = new File(filePath);
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            LoggerService.log(Level.SEVERE, "Failed to convert file to bytes, file path maybe invalid");
            return false;
        }

        try {
            // Convert bytes to base64 encoded string
            String encodedString = Base64.getEncoder().encodeToString(bytes);
            this.bodyParams = new HashMap<>();
            this.bodyParams.put("fileContents", encodedString);
            this.bodyParams.put("fileName", file.getName());
            this.bodyParams.put("senderId", this.user.getId());
            this.bodyParams.put("receiverId", receiverId);

            Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.CREATE_MEDIA_MESSAGE_ENDPOINT, headers, this.bodyParams);
            Gson gson = new Gson();
            ClientMessagePojo messagePojo = gson.fromJson(response.body().string(), ClientMessagePojo.class);

            Message message = Message.messageBuilder()
                    .setMessageContent(messagePojo.getContent())
                    .setReceiverId(new ObjectId(messagePojo.getReceiverId()))
                    .setSenderId(new ObjectId(messagePojo.getSenderId()))
                    .setMessageType(messagePojo.getMessageType())
                    .setId(new ObjectId(messagePojo.getId()))
                    .setEncryptionLevel(messagePojo.getEncryptionLevel())
                    .setExpiryDate(messagePojo.getExpiryDate())
                    .setTimestamp(messagePojo.getTimestamp())
                    .setTags(messagePojo.getTags())
                    .setDeleted(messagePojo.isDeleted())
                    .setHidden(messagePojo.isHidden())
                    .setForwarded(messagePojo.isForwarded())
                    .build();

            ServerCommand command = ServerCommand.getCommandBuilder()
                    .setMessage(message)
                    .setCommandType(ServerCommandType.SEND_MESSAGE)
                    .build();

            if (null != session) {
                session.getBasicRemote().sendObject(command);
                return true;
            }

            return false;
        } catch (Exception e) {
            LoggerService.log(Level.SEVERE, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteAccount() {
        Response response = httpService.sendDELETE(AppConstants.HOSTNAME + AppConstants.DELETE_ACCOUNT_ENDPOINT, headers);
        if (response == null) {
            return false;
        }
        return response.code() == HttpStatus.SC_OK;
    }

    @Override
    public ClientUserPojo getUser() {
        return this.user;
    }

    @Override
    public List<ClientUserPojo> searchUsers(String keyword) {
        this.headers = new HashMap<>();
        Response response = httpService.sendGET(AppConstants.HOSTNAME + AppConstants.SEARCH_USER_ENDPOINT.replace("{username}", keyword), headers);
        ResponseBody responseBody = null;
        String usersJSON;

        try {
            if (response != null && response.code() == HttpStatus.SC_OK) {
                responseBody = response.body();
                usersJSON = responseBody.string();
                return parseJSONToUsers(usersJSON);
            }
        } catch (IOException | IllegalStateException e) {
            LoggerService.log(Level.SEVERE, "Failed to search users: " + e.getMessage());
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getChats() {
        this.headers = new HashMap<>();
        Response response = httpService.sendGET(AppConstants.HOSTNAME + AppConstants.GET_CHATS_ENDPOINT, headers);
        ResponseBody responseBody = null;
        String chatsJSON;

        try {
            if (response != null && response.code() == HttpStatus.SC_OK) {
                responseBody = response.body();
                chatsJSON = responseBody.string();
                return parseJSONToString(chatsJSON);
            }
        } catch (IOException | IllegalStateException e) {
            LoggerService.log(Level.SEVERE, "Failed to get chats: " + e.getMessage());
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    public boolean connect(String username) {
        WebSocketContainer webSocketContainer;
        try {
            webSocketContainer = ContainerProvider.getWebSocketContainer();
            this.session = webSocketContainer
                    .connectToServer(MainClientEndpoint.class,
                            new URI(AppConstants.WS_CONNECT_ENDPOINT
                                    .replace("{username}", username)));
            return true;
        } catch (Exception ex) {
            LoggerService.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean isUserLoggedIn() {
        return this.user != null;
    }

    @Override
    public boolean setProfilePicture(String forUser, String filePath) {
        File file;
        byte[] bytes;

        // Convert file to bytes
        try {
            file = new File(filePath);
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            LoggerService.log(Level.SEVERE, "Failed to convert file to bytes, file path maybe invalid");
            return false;
        }

        try {
            // Convert bytes to base64 encoded string
            String encodedString = Base64.getEncoder().encodeToString(bytes);
            this.bodyParams = new HashMap<>();
            this.bodyParams.put("forUser", forUser);
            this.bodyParams.put("profilePictureContents", encodedString);
            this.bodyParams.put("fileName", file.getName());

            Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.SET_PROFILE_PICTURE_ENDPOINT, headers, this.bodyParams);
            return response.isSuccessful();
        } catch (Exception e) {
            LoggerService.log(Level.SEVERE, e.getMessage());
            return false;
        }
    }

    @Override
    public ClientUserPojo getProfile(String userId) {
        Response response = httpService.sendGET(AppConstants.HOSTNAME + AppConstants.GET_PROFILE_ENDPOINT.replace("{id}", userId), headers);
        ResponseBody responseBody = null;
        String usr;
        try {
            if (response != null && response.code() == HttpStatus.SC_OK) {
                responseBody = response.body();
                usr = responseBody.string();
                return parseJSONToUser(usr);
            }
        } catch (IOException | IllegalStateException e) {
            LoggerService.log(Level.SEVERE, "Failed to get profile: " + e.getMessage());
            return new ClientUserPojo();
        }
        return new ClientUserPojo();
    }

    /**
     * Set this user to currently logged in user.
     *
     * @param userJSON JSON-String representation of user data.
     */
    private void setUser(String userJSON) {
        Gson gson = new Gson();
        Type userType = new TypeToken<ClientUserPojo>() {
        }.getType();
        user = gson.fromJson(userJSON, userType);
        LoggerService.log(Level.INFO, user.getId() + " | " + user.isDeleted());
    }

    /**
     * Converts a list of users from JSON-String to {@code List<ClientUserPojo>}
     *
     * @param usersJSON list of users as JSON-String
     * @return list of users as {@code List<ClientUserPojo>}
     */
    private List<ClientUserPojo> parseJSONToUsers(String usersJSON) {
        Gson gson = new Gson();
        Type userType = new TypeToken<List<ClientUserPojo>>() {
        }.getType();
        return gson.fromJson(usersJSON, userType);
    }

    /**
     * Converts a user from JSON-String to {@code ClientUserPojo}
     *
     * @param user user data retrieved in form of JSON String
     * @return user model in form {@link ClientUserPojo}
     */
    private ClientUserPojo parseJSONToUser(String user) {
        Gson gson = new Gson();
        Type userType = new TypeToken<ClientUserPojo>() {
        }.getType();
        return gson.fromJson(user, userType);
    }

    /**
     * Converts a list of Strings from JSON-String to {@code List<String>}
     *
     * @param stringJSON list of String as JSON-String
     * @return list of string as {@code List<String>}
     */
    private List<String> parseJSONToString(String stringJSON) {
        LoggerService.log(Level.INFO, "parsing to JSON:" + stringJSON);
        Gson gson = new Gson();
        Type userType = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(stringJSON, userType);
    }
}
