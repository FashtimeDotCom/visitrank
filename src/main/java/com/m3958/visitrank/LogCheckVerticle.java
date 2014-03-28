package com.m3958.visitrank;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.LogProcessorWorkVerticle.LogProcessorWorkCfgKey;

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

    vertx.setPeriodic(451111, new Handler<Long>() {
      public void handle(Long timerID) {
        // logger file check.
        log.info("daily copy instance remains: " + AppUtils.dailyProcessorRemainsGetSet(0));
        if (AppUtils.dailyProcessorRemainsGetSet(0) > 0) {
          AppUtils.dailyProcessorRemainsGetSet(1);
          vertx.eventBus().send(DailyCopyWorkVerticle.VERTICLE_ADDRESS, "start",new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> msg) {
              AppUtils.dailyProcessorRemainsGetSet(-1);
            }});
        }
      }
    });

    vertx.setPeriodic(300000, new Handler<Long>() {
      public void handle(Long timerID) {
        // logger file check.
        log.info("log processor instance remains: " + AppUtils.logProcessorRemainsGetSet(0));
        if (AppUtils.logProcessorRemainsGetSet(0) > 0) {
          final String logfilename = new RemainLogFileFinder(logDir).findOne();
          if (logfilename != null) {
            JsonObject body =
                new JsonObject().putString(LogProcessorWorkCfgKey.LOG_DIR, logDir)
                    .putString(LogProcessorWorkCfgKey.FILE_NAME, logfilename)
                    .putString(LogProcessorWorkCfgKey.ARCHIVE_DIR, archiveDir);
            AppUtils.logProcessorRemainsGetSet(1);
            vertx.eventBus().send(LogProcessorWorkVerticle.VERTICLE_ADDRESS, body,new Handler<Message<String>>() {
              @Override
              public void handle(Message<String> msg) {
                AppUtils.logProcessorRemainsGetSet(-1);
              }});
          }
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
