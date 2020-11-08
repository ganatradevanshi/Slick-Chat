package fse.team2.slickclient.commands.usercommands;

import java.util.logging.Level;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;
import fse.team2.slickclient.utils.LoggerService;

/**
 * Invokes the method to mute a user or group.
 */
public class Mute extends AbstractCommand {
  private final String userOrGroupId;

  /**
   * Instantiates the command with user parameters.
   *
   * @param userOrGroupId Id of user or group to be muted.
   */
  public Mute(String userOrGroupId) {
    this.userOrGroupId = userOrGroupId;
  }

  @Override
  public void apply(ClientUser clientUser) {
    LoggerService.log(Level.INFO, "Mute called: " + this.userOrGroupId);
    clientUser.mute(this.userOrGroupId);
  }
}
