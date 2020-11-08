package fse.team2.slickclient.model;

public interface ClientUser {
    /**
     * Login this user into the application with given credentials.
     *
     * @param username username of the user to be logged in.
     * @param password password of the user to be logged in.
     */
    void login(String username, String password);

    /**
     * Register user into the system with given details.
     *
     * @param name     name of user to be registered.
     * @param username unique username of user to be registered.
     * @param password password of the user to be registered.
     */
    void register(String name, String username, String password);

    /**
     * Follow another user with the given userId.
     *
     * @param userId userId of the user to be followed.
     */
    void follow(String userId);


    /**
     * Unfollow user with the given userId.
     *
     * @param userId userId of the user to be unfollowed.
     */
    void unfollow(String userId);

    /**
     * Send a text message of the user with the given id.
     *
     * @param receiverId userId of the receiver.
     * @param message    message to be sent as String.
     */
    void sendMessage(String receiverId, String message);

    /**
     * Deletes message with this id.
     *
     * @param messageId id of the message to be deleted.
     */
    void deleteMessage(String messageId);

    /**
     * Sends a file/document to the user with the given id.
     *
     * @param receiverId id of the receiving user.
     * @param url        url/filepath of the file/document to be sent.
     */
    void sendFile(String receiverId, String url);

    /**
     * Forwards message with the given id to the user with the given user id.
     *
     * @param receiverId id of the receiving user.
     * @param messageId  id of the message to be forwarded.
     */
    void forwardMessage(String receiverId, String messageId);

    /**
     * Mutes notifications from the user or group whose id is specified.
     *
     * @param userOrGroupId id of the user or group to be muted.
     */
    void mute(String userOrGroupId);

    /**
     * Deletes this user's version of all messages sent to the user or group with the given id.
     *
     * @param chatId id of the user or group to delete chat.
     */
    void deleteChat(String chatId);

    /**
     * Prints a list of commands supported by the cli client.
     */
    void help();

    /**
     * Prints a list of users matching the keyword exactly or partially
     */
    void searchUsers(String keyword);

    /**
     * Prints a list of users/groups whom this user has sent/received at-least 1 message.
     */
    void getChats();

    /**
     * Prints all messages from a chat session between this user and the user with the given id.
     *
     * @param userOrGroupId receiverId to fetch chat messages for.
     */
    void getChatMessages(String userOrGroupId);

    /**
     * Set's profile picture for mentioned user for the logged in user.
     *
     * @param forUser  - user for which the profile picture is to be set
     *                 ('default' value means, the profile picture to be set is a default one.)
     * @param filePath - path of the profile picture.
     */
    void setProfilePicture(String forUser, String filePath);

    /**
     * Retrieve profile of the user mentioned
     *
     * @param userId - id of the user whose profile is to be fetched.
     */
    void getProfile(String userId);
}
