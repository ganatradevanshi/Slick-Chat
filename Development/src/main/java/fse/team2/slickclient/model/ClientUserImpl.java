package fse.team2.slickclient.model;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.enums.MessageType;
import fse.team2.slickclient.services.MessageService;
import fse.team2.slickclient.services.UserService;
import fse.team2.slickclient.utils.LoggerService;

public class ClientUserImpl extends ClientUserPojo implements ClientUser {
    private StringBuilder log;
    private UserService userService;
    private MessageService messageService;

    public ClientUserImpl() {
        // This public constructor is needed by the gson to map documents to user POJO.
    }

    public ClientUserImpl(StringBuilder log, UserService userService, MessageService messageService) {
        this.log = log;
        this.userService = userService;
        this.messageService = messageService;
    }

    @Override
    public void login(String username, String password) {
        boolean loginStatus = this.userService.login(username, password);
        LoggerService.log(Level.INFO, "Login status: " + loginStatus);
        log.append(loginStatus ? "Login Successful\n" : "Login Failed\n");
        if (this.userService.getUser() != null) {
            boolean connectStatus = false;
            if (connect(this.userService.getUser().getUsername())) {
                connectStatus = true;
                LoggerService.log(Level.INFO, "Connect status: " + true);
            }
            log.append(connectStatus ? "Connect Successful\n" : "Connect Failed\n");
        }
    }

    @Override
    public void register(String name, String username, String password) {
        boolean registerStatus = this.userService.register(name, username, password);
        LoggerService.log(Level.INFO, "Register status: " + registerStatus);
        log.append(registerStatus ? "Registration Successful\n" : "Registration Failed\n");
    }


    @Override
    public void follow(String userId) {
        boolean followStatus = false;
        if (this.userService.isUserLoggedIn()) {
            followStatus = this.userService.follow(userId);
            LoggerService.log(Level.INFO, "Follow status: " + followStatus);
        } else {
            LoggerService.log(Level.WARNING, "You must login to follow other users!");
        }
        log.append(followStatus ? "Follow Successful\n" : "Follow Failed\n");
    }

    @Override
    public void unfollow(String userId) {
        boolean unfollowStatus = false;
        if (this.userService.isUserLoggedIn()) {
            unfollowStatus = this.userService.unfollow(userId);
            LoggerService.log(Level.INFO, "Unfollow status: " + unfollowStatus);
        } else {
            LoggerService.log(Level.WARNING, "You must login to unfollow users!");
        }
        log.append(unfollowStatus ? "Unfollow Successful\n" : "Unfollow Failed\n");
    }

    @Override
    public void sendMessage(String receiverId, String text) {
        boolean sendMessageStatus = false;
        if (this.userService.isUserLoggedIn()) {
            Message message = Message.messageBuilder()
                    .setSenderId(new ObjectId(this.userService.getUser().getId()))
                    .setReceiverId(new ObjectId(receiverId))
                    .setTimestamp(new Date())
                    .setEncryptionLevel(EncrpytionLevel.BASIC)
                    .setMessageType(MessageType.TEXT)
                    .setMessageContent(text)
                    .build();
            sendMessageStatus = this.userService.sendMessage(receiverId, message);
            LoggerService.log(Level.INFO, "Send message status: " + sendMessageStatus);
        } else {
            LoggerService.log(Level.WARNING, "You must login to send messages!");
        }
        log.append(sendMessageStatus ? "Send Message Successful\n" : "Send Message Failed\n");
    }

    @Override
    public void deleteMessage(String messageId) {
        boolean deleteStatus = false;
        if (this.userService.isUserLoggedIn()) {
            deleteStatus = this.messageService.deleteMessage(this.userService.getUser().getId(), messageId);
            LoggerService.log(Level.INFO, "Delete Message status: " + deleteStatus);
        } else {
            LoggerService.log(Level.WARNING, "You must login to delete messages!");
        }
        log.append(deleteStatus ? "Delete Message Successful\n" : "Delete Message Failed\n");
    }

    @Override
    public void sendFile(String receiverId, String filePath) {
        boolean sendFileStatus = false;
        if (this.userService.isUserLoggedIn()) {
            sendFileStatus = this.userService.sendFile(receiverId, filePath);
            LoggerService.log(Level.INFO, "Send file status: " + sendFileStatus);
        } else {
            LoggerService.log(Level.WARNING, "You must login to send files!");
        }
        log.append(sendFileStatus ? "Send File Successful\n" : "Send File Failed\n");
    }

    @Override
    public void forwardMessage(String receiverId, String messageId) {
        log.append("Forward Message Successful\n");
    }

    @Override
    public void mute(String userOrGroupId) {
        log.append("Mute Successful\n");
    }

    @Override
    public void deleteChat(String chatId) {
        log.append("Delete Chat Successful\n");
    }

    @Override
    public void help() {
        String helpText = "Commands:  \n" +
                "/connect <userOrGroupId> - Start chat with this user/group \n" +
                "/login <username> <passowrd> - login user with username and password \n" +
                "/register <name> <username> <passowrd> - register user with given name, username and password \n" +
                "/follow <userId> - follow user with this userId \n" +
                "/unfollow <userId> - unfollow user with this userId \n" +
                "/getChats - get a list of followers this user interacted with atleast once \n" +
                "/getMessages <userId> - get a list of messages exchanged between users \n" +
                "/sendMessage <userOrGroupId> <message> - Send a text message to this user/group \n" +
                "/sendFile <userOrGroupId> <fileURL> - Send a media file to this user/group \n" +
                "/forwardMessage <receiverId> <messageId> \n" +
                "/deleteMessage <messageId> - Deletes this message \n" +
                "/deleteChat <userOrGroupId> - Delete entire conversation made with this user/group \n" +
                "/mute <userOrGroupId> - Mute notifications for messages from this user/group \n" +
                "/setProfilePicture <default or forUser (userId)> <fileURL> \n" +
                "/getProfile <userId>";

        LoggerService.log(Level.INFO, helpText);

        log.append(helpText);
    }

    @Override
    public void searchUsers(String keyword) {
        List<ClientUserPojo> usersList = this.userService.searchUsers(keyword);
        LoggerService.log(Level.INFO, "Users Found: " + usersList);
        log.append(usersList.isEmpty() ? "No Users Found\n" : "Users Found\n");
    }

    @Override
    public void getChats() {
        if (this.userService.isUserLoggedIn()) {
            List<String> chatsList = this.userService.getChats();
            LoggerService.log(Level.INFO, "Chats Found: " + chatsList);
            log.append(chatsList.isEmpty() ? "No Chats Found\n" : "Chats Found\n");
        } else {
            log.append("You must login to get chats.");
            LoggerService.log(Level.WARNING, "You must login to get chats.");
        }
    }

    @Override
    public void getChatMessages(String userOrGroupId) {
        if (this.userService.isUserLoggedIn()) {
            List<ClientMessagePojo> messagesList = this.messageService.getMessages(this.userService.getUser().getId(), userOrGroupId);
            LoggerService.log(Level.INFO, "Messages Found: " + messagesList);
            log.append("Messages Found:");
            log.append(messagesList);
        } else {
            log.append("You must login to fetch messages!");
            LoggerService.log(Level.WARNING, "You must login to fetch messages!");
        }
    }

    @Override
    public void setProfilePicture(String forUser, String filePath) {
        boolean setProfilePictureStatus = false;
        if (this.userService.isUserLoggedIn()) {
            setProfilePictureStatus = this.userService.setProfilePicture(forUser, filePath);
            LoggerService.log(Level.INFO, "Set Profile Picture: " + setProfilePictureStatus);
        } else {
            LoggerService.log(Level.WARNING, "You must login to set profile picture");
        }
        log.append(setProfilePictureStatus ? "Set Profile Picture Successful\n" : "Set Profile Picture Failed\n");
    }

    @Override
    public void getProfile(String userId) {
        if (this.userService.isUserLoggedIn()) {
            ClientUserPojo user = this.userService.getProfile(userId);
            LoggerService.log(Level.INFO, "User's Profile: " + user);
            log.append("User's Profile: " + user);
        } else {
            log.append("You must login to see profile");
            LoggerService.log(Level.WARNING, "You must login to see profile");
        }
    }


    /**
     * Initiates a chat session with the user or group with this id.
     *
     * @param userOrGroupId id of the user or the group to initiate a chat with.
     */
    private boolean connect(String userOrGroupId) {
        return this.userService.connect(userOrGroupId);
    }
}
