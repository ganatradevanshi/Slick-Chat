package fse.team2.slickclient.view;

import java.util.Map;

public interface CommandView {
  void performAction(String action);

  void setCommandMap(Map<String, Runnable> commandMap);
}
