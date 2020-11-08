package fse.team2.slickclient.commands.usercommands;

import java.util.logging.Level;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;
import fse.team2.slickclient.utils.LoggerService;

/**
 * Invokes the method to forward a message to a user.
 */
public class ForwardMessage extends AbstractCommand {
  private final String receiverId;
  private final String messageId;

  /**
   * Instantiates the command with user parameters.
   *
   * @param receiverId userId of the user that will receive the message.
   * @param messageId  Id of the message to be forwarded.
   */
  public ForwardMessage(String receiverId, String messageId) {
    this.receiverId = receiverId;
    this.messageId = messageId;
  }

  @Override
  public void apply(ClientUser clientUser) {
    LoggerService.log(Level.INFO, "Forward message called: " + this.receiverId + ", " + this.messageId);
    clientUser.forwardMessage(this.receiverId, this.messageId);
  }
}
