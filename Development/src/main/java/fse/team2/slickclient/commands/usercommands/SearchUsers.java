package fse.team2.slickclient.commands.usercommands;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;

public class SearchUsers extends AbstractCommand {
  private final String keyword;

  public SearchUsers(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public void apply(ClientUser clientUser) {
    clientUser.searchUsers(this.keyword);
  }
}
