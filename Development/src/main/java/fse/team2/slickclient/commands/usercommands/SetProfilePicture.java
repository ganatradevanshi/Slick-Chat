package fse.team2.slickclient.commands.usercommands;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;
import fse.team2.slickclient.utils.LoggerService;

import java.util.logging.Level;

/**
 * Invokes the method to set profile picture for the user
 */
public class SetProfilePicture extends AbstractCommand {

    private final String forUser;
    private final String filePath;

    /**
     * Instantiates the command with user parameters.
     *
     * @param forUser  Id of the user for which to set the profile picture (for some user or default profile picture)
     * @param filePath url of the media file to be sent.
     */
    public SetProfilePicture(String forUser, String filePath) {
        this.forUser = forUser;
        this.filePath = filePath;
    }

    @Override
    public void apply(ClientUser clientUser) {
        LoggerService.log(Level.INFO, "Set Profile Picture called: " + this.forUser + ", " + this.filePath);
        clientUser.setProfilePicture(this.forUser, this.filePath);
    }
}
