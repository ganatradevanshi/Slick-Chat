package fse.team2.slickclient.commands.usercommands;

import java.util.logging.Level;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.model.ClientUser;
import fse.team2.slickclient.utils.LoggerService;

/**
 * Invokes the method to send a media file to a user or group.
 */
public class SendFile extends AbstractCommand {
  private final String receiverId;
  private final String url;

  /**
   * Instantiates the command with user parameters.
   *
   * @param receiverId Id of user or group that will receive the file.
   * @param url        url of the media file to be sent.
   */
  public SendFile(String receiverId, String url) {
    this.receiverId = receiverId;
    this.url = url;
  }

  @Override
  public void apply(ClientUser clientUser) {
    LoggerService.log(Level.INFO, "Send File called: " + this.receiverId + ", " + this.url);
    clientUser.sendFile(this.receiverId, this.url);
  }
}
