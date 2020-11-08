package fse.team2.slickclient.commands.usercommands;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;

public class GetProfile extends AbstractCommand {
    private final String userId;

    public GetProfile(String userId) {
        this.userId = userId;
    }

    @Override
    public void apply(ClientUser clientUser) {
        clientUser.getProfile(this.userId);
    }
}
