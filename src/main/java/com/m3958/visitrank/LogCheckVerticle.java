package com.m3958.visitrank;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.LogProcessorWorkVerticle.LogProcessorWorkCfgKey;
import com.m3958.visitrank.logger.AppLogger;

/**
 * We run one instance of this verticle,so don't worry about concurrency problem.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class LogCheckVerticle extends Verticle {

  @Override
  public void start() {
    final Logger log = container.logger();
    JsonObject jo = container.config();
    final String logDir = jo.getString(LogProcessorWorkCfgKey.LOG_DIR, "logs");
    final String archiveDir = jo.getString(LogProcessorWorkCfgKey.ARCHIVE_DIR, "archives");

    vertx.setPeriodic(30000, new Handler<Long>() {
      public void handle(Long timerID) {
        // logger file check.
        final String logfilename = new RemainLogFileFinder(logDir).findOne();
        if (logfilename != null) {
          JsonObject lpConfig =
              new JsonObject().putString(LogProcessorWorkCfgKey.ADDRESS, logfilename)
                  .putString(LogProcessorWorkCfgKey.FILE_NAME, logfilename)
                  .putString(LogProcessorWorkCfgKey.ARCHIVE_DIR, archiveDir);
          container.deployVerticle("com.m3958.visitrank.LogProcessorWorkVerticle", lpConfig, 1,
              new Handler<AsyncResult<String>>() {
                @Override
                public void handle(AsyncResult<String> ar) {
                  if (ar.succeeded()) {
                    vertx.eventBus().send(logfilename, ar.result());
                  } else {
                    AppLogger.deployError.info("logprocessor deploy:" + logfilename + " failure");
                  }
                }
              });
        }
      }
    });

    log.info("First this is printed");
  }

  public static class RemainLogFileFinder {
    private String logDirStr;

    private static Pattern fptn = Pattern.compile(".*\\d{4}-\\d{2}-\\d{2}.*\\.log");

    public RemainLogFileFinder(String logDirStr) {
      this.logDirStr = logDirStr;
    }

    public String findOne() {
      File logDir = new File(logDirStr);
      String[] files = logDir.list();
      for (String f : files) {
        Matcher m = fptn.matcher(f);
        if (f.endsWith("log") && m.matches()) { // find log file.
          if (AppUtils.canLockLog(f)) {
            return f;
          } else {
            return null;
          }
        }
      }
      return null;
    }
  }

}
