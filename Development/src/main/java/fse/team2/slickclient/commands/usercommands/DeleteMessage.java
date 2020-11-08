package fse.team2.slickclient.commands.usercommands;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;

/**
 * Invokes the method to delete message with another user from the {@code User} class.
 */
public class DeleteMessage extends AbstractCommand {
  private final String messageId;

  /**
   * Instantiates the command with user parameters.
   *
   * @param messageId delete message with this id.
   */
  public DeleteMessage(String messageId) {
    this.messageId = messageId;
  }

  @Override
  public void apply(ClientUser clientUser) {
    clientUser.deleteMessage(this.messageId);
  }
}
