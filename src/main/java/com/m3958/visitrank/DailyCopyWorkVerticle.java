package com.m3958.visitrank;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

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


  @Override
  public void start() {
    vertx.eventBus().registerHandler(AppConstants.DAILY_MOVE_DB_ADDRESS,
        new Handler<Message<String>>() {
          @Override
          public void handle(Message<String> message) {
            new DailyCopyProcessor().dailyCopy();
          }
        });
  }

  public static class DailyCopyProcessor {

    public void dailyCopy() {
      Calendar rightNow = Calendar.getInstance();
      int hour = rightNow.get(Calendar.HOUR_OF_DAY);
      if (hour == 1) {
        MongoClient mongoClient;
        try {
          mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
          for (String dbname : mongoClient.getDatabaseNames()) {
            if (AppUtils.isDailyDb(dbname) && !isDailyInProcess(mongoClient, dbname)) {
              if (isDailyDbComplete(mongoClient, dbname)) {
                markInProcess(mongoClient, dbname);
                try {
                  copyDailyDb(mongoClient, dbname);
                } catch (IOException e) {
                  mongoClient.close();
                }
              }
            }
          }
        } catch (UnknownHostException e) {}
      }
    }

    /**
     * when all hourly job is end,return true
     * 
     * @param mongoClient
     * @param dbname
     * @return
     */
    private boolean isDailyDbComplete(MongoClient mongoClient, String dbname) {
      DB db = mongoClient.getDB(dbname);
      DBCollection coll = db.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
      DBCursor cursor = coll.find();
      List<Integer> hourlyJobAry = new ArrayList<>();
      boolean hasTwentyFour = false;
      try {
        while (cursor.hasNext()) {
          DBObject item = cursor.next();
          Integer jobHour = (Integer) item.get(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY);
          String status = (String) item.get(AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY);
          if (!"end".equals(status)) {
            return false;
          }
          if (jobHour == 24) {
            hasTwentyFour = true;
          }
          hourlyJobAry.add(jobHour);
        }
      } finally {
        cursor.close();
        mongoClient.close();
      }
      if (hasTwentyFour) {
        int jobSize = hourlyJobAry.size();
        Collections.sort(hourlyJobAry);
        int gap = hourlyJobAry.get(jobSize - 1) - hourlyJobAry.get(0);
        if (gap + 1 == hourlyJobAry.size()) {
          return true;
        }
      }
      return false;
    }


    public void copyDailyDb(MongoClient mongoClient, String dbname) throws IOException {
      DB dailyDb = mongoClient.getDB(dbname);
      DBCollection dailyColl = dailyDb.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);

      DB repositoryDb = mongoClient.getDB(AppConstants.MongoNames.REPOSITORY_DB_NAME);
      DBCollection repositoryCol =
          repositoryDb.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
      Path partialLogPath = Paths.get(AppConstants.DAILY_PARTIAL_DIR, dbname);

      long partialStart = 0;
      if (Files.exists(partialLogPath)) {
        partialStart = AppUtils.getLastPartialPosition(partialLogPath);
      }

      OutputStreamWriter partialWriter =
          new OutputStreamWriter(new FileOutputStream(partialLogPath.toFile()), "UTF-8");
      partialWriter.write(partialStart + "," + partialStart + "\n");

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
        if (counter % AppConstants.DAILY_DB_READ_GAP == 0) {
          partialWriter.write(counter + ",");
          partialWriter.flush();
          repositoryCol.insert(obs);
          partialWriter.write(counter + "\n");
          partialWriter.flush();
          obs.clear();
        }
      }

      if (obs.size() > 0) {
        partialWriter.write(counter + ",");
        partialWriter.flush();
        repositoryCol.insert(obs);
        partialWriter.write(counter + "\n");
        partialWriter.flush();
      }
      partialWriter.close();
      Files.delete(partialLogPath);
    }

    public boolean isDailyInProcess(MongoClient mongoClient, String dbname) {
      DB db = mongoClient.getDB(dbname);
      DBCollection coll = db.getCollection(AppConstants.MongoNames.DAILY_JOB_COL_NAME);
      DBObject item = coll.findOne();
      if (item == null) {
        return false;
      }
      return true;
    }

    public void markInProcess(MongoClient mongoClient, String dbname) {
      DB db = mongoClient.getDB(dbname);
      DBCollection coll = db.getCollection(AppConstants.MongoNames.DAILY_JOB_COL_NAME);
      DBObject dbo = new BasicDBObject();
      dbo.put(AppConstants.MongoNames.DAILY_JOB_STATUS_KEY, "yes");
      coll.insert(dbo);
    }
  }
}
