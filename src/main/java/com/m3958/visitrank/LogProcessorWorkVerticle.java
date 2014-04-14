package com.m3958.visitrank;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.IndexBuilder;
import com.m3958.visitrank.Utils.PartialUtil;
import com.m3958.visitrank.Utils.WriteConcernParser;
import com.m3958.visitrank.logger.AppLogger;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

/**
 * it's a sync worker verticle. First we start a mongodb connection, readlines from logfile,batchly
 * insert into mongodb. according log filename,hourly 2014-03-03-01,construct hourly dbname,daily
 * dbname 2014-03-03.maybe monthly dbname:2014-03,yearly dbname 2014.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class LogProcessorWorkVerticle extends Verticle {

  public static String VERTICLE_ADDRESS = "logprocessor";
  public static String VERTICLE_NAME = LogProcessorWorkVerticle.class.getName();

  public static class LogProcessorWorkMsgKey {
    public static String FILE_NAME = "filename";
    public static String LOG_DIR = "logDir";
    public static String ARCHIVE_DIR = "archiveDir";
    public static String REPLY = "reply";
    public static String LOGITEM_POOL_SIZE = "logitempoolsize";
  }

  @Override
  public void start() {
    vertx.eventBus().registerHandler(VERTICLE_ADDRESS, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        final JsonObject body = message.body();
        final String filename = body.getString(LogProcessorWorkMsgKey.FILE_NAME);
        final String logDir = body.getString(LogProcessorWorkMsgKey.LOG_DIR, "logs");
        final String archiveDir = body.getString(LogProcessorWorkMsgKey.ARCHIVE_DIR, "archives");
        final int logitemPoolSize = body.getInteger(LogProcessorWorkMsgKey.LOGITEM_POOL_SIZE, 100);
        new LogProcessor(logDir, archiveDir, filename, body, logitemPoolSize).process();
        message.reply("done");
      }
    });
  }

  public static class LogProcessor {
    private String filename;
    private String logDir;
    private String archiveDir;

    private long gap;

    private JsonObject writeconcern;

    public LogProcessor(String logDir, String archiveDir, String filename, JsonObject cfg,
        int logitemPoolSize) {
      this.logDir = logDir;
      this.archiveDir = archiveDir;
      this.filename = filename;
      this.gap = cfg.getLong("logfilereadgap", 1000);
      this.writeconcern = cfg.getObject("writeconcern");
    }

    public void process() {
      try {
        WriteConcern wc = WriteConcernParser.getWriteConcern(writeconcern);
        Path logfilePath = Paths.get(logDir, filename);
        Path partialLogPath = Paths.get(logDir, filename + AppConstants.PARTIAL_POSTFIX);

        MongoClient mongoClient =
            new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
        DB db = mongoClient.getDB(AppConstants.MongoNames.REPOSITORY_DB_NAME);
        DBCollection coll = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);

        coll.createIndex(IndexBuilder.getPageVisitColIndexKeys());

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(new FileInputStream(logfilePath.toFile()),
                "UTF-8"));


        long start = 0;
        if (Files.exists(partialLogPath)) {
          start = PartialUtil.findLastProcessDbObject(logfilePath);
        } else {
          Files.createFile(partialLogPath);
        }

        List<DBObject> dbos = new ArrayList<>();

        String line;
        long counter = 0;
        while ((line = reader.readLine()) != null) {
          if (counter < start) {
            counter++;
            continue;
          }
          try {
            dbos.add((DBObject) JSON.parse(line));
          } catch (Exception e) {
            AppLogger.error.error("parse exception:" + line);
          }
          counter++;
          if (counter % gap == 0) {
            if (wc == null) {
              coll.insert(dbos);
            } else {
              coll.insert(dbos, wc);
            }
            dbos.clear();
          }
        }
        if (dbos.size() > 0) {
          if (wc == null) {
            coll.insert(dbos);
          } else {
            coll.insert(dbos, wc);
          }
          dbos.clear();
        }
        reader.close();

        Files.delete(partialLogPath);

        mongoClient.close();

        moveLogFiles(logfilePath);
      } catch (UnsupportedEncodingException | FileNotFoundException e) {
        AppLogger.error.error("cann't create reader from file: " + filename);
      } catch (UnknownHostException e1) {
        AppLogger.error.error("cann't connect to mongo host: " + filename);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void moveLogFiles(Path logfilePath) throws IOException {
      Path archiedPath = Paths.get(archiveDir);
      if (!archiedPath.toFile().exists()) {
        Files.createDirectories(archiedPath);
      }
      if (Files.exists(archiedPath.resolve(filename), LinkOption.NOFOLLOW_LINKS)) {
        Files.move(logfilePath, archiedPath.resolve(filename + ".duplicated"));
      } else {
        Files.move(logfilePath, archiedPath.resolve(filename));
      }
      if (Files.exists(Paths.get(logDir, filename + AppConstants.PARTIAL_POSTFIX),
          LinkOption.NOFOLLOW_LINKS)) {
        Files.delete(Paths.get(logDir, filename + AppConstants.PARTIAL_POSTFIX));
      }
    }
  }
}
