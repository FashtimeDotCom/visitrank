package com.m3958.visitrank;

/*
 * @author jianglibo@gmail.com
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.IndexBuilder;
import com.m3958.visitrank.Utils.RemainLogFileFinder;
import com.m3958.visitrank.logger.AppLogger;

public class MainVerticle extends Verticle {

  public void start() {
    Path applog = Paths.get("logs", "app.log");
    if (Files.exists(applog)) {
      try {
        Files.copy(applog, Paths.get("logs",new RemainLogFileFinder("logs").nextLogName()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    final Logger log = container.logger();
    AppConstants.initConfigConstants(container.config());
    log.info("check index status ...");
    IndexBuilder.pageVisitIndex();
    log.info("check index done");
    AppLogger.urlPersistor.trace("loger started");

    JsonObject httpCfg = new JsonObject();
    httpCfg.putNumber("port", AppConstants.HTTP_PORT);

    container.deployVerticle(CounterVerticle.VERTICLE_NAME, httpCfg, AppConstants.HTTP_INSTANCE);

    // deploy redis
    JsonObject redisCfg = new JsonObject();
    redisCfg.putString("address", AppConstants.MOD_REDIS_ADDRESS)
        .putString("host", AppConstants.REDIS_HOST).putString("encode", "UTF-8")
        .putNumber("port", AppConstants.REDIS_PORT);

    container.deployModule(AppConstants.REDIS_MODULE_NAME, redisCfg, AppConstants.REDIS_INSTANCE,
        new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            if (asyncResult.succeeded()) {
              log.info("redis module has successly deployed:" + asyncResult.result());
            } else {
              log.info("redis module deploy failure.");
            }
          }
        });

    // deploy mongodb

    if (!AppConstants.ONLY_LOG) {
      JsonObject mongodbCfg = new JsonObject();
      mongodbCfg.putString("address", AppConstants.MOD_MONGO_PERSIST_ADDRESS)
          .putString("host", AppConstants.MONGODB_HOST).putString("db_name", "visitrank")
          .putNumber("port", AppConstants.MONGODB_PORT);

      container.deployModule(AppConstants.MONGODB_MODULE_NAME, mongodbCfg,
          AppConstants.MONGODB_INSTANCE, new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
              if (asyncResult.succeeded()) {
                log.info("mongo-persistor module has successly deployed:" + asyncResult.result());
              } else {
                log.info("mongo-persistor module deploy failure.");
              }
            }
          });

      container.deployVerticle("mapreduce_verticle.js", 1);

      JsonObject logCheckCfg =
          new JsonObject().putNumber("dailyprocessinstance", AppConstants.DAILY_PROCESSOR_INSTANCE)
              .putNumber("logprocessorinstance", AppConstants.LOG_PROCESSOR_INSTANCE)
              .putNumber("dailydbreadgap", AppConstants.DAILY_DB_READ_GAP)
              .putNumber("logfilereadgap", AppConstants.LOGFILE_READ_GAP)
              .putString("writeconcern", AppConstants.WRITE_CONCERN)
              .putNumber("logitempoolsize", AppConstants.LOGITEM_POOL_SIZE);

      container.deployVerticle(LogCheckVerticle2.VERTICLE_NAME, logCheckCfg, 1);

      // container.deployWorkerVerticle(DailyCopyWorkVerticle.VERTICLE_NAME, new JsonObject(),
      // AppConstants.DAILY_PROCESSOR_INSTANCE, false);

      container.deployWorkerVerticle(LogProcessorWorkVerticle2.VERTICLE_NAME, new JsonObject(),
          AppConstants.LOG_PROCESSOR_INSTANCE, false);
    }
  }
}
