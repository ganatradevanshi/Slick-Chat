package fse.team2.slickclient.commands.usercommands;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;

/**
 * Invokes the method to login a user.
 */
public class Login extends AbstractCommand {
  private final String username;
  private final String password;

  /**
   * Instantiates the command with user parameters.
   *
   * @param username username of the user to be logged in.
   * @param password password of the user to be logged in.
   */
  public Login(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public void apply(ClientUser clientUser) {
    clientUser.login(this.username, this.password);
  }
}
