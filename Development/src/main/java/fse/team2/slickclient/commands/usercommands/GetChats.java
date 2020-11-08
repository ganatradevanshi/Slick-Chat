package fse.team2.slickclient.commands.usercommands;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;

public class GetChats extends AbstractCommand {

  @Override
  public void apply(ClientUser clientUser) {
    clientUser.getChats();
  }
}
