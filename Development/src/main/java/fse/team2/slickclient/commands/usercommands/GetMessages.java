package fse.team2.slickclient.commands.usercommands;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;

public class GetMessages extends AbstractCommand {
  private final String userOrGroupId;

  public GetMessages(String userOrGroupId) {
    this.userOrGroupId  = userOrGroupId;
  }

  @Override
  public void apply(ClientUser clientUser) {
    clientUser.getChatMessages(this.userOrGroupId);
  }
}
