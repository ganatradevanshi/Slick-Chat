package fse.team2.slickclient.commands.usercommands;

import java.util.logging.Level;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;
import fse.team2.slickclient.utils.LoggerService;

/**
 * Invokes the method to follow a user from the {@code User} class.
 */
public class Follow extends AbstractCommand {
  private final String userId;

  /**
   * Instantiates the command with user parameters.
   *
   * @param userId userId of the user to follow.
   */
  public Follow(String userId) {
    this.userId = userId;
  }

  @Override
  public void apply(ClientUser clientUser) {
    LoggerService.log(Level.INFO, "Follow user called: " + this.userId);
    clientUser.follow(this.userId);
  }
}
