package fse.team2.slickclient.commands.usercommands;

import fse.team2.slickclient.model.ClientUser;

/**
 * This class represents an interface to implement command design pattern where the method calls all
 * the different member functions of the {@code ClientUser} model by dynamic dispatch.
 */
public interface UserCommand {

  /**
   * Applies relevant operation to the UserModel instance.
   *
   * @param clientUser model of the User that is to be manipulated.
   */
  void apply(ClientUser clientUser);
}
