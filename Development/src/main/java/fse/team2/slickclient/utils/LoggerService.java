package fse.team2.slickclient.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a logger class which calls its handler everytime it is called and logs the
 * exception as well as the activities of the user and logs appropriate errors and warnings.
 */
public class LoggerService {
  private static final Logger LOGGER
          = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  private LoggerService() {

  }

  static {
    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(new LogFormatter());
    LOGGER.addHandler(consoleHandler);
    LOGGER.setUseParentHandlers(false);
  }

  /**
   * Logs a message in the logger with specified log level.
   *
   * @param level   log level to log message at.
   * @param message message to be logged.
   */
  public static void log(Level level, String message) {
    LOGGER.log(level, message);
  }
}
