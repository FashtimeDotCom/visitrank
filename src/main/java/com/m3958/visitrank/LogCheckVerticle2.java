package com.m3958.visitrank;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.LogProcessorWorkVerticle2.LogProcessorWorkMsgKey;
import com.m3958.visitrank.Utils.Locker;
import com.m3958.visitrank.Utils.RemainLogFileFinder;
import com.m3958.visitrank.Utils.RemainsCounter;
import com.m3958.visitrank.Utils.WriteConcernParser;
import com.m3958.visitrank.logger.AppLogger;

/**
 * We run one instance of this verticle,so don't worry about concurrency problem.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class LogCheckVerticle2 extends Verticle {

  private Locker locker = new Locker();
  
  public static String VERTICLE_NAME = LogCheckVerticle2.class.getName();

  @Override
  public void start() {
    final Logger log = container.logger();
    JsonObject jo = container.config();
    final String logDir = jo.getString(LogProcessorWorkMsgKey.LOG_DIR, "logs");
    final String archiveDir = jo.getString(LogProcessorWorkMsgKey.ARCHIVE_DIR, "archives");

    final Long logfilereadgap = jo.getLong("logfilereadgap");

    int logProcessorInstance = jo.getInteger("logprocessorinstance", 5);
    
    log.info("logProcessorInstance: " + logProcessorInstance);
    
    log.info("logfilereadgap: " + logfilereadgap);
    
    final JsonObject writeConcern = new WriteConcernParser(jo.getString("writeconcern","")).parse();
    log.info(writeConcern);

    final RemainsCounter logProcessorCounter = new RemainsCounter(logProcessorInstance);
    vertx.setPeriodic(30000, new Handler<Long>() {
      public void handle(Long timerID) {
        // logger file check.
        if (logProcessorCounter.remainsGetSet(0) > 0) {
          final String logfilename = new RemainLogFileFinder(logDir, locker).findOne();
          if (logfilename != null) {
            JsonObject body =
                new JsonObject().putString(LogProcessorWorkMsgKey.LOG_DIR, logDir)
                    .putString(LogProcessorWorkMsgKey.FILE_NAME, logfilename)
                    .putString(LogProcessorWorkMsgKey.ARCHIVE_DIR, archiveDir)
                    .putNumber("logfilereadgap", logfilereadgap)
                    .putObject("writeconcern", writeConcern);
            logProcessorCounter.remainsGetSet(1);
            AppLogger.processLogger.info("process " + logfilename
                + " starting. remain LogProcessorInstancs: " + logProcessorCounter.remainsGetSet(0));
            vertx.eventBus().send(LogProcessorWorkVerticle2.VERTICLE_ADDRESS, body,
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
    log.info("First this is printed");
  }

}
