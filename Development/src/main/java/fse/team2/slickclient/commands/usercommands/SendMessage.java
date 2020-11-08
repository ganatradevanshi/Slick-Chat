package fse.team2.slickclient.commands.usercommands;


import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;

/**
 * Invokes the method to send a text message to a user.
 */
public class SendMessage extends AbstractCommand {
    private final String receiverId;
    private final String message;

    /**
     * Instantiates the command with user parameters.
     *
     * @param receiverId userId of the user that will receive the message.
     * @param message    text to be sent to user as String.
     */
    public SendMessage(String receiverId, String message) {
        this.receiverId = receiverId;
        this.message = message;
    }

    @Override
    public void apply(ClientUser clientUser) {
        clientUser.sendMessage(this.receiverId, this.message);
    }
}
