package com.m3958.visitrank;

import java.io.File;
import java.io.IOException;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

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
    final String rdir = jo.getString("logdir", "logs");

    vertx.setPeriodic(30000, new Handler<Long>() {
      public void handle(Long timerID) {
        final String logfilename = new RemainLogFileFinder(rdir).findOne();
        if (logfilename != null) {
          JsonObject lpConfig =
              new JsonObject().putString("address", logfilename).putString("filename", logfilename);
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

    public RemainLogFileFinder(String logDirStr) {
      this.logDirStr = logDirStr;
    }

    public String findOne() {
      File logDir = new File(logDirStr);
      String[] files = logDir.list();
      for (String f : files) {
        if (f.endsWith("log") && !f.endsWith("app.log")) { // find log file.
          File doingfile = new File(logDir, f + ".doing");
          if (!doingfile.exists()) {
            try {
              doingfile.createNewFile();
              return f;
            } catch (IOException e) {
              AppLogger.error.error("can't create :" + doingfile);
              return null;
            }
          }
        }
      }
      return null;
    }
  }

}
