package com.m3958.visitrank;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.Utils.WriteConcernParser;
import com.m3958.visitrank.logger.AppLogger;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

/**
 * it's a sync worker verticle. unlike LogProcessorWorkVerticle, We don't consider task overlap. So
 * we can use one verticle instance to process this task. If one task lasting moer than 24 hours,
 * this design may cause problem.what will happen when send a message to workverticle,that
 * workverticle still in process previous message?
 * 
 * resolver: we can run mulitple instances,when start one task on one db,we can set an flag
 * indicator it's processing status.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class DailyCopyWorkVerticle extends Verticle {

  public static String VERTICLE_ADDRESS = "move_daily_db_address";
  public static String VERTICLE_NAME = DailyCopyWorkVerticle.class.getName();

  public static class DailyProcessorWorkMsgKey {
    public static String DBNAME = "dbname";
  }

  @Override
  public void start() {
    vertx.eventBus().registerHandler(VERTICLE_ADDRESS, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        JsonObject body = message.body();
        String dbname = body.getString(DailyProcessorWorkMsgKey.DBNAME);
        MongoClient mongoClient;
        try {
          mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
          boolean processed = new DailyCopyProcessor(mongoClient, dbname, body).process();
          if(processed){
            message.reply("done");
          }else{
            message.reply("undone");
          }
          return;
        } catch (UnknownHostException e) {}
        message.reply("undone");
      }
    });
  }

  public static class DailyCopyProcessor {

    private String repositoryDbName;

    private String dailyDbname;

    private MongoClient mongoClient;

    private String dailyPartialDir;

    private long gap;

    private JsonObject writeConcern;

    public DailyCopyProcessor(MongoClient mongoClient, String dailyDbname, JsonObject cfg) {
      this.mongoClient = mongoClient;
      this.dailyDbname = dailyDbname;
      this.repositoryDbName = AppConstants.MongoNames.REPOSITORY_DB_NAME;
      this.dailyPartialDir = AppConstants.DAILY_PARTIAL_DIR;
      this.gap = cfg.getLong("dailydbreadgap", 1000);
      this.writeConcern = cfg.getObject("writeConcern");
    }

    public DailyCopyProcessor(MongoClient mongoClient, String dailyDbname, String repositoryDbName,
        String dailyPartialDir, JsonObject cfg) {
      this.repositoryDbName = repositoryDbName;
      this.mongoClient = mongoClient;
      this.dailyDbname = dailyDbname;
      this.dailyPartialDir = dailyPartialDir;
      this.gap = cfg.getLong("dailydbreadgap", 1000);
      this.writeConcern = cfg.getObject("writeConcern");
    }

    /**
     * when a newer db exist,and all hourlyjob in this db has end.
     * 
     * @return
     */
    public boolean isDailyDbComplete() {
      DB db = mongoClient.getDB(dailyDbname);
      DBCollection coll = db.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
      DBCursor cursor = coll.find();
      List<String> hourlyJobAry = new ArrayList<>();
      try {
        while (cursor.hasNext()) {
          DBObject item = cursor.next();
          String jobHour;
          try{
            jobHour = String.valueOf((Integer) item.get(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY));
          }catch(Exception e){
            jobHour = (String) item.get(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY);
          }
          String status = (String) item.get(AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY);
          if (!"end".equals(status)) {
            return false;
          }
          hourlyJobAry.add(jobHour);
        }
      } finally {
        cursor.close();
      }
      if (hourlyJobAry.size() < 1) {
        return false;
      } else {
        return true;
      }
    }


    public boolean process() {
      if (isDailyDbComplete()) {
        try {
          AppLogger.processLogger.info("process daily copy " + dailyDbname + " starting.");
          copyDailyDb();
          return true;
        } catch (IOException e) {}
      }
      return false;
    }

    public void copyDailyDb() throws IOException {

      WriteConcern wc = WriteConcernParser.getWriteConcern(writeConcern);

      DB dailyDb = mongoClient.getDB(dailyDbname);
      DBCollection dailyColl = dailyDb.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);

      DB repositoryDb = mongoClient.getDB(repositoryDbName);
      DBCollection repositoryCol =
          repositoryDb.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);

      if (!Files.exists(Paths.get(dailyPartialDir))) {
        Files.createDirectory(Paths.get(dailyPartialDir));
      }
      Path partialLogPath = Paths.get(dailyPartialDir, dailyDbname);

      long partialStart = 0;
      if (Files.exists(partialLogPath)) {
        partialStart = AppUtils.getLastPartialPosition(partialLogPath);
      }

      OutputStreamWriter partialWriter =
          new OutputStreamWriter(new FileOutputStream(partialLogPath.toFile()), "UTF-8");
      partialWriter.write(partialStart + "," + partialStart + AppConstants.LINE_SEP);

      //sort({$natural: 1})
      DBCursor cursor = dailyColl.find();
      int counter = 0;
      List<DBObject> obs = new ArrayList<>();
      while (cursor.hasNext()) {
        if (counter < partialStart) {
          counter++;
          cursor.next();
          continue;
        }

        DBObject item = cursor.next();
        counter++;
        obs.add(item);
        if (counter % gap == 0) {
          partialWriter.write(counter + ",");
          partialWriter.flush();
          if (wc == null) {
            repositoryCol.insert(obs);
          } else {
            repositoryCol.insert(obs, wc);
          }
          partialWriter.write(counter + AppConstants.LINE_SEP);
          partialWriter.flush();
          obs.clear();
        }
      }

      if (obs.size() > 0) {
        partialWriter.write(counter + ",");
        partialWriter.flush();
        if (wc == null) {
          repositoryCol.insert(obs);
        } else {
          repositoryCol.insert(obs, wc);
        }
        partialWriter.write(counter + AppConstants.LINE_SEP);
        partialWriter.flush();
      }
      obs.clear();
      partialWriter.close();
      Files.delete(partialLogPath);
      mongoClient.dropDatabase(dailyDbname);
    }
  }
}
