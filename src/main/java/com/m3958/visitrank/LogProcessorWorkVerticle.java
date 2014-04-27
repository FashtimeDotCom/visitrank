package com.m3958.visitrank;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.Utils.IndexBuilder;
import com.m3958.visitrank.Utils.LogItemTransformer;
import com.m3958.visitrank.Utils.PartialUtil;
import com.m3958.visitrank.interf.TestableVerticle;
import com.m3958.visitrank.logger.AppLogger;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

/**
 * it's a sync worker verticle. First we start a mongodb connection, readlines from logfile,batchly
 * insert into mongodb. according log filename,hourly 2014-03-03-01,construct hourly dbname,daily
 * dbname 2014-03-03.maybe monthly dbname:2014-03,yearly dbname 2014.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class LogProcessorWorkVerticle extends Verticle implements TestableVerticle {

  public static String VERTICLE_ADDRESS = "logprocessor";
  public static String VERTICLE_NAME = LogProcessorWorkVerticle.class.getName();

  public static class LogProcessorWorkMsgKey {
    public static String FILE_NAME = "filename";
    public static String REPLY = "reply";
  }

  @Override
  public void start() {
    
    if (!AppUtils.deployTestableVerticle(this, container)) {
      final Logger log = container.logger();
      final AppConfig appConfig = new AppConfig(container.config(), false);
      deployMe(appConfig, log);
    }
  }

  public void deployMe(final AppConfig appConfig,Logger log) {
    vertx.eventBus().registerHandler(VERTICLE_ADDRESS, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        final JsonObject body = message.body();
        final String filename = body.getString(LogProcessorWorkMsgKey.FILE_NAME);
        LogItemTransformer logItemTransformer = new LogItemTransformer(appConfig);
        new LogProcessor(appConfig, logItemTransformer, filename).process();
        message.reply("done");
      }
    });
  }

  public static class LogProcessor {
    private String filename;

    private AppConfig appConfig;

    private LogItemTransformer logItemTransformer;

    public LogProcessor(AppConfig appConfig, LogItemTransformer logItemTransformer, String filename) {
      this.appConfig = appConfig;
      this.logItemTransformer = logItemTransformer;
      this.filename = filename;
    }

    public void process() {
      try {
        WriteConcern wc = appConfig.getWriteConcern();
        Path logfilePath = Paths.get(appConfig.getLogDir(), filename);
        Path partialLogPath =
            Paths.get(appConfig.getLogDir(), filename + AppConstants.PARTIAL_POSTFIX);
        DB db = appConfig.getMongoClient().getDB(appConfig.getRepoDbName());
        DBCollection coll = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);

        coll.createIndex(IndexBuilder.getPageVisitColIndexKeys());

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(new FileInputStream(logfilePath.toFile()),
                "UTF-8"));


        long start = 0;
        if (Files.exists(partialLogPath)) {
          start = PartialUtil.findLastProcessDbObject(appConfig, logfilePath);
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
            dbos.add(logItemTransformer.transformToDb(line));
          } catch (Exception e) {
            AppLogger.error.error("parse exception:" + line);
          }
          counter++;
          if (counter % appConfig.getLogFileReadGap() == 0) {
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

        AppUtils.moveLogFiles(appConfig.getLogDir(), appConfig.getArchiveDir(), filename,
            logfilePath);
      } catch (UnsupportedEncodingException | FileNotFoundException e) {
        AppLogger.error.error("cann't create reader from file: " + filename);
      } catch (UnknownHostException e1) {
        AppLogger.error.error("cann't connect to mongo host: " + filename);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
