package fse.team2.slickclient.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * This class represents the logger class which logs all the actions taken by the user on the
 * applications views with formatted timestamp.
 */
public class LogFormatter extends SimpleFormatter {
  private static final String PATTERN = "MMM dd, YYYY HH:mm:ss.SSSXXX";

  @Override
  public synchronized String format(final LogRecord record) {
    return String.format(
            "%1$s %2$-7s %3$s%n",
            new SimpleDateFormat(PATTERN).format(
                    new Date(record.getMillis())),
            record.getLevel().getName(), formatMessage(record));
  }
}
