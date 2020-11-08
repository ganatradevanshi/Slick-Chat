package fse.team2.slickclient.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.commands.usercommands.*;
import fse.team2.slickclient.model.ClientUser;
import fse.team2.slickclient.utils.LoggerService;
import fse.team2.slickclient.view.CommandView;

/**
 * Represents a controller in the MVC design pattern. It facilitates communication between
 * the User model and the view.
 */
public class CommandControllerImpl extends AbstractCommand {
    private final Map<String, Runnable> commandMap;
    private final Scanner scanner;
    private final CommandView commandView;

    /**
     * Instantiates the controller with user parameters.
     *
     * @param in          Readable instance of an input (Can be console, file or network based).
     * @param commandView instance of the view to be bound with the model.
     */
    public CommandControllerImpl(Readable in, CommandView commandView) {
        this.commandMap = new HashMap<>();
        this.commandView = commandView;
        this.scanner = new Scanner(in);
    }

    @Override
    public void apply(ClientUser clientUser) {
        configureCommandListener(clientUser);
        run();
    }

    /**
     * Builds a hashmap with commands as strings and their respective handlers as values to the map.
     *
     * @param clientUser instance of the {@code User} that is currently logged in.
     */
    private void configureCommandListener(ClientUser clientUser) {
        this.commandMap.put("/login", loginHandler(clientUser));
        this.commandMap.put("/register", registerHandler(clientUser));
        this.commandMap.put("/searchUsers", searchUsersHandler(clientUser));
        this.commandMap.put("/getChats", getChatsHandler(clientUser));
        this.commandMap.put("/getMessages", getMessagesHandler(clientUser));
        this.commandMap.put("/follow", followHandler(clientUser));
        this.commandMap.put("/unfollow", unfollowHandler(clientUser));
        this.commandMap.put("/sendMessage", sendMessageHandler(clientUser));
        this.commandMap.put("/forwardMessage", forwardMessageHandler(clientUser));
        this.commandMap.put("/sendFile", sendFileHandler(clientUser));
        this.commandMap.put("/mute", muteHandler(clientUser));
        this.commandMap.put("/deleteChat", deleteChatHandler(clientUser));
        this.commandMap.put("/deleteMessage", deleteMessageHandler(clientUser));
        this.commandMap.put("/setProfilePicture", setProfilePictureHandler(clientUser));
        this.commandMap.put("/getProfile", getProfileHandler(clientUser));
        this.commandMap.put("/help", helpHandler(clientUser));

        this.commandView.setCommandMap(this.commandMap);
    }

    /**
     * Handles register command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable registerHandler(ClientUser clientUser) {
        return () -> new Register(this.scanner.next(), this.scanner.next(), this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles login command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable loginHandler(ClientUser clientUser) {
        return () -> new Login(this.scanner.next(), this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles search users command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable searchUsersHandler(ClientUser clientUser) {
        return () -> new SearchUsers(this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles sendMessage command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable sendMessageHandler(ClientUser clientUser) {
        return () -> new SendMessage(this.scanner.next(), this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles sendFile command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable sendFileHandler(ClientUser clientUser) {
        return () -> new SendFile(this.scanner.next(), this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles setProfilePicture command within the command map
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable setProfilePictureHandler(ClientUser clientUser) {
        return () -> new SetProfilePicture(this.scanner.next(), this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles getProfile command within the command map
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable getProfileHandler(ClientUser clientUser) {
        return () -> new GetProfile(this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles forwardMessage command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable forwardMessageHandler(ClientUser clientUser) {
        return () -> new ForwardMessage(this.scanner.next(), this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles follow command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable followHandler(ClientUser clientUser) {
        return () -> new Follow(this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles unfollow command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable unfollowHandler(ClientUser clientUser) {
        return () -> new Unfollow(this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles get chats command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable getChatsHandler(ClientUser clientUser) {
        return () -> new GetChats().apply(clientUser);
    }

    /**
     * Handles get messages command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable getMessagesHandler(ClientUser clientUser) {
        return () -> new GetMessages(this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles deleteMessage command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable deleteMessageHandler(ClientUser clientUser) {
        return () -> new DeleteMessage(this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles deleteChat command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable deleteChatHandler(ClientUser clientUser) {
        return () -> new DeleteChat(this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles mute command within the command map.
     *
     * @param clientUser user model used to login user.
     * @return reference to lambda function as runnable.
     */
    private Runnable muteHandler(ClientUser clientUser) {
        return () -> new Mute(this.scanner.next()).apply(clientUser);
    }

    /**
     * Handles help command within the command map.
     *
     * @return reference to lambda function as runnable.
     */
    private Runnable helpHandler(ClientUser clientUser) {
        return () ->
                new Help().apply(clientUser);
    }

    /**
     * Initiates a continuous event loop to read commands from the CLI and execute handlers based on
     * matched commands. The event loop exits if the "exit" command is encountered.
     */
    private void run() {
        while (true) {
            LoggerService.log(Level.INFO, "Enter a command or enter \"help\" for instructions: ");
            String command = this.scanner.next();
            if (command.equalsIgnoreCase("exit")) {
                break;
            }
            this.commandView.performAction(command);
        }
    }
}
