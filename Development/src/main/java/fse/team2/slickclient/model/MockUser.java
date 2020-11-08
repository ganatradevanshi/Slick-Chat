package fse.team2.slickclient.model;

import java.util.logging.Level;

import fse.team2.slickclient.utils.LoggerService;

public class MockUser implements ClientUser {
    private final StringBuilder log;

    public MockUser(StringBuilder log) {
        this.log = log;
    }

    @Override
    public void login(String username, String password) {
        log.append("Login called: ").append(username).append(", ").append(password).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void register(String name, String username, String password) {
        log.append("Register called: ").append(name).append(", ").append(username).append(", ").append(password).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void follow(String userId) {
        log.append("Follow user called: ").append(userId).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void unfollow(String userId) {
        log.append("Unfollow user called: ").append(userId).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void sendMessage(String receiverId, String message) {
        log.append("Send Message called: ").append(receiverId).append(", ").append(message).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void deleteMessage(String messageId) {
        log.append("Delete Message called: ").append(messageId).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void sendFile(String receiverId, String url) {
        log.append("Send file called: ").append(receiverId).append(", ").append(url).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void forwardMessage(String receiverId, String messageId) {
        log.append("Forward message called: ").append(receiverId).append(", ").append(messageId).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void mute(String userOrGroupId) {
        log.append("Mute user called: ").append(userOrGroupId).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void deleteChat(String chatId) {
        log.append("Delete chat called: ").append(chatId).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void help() {
        log.append("Help called\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void searchUsers(String keyword) {
        log.append("Search Users called").append(", ").append(keyword).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void getChats() {
        log.append("Get chats called").append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void getChatMessages(String userOrGroupId) {
        log.append("Get messages called").append(", ").append(userOrGroupId).append("\n");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void setProfilePicture(String forUser, String filePath) {
        log.append("Set profile picture called");
        LoggerService.log(Level.INFO, log.toString());
    }

    @Override
    public void getProfile(String userId) {
        log.append("Get Profile called");
        LoggerService.log(Level.INFO, log.toString());
    }
}
