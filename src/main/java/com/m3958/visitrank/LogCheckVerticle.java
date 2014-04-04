package com.m3958.visitrank;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.DailyCopyWorkVerticle.DailyProcessorWorkMsgKey;
import com.m3958.visitrank.LogProcessorWorkVerticle.LogProcessorWorkMsgKey;
import com.m3958.visitrank.Utils.Locker;
import com.m3958.visitrank.Utils.RemainsCounter;
import com.m3958.visitrank.logger.AppLogger;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

/**
 * We run one instance of this verticle,so don't worry about concurrency problem.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class LogCheckVerticle extends Verticle {

  private Locker locker = new Locker();

  @Override
  public void start() {
    final Logger log = container.logger();
    JsonObject jo = container.config();
    final String logDir = jo.getString(LogProcessorWorkMsgKey.LOG_DIR, "logs");
    final String archiveDir = jo.getString(LogProcessorWorkMsgKey.ARCHIVE_DIR, "archives");

    final Long logfilereadgap = jo.getLong("logfilereadgap");
    final Long dailydbreadgap = jo.getLong("dailydbreadgap");

    int logProcessorInstance = jo.getInteger("logprocessorinstance", 5);
    int dailyProcessorInstance = jo.getInteger("dailyprocessinstance", 3);
    
    log.info("logProcessorInstance: " + logProcessorInstance);
    log.info("dailyProcessorInstance: " + dailyProcessorInstance);
    
    log.info("logfilereadgap: " + logfilereadgap);
    log.info("dailydbreadgap: " + dailydbreadgap);
    
    final JsonObject writeConcern = new WriteConcernParser(jo.getString("writeconcern","")).parse();
    log.info(writeConcern);

    final RemainsCounter dailyProcessorCounter = new RemainsCounter(dailyProcessorInstance);
    vertx.setPeriodic(45111, new Handler<Long>() {
      public void handle(Long timerID) {
        // logger file check.
        final String dbname = new RemainDailyDbFinder(locker).findOne("^\\d{4}-\\d{2}-\\d{2}$");
        if (dbname != null) {
          log.info("find dailydb:" + dbname + ", remain DailyProcessor: " + (dailyProcessorCounter.remainsGetSet(0) - 1));
          if (dailyProcessorCounter.remainsGetSet(0) > 0) {
            JsonObject body =
                new JsonObject().putString(DailyProcessorWorkMsgKey.DBNAME, dbname).putNumber(
                    "dailydbreadgap", dailydbreadgap).putObject("writeConcern", writeConcern);

            dailyProcessorCounter.remainsGetSet(1);
            vertx.eventBus().send(DailyCopyWorkVerticle.VERTICLE_ADDRESS, body,
                new Handler<Message<String>>() {
                  @Override
                  public void handle(Message<String> msg) {
                    String reply = msg.body();
                    if("done".equals(reply)){
                      AppLogger.processLogger.info("process daily copy " + dbname + " end.");
                    }
                    dailyProcessorCounter.remainsGetSet(-1);
                    locker.releaseLock(dbname);
                  }
                });
          }
        }
      }
    });

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
    log.info("First this is printed");
  }

  public static class RemainLogFileFinder {
    private String logDirStr;

    private static Pattern fptn = Pattern.compile(".*\\d{4}-\\d{2}-\\d{2}.*\\.log");

    private Locker locker;

    public RemainLogFileFinder(String logDirStr, Locker locker) {
      this.logDirStr = logDirStr;
      this.locker = locker;
    }

    public String findOne() {
      File logDir = new File(logDirStr);
      String[] files = logDir.list();
      for (String f : files) {
        Matcher m = fptn.matcher(f);
        if (f.endsWith("log") && m.matches()) { // find log file.
          if (locker.canLockLog(f)) {
            return f;
          }
        }
      }
      return null;
    }
  }

  public static class RemainDailyDbFinder {
    private Locker locker;

    public RemainDailyDbFinder(Locker locker) {
      this.locker = locker;
    }

    public String findOne(String ptn) {
      String foundDbName = null;

      MongoClient mongoClient;
      try {
        mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);

        List<String> alldbnames = mongoClient.getDatabaseNames();
        List<String> dbnames = new ArrayList<>();

        for (String dbname : alldbnames) {
          if (dbname.matches(ptn)) {
            dbnames.add(dbname);
          }
        }

        Collections.sort(dbnames);
        Iterator<String> it = dbnames.iterator();
        while (it.hasNext()) {
          String dbname = it.next();
          DB db = mongoClient.getDB(dbname);
          DBCollection hourlyCol = db.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
          DBCursor cursor = hourlyCol.find();
          boolean hexist = cursor.hasNext();
          cursor.close();
          if (hexist && locker.canLockLog(dbname) && it.hasNext()) {
            foundDbName = dbname;
            break;
          }
        }
        mongoClient.close();
        return foundDbName;
      } catch (Exception e) {
        AppLogger.error.error(e.getMessage());
        return null;
      }
    }
  }

  public static class WriteConcernParser {
    private String raw;

    public WriteConcernParser(String raw) {
      this.raw = raw;
    }

    public JsonObject parse() {
      if (raw.isEmpty()) {
        return null;
      }
      // WriteConcern(int w, int wtimeout, boolean fsync, boolean j, boolean continueOnInsertError)
      String[] ss = raw.split(",");
      JsonObject jo = new JsonObject();
      jo.putNumber("w", Integer.parseInt(ss[0], 10));
      jo.putNumber("wtimeout", Long.parseLong(ss[1], 10));
      jo.putBoolean("fsync", "true".equals(ss[2]) ? true : false);
      jo.putBoolean("j", "true".equals(ss[3]) ? true : false);
      jo.putBoolean("continueOnInsertError", "true".equals(ss[4]) ? true : false);
      return jo;
    }

    public static WriteConcern getWriteConcern(JsonObject jo){
      if (jo != null) {
        return
            new WriteConcern(jo.getInteger("w"), jo.getInteger("wtimeout"),
                jo.getBoolean("fsync"), jo.getBoolean("j"),
                jo.getBoolean("continueOnInsertError"));
      }else{
        return null;
      }
    }
  }
}
