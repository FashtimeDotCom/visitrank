package com.m3958.visitrank;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.LogProcessorWorkVerticle.LogProcessorWorkMsgKey;
import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.Locker;
import com.m3958.visitrank.Utils.RemainLogFileFinder;
import com.m3958.visitrank.Utils.RemainsCounter;
import com.m3958.visitrank.logger.AppLogger;

/**
 * We run one instance of this verticle,so don't worry about concurrency problem.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class LogCheckVerticle extends Verticle {

  private Locker locker = new Locker();

  public static String VERTICLE_NAME = LogCheckVerticle.class.getName();

  @Override
  public void start() {
    final Logger log = container.logger();
    vertx.eventBus().send(AppConfigVerticle.VERTICLE_ADDRESS, new JsonObject(),
        new Handler<Message<JsonObject>>() {
          @Override
          public void handle(Message<JsonObject> msg) {
            final AppConfig gcfg = new AppConfig(msg.body());
            deployMe(gcfg, log);
          }
        });
  }

  private void deployMe(final AppConfig appConfig,Logger log) {

    log.info("logProcessorInstance: " + 1);

    log.info("logfilereadgap: " + appConfig.getLogFileReadGap());

    log.info(appConfig.getWriteConcern());

    final RemainsCounter logProcessorCounter = new RemainsCounter(1);
    vertx.setPeriodic(30000, new Handler<Long>() {
      public void handle(Long timerID) {
        // logger file check.
        if (logProcessorCounter.remainsGetSet(0) > 0) {
          final String logfilename = new RemainLogFileFinder(appConfig.getLogDir(), locker).findOne();
          if (logfilename != null) {
            JsonObject body =
                new JsonObject()
                    .putString(LogProcessorWorkMsgKey.FILE_NAME, logfilename);
            logProcessorCounter.remainsGetSet(1);
            AppLogger.processLogger.info("process " + logfilename
                + " starting. remain LogProcessorInstancs: " + logProcessorCounter.remainsGetSet(0));
            vertx.eventBus().send(LogProcessorWorkVerticle.VERTICLE_ADDRESS, body,
                new Handler<Message<String>>() {
                  @Override
                  public void handle(Message<String> msg) {
                    logProcessorCounter.remainsGetSet(-1);
                    locker.releaseLock(logfilename);
                    AppLogger.processLogger.info("process " + logfilename
                        + " end. remain LogProcessorInstancs: "
                        + logProcessorCounter.remainsGetSet(0));
                  }
                });
          }
        }
      }
    });
  }
}
