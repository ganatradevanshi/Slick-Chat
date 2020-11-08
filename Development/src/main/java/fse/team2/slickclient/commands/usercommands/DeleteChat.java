package fse.team2.slickclient.commands.usercommands;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;

/**
 * Invokes the method to delete chat with another user from the {@code User} class.
 */
public class DeleteChat extends AbstractCommand {
  private final String userOrGroupId;

  /**
   * Instantiates the command with user parameters.
   *
   * @param userOrGroupId delete chat with this user to user/groupId.
   */
  public DeleteChat(String userOrGroupId) {
    this.userOrGroupId = userOrGroupId;
  }


  @Override
  public void apply(ClientUser clientUser) {
    clientUser.deleteChat(this.userOrGroupId);
  }
}
