package fse.team2.slickclient.view;

import java.util.Map;
import java.util.logging.Level;

import fse.team2.slickclient.utils.LoggerService;

public class CommandViewImpl implements CommandView {
  private Map<String, Runnable> commandMap;

  @Override
  public void performAction(String action) {
    LoggerService.log(Level.INFO, action);
    Runnable handler = this.commandMap.getOrDefault(action, null);
    if (null == handler) {
      throw new IllegalArgumentException("Invalid command. Enter \"help\" to view list of possible commands.");
    }
    handler.run();
  }

  @Override
  public void setCommandMap(Map<String, Runnable> commandMap) {
    this.commandMap = commandMap;
  }
}
