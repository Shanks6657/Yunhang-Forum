package com.yunhang.forum.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * 轻量日志工具（避免引入外部依赖）。
 * 统一输出格式：时间 | level | 线程 | logger | message | throwable
 */
public final class LogUtil {
  private static final Logger LOG = Logger.getLogger("YunhangForum");
  private static volatile boolean initialized;

  private LogUtil() {}

  private static void ensureInitialized() {
    if (initialized) {
      return;
    }
    synchronized (LogUtil.class) {
      if (initialized) {
        return;
      }

      LOG.setUseParentHandlers(false);
      LOG.setLevel(Level.INFO);

      ConsoleHandler handler = new ConsoleHandler();
      handler.setLevel(Level.ALL);
      handler.setFormatter(new Formatter() {
        private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
          String time = LocalDateTime.now().format(fmt);
          String thread = Thread.currentThread().getName();
          String logger = record.getLoggerName();
          String level = record.getLevel().getName();
          String msg = formatMessage(record);

          StringBuilder sb = new StringBuilder();
          sb.append(time)
              .append(" | ")
              .append(level)
              .append(" | ")
              .append(thread)
              .append(" | ")
              .append(logger)
              .append(" | ")
              .append(msg);

          if (record.getThrown() != null) {
            sb.append(" | ").append(record.getThrown());
          }
          sb.append(System.lineSeparator());
          return sb.toString();
        }
      });

      LOG.addHandler(handler);
      initialized = true;
    }
  }

  public static void info(String message) {
    ensureInitialized();
    LOG.info(message);
  }

  public static void warn(String message) {
    ensureInitialized();
    LOG.warning(message);
  }

  public static void error(String message, Throwable t) {
    ensureInitialized();
    LOG.log(Level.SEVERE, message, t);
  }
}
