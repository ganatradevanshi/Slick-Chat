package fse.team2.slickclient.services;

import java.util.List;

import fse.team2.common.models.mongomodels.Message;
import fse.team2.slickclient.model.ClientUserPojo;

public interface UserService {
    /**
     * Executes login on the server via the HttpService.
     *
     * @param username username of the user logging in.
     * @param password password of the user logging in.
     * @return true if login succeeds, false otherwise.
     */
    boolean login(String username, String password);

    /**
     * Executes signup on the server via the HttpService.
     *
     * @param name     name of the user signing up.
     * @param username username of the user signing up.
     * @param password password of the user signing up.
     * @return true if signup succeeds, false otherwise.
     */
    boolean register(String name, String username, String password);

    /**
     * Executes followUser on the server via the HttpService.
     *
     * @param followee if of the user to be followed.
     * @return true if follow succeeds, false otherwise.
     */
    boolean follow(String followee);

    /**
     * Executes unfollowUser on the server via the HttpService.
     *
     * @param followee if of the user to be followed.
     * @return true if unfollow succeeds, false otherwise.
     */
    boolean unfollow(String followee);

    /**
     * Connects to server websocket endpoint on the server.
     *
     * @param username username of this user trying to make a websocket connection.
     * @return true if connect succeeds, false otherwise.
     */
    boolean connect(String username);

    /**
     * Executes send message on the server via the web sockets.
     *
     * @param receiverId username of this user trying to make a websocket connection.
     * @param message    message object consisting of text and metadata to be sent to the server.
     * @return true if message is sent successfully, false otherwise.
     */
    boolean sendMessage(String receiverId, Message message);

    /**
     * Send the file specified by the user over to receiving user.
     *
     * @param receiverId - user id of the receiver.
     * @param filePath   - path of the file to be sent.
     * @return - true if the file was sent successfully, false otherwise.
     */
    boolean sendFile(String receiverId, String filePath);

    /**
     * Deletes account for the currently logged in user via the HttpService.
     *
     * @return true if delete account succeeds, false otherwise.
     */
    boolean deleteAccount();

    /**
     * Gets currently logged in user as {@code ClientUserPojo}.
     *
     * @return user data as {@code ClientUserPojo} if user is logged in; null otherwise.
     */
    ClientUserPojo getUser();

    /**
     * Get a list of users matching the keyword exactly or partially.
     *
     * @param keyword username or part of the username to search for.
     * @return List of matched users as {@code List<ClientUserPojo>}
     */
    List<ClientUserPojo> searchUsers(String keyword);

    /**
     * Get a list of users that have interacted with the currently logged in user at-least once.
     *
     * @return List of matched users as {@code List<String>}
     */
    List<String> getChats();

    /**
     * Check if user is currently logged in.
     *
     * @return true if user is logged in; false otherwise.
     */
    boolean isUserLoggedIn();

    /**
     * Handle setting profile picture for the logged in user for the mentioned user.
     *
     * @param forUser  - user for which the profile picture is to be set
     *                 (can take 'default' value which means that we are
     *                 setting default profile picture for the logged in user)
     * @param filePath - path of the profile picture file.
     * @return - true if the operation is success, false otherwise.
     */
    boolean setProfilePicture(String forUser, String filePath);

    /**
     * Retrieve profile of the user mentioned
     *
     * @param userId - id of the user whose profile is to be fetched.
     */
    ClientUserPojo getProfile(String userId);
}
