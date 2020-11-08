package fse.team2.slickclient.commands.usercommands;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;

public class Register extends AbstractCommand {
  private final String name;
  private final String username;
  private final String password;

  /**
   * Instantiates the command with user parameters.
   *
   * @param name     name of user being registered.
   * @param username username of the user to be registered.
   * @param password password of the user to be registered.
   */
  public Register(String name, String username, String password) {
    this.username = username;
    this.password = password;
    this.name = name;
  }

  @Override
  public void apply(ClientUser clientUser) {
    clientUser.register(this.name, this.username, this.password);
  }
}
