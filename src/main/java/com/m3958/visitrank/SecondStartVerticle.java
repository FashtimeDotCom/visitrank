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
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.Utils.IndexBuilder;
import com.m3958.visitrank.Utils.RemainLogFileFinder;
import com.m3958.visitrank.logger.AppLogger;

public class SecondStartVerticle extends Verticle {

  public static String VERTICLE_ADDRESS = "second-start-verticle";

  public static String VERTICLE_NAME = SecondStartVerticle.class.getName();

  public void start() {
    final Logger log = container.logger();
    vertx.eventBus().registerHandler(VERTICLE_ADDRESS, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        final JsonObject body = message.body();
        final AppConfig appConfig = new AppConfig(body, true);
        deployAll(appConfig, body, log);
      }
    });
  }

  protected void deployAll(AppConfig appConfig, JsonObject configJson, final Logger log) {

    Path applog = Paths.get(appConfig.getLogDir(), "app.log");

    if (Files.exists(applog)) {
      try {
        String np = new RemainLogFileFinder(appConfig.getLogDir()).nextLogName();
        log.info("copy log.app to: " + np);
        Files.copy(applog, Paths.get(appConfig.getLogDir(), np));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    log.info("check pagevisit index status ...");
    IndexBuilder.pageVisitIndex(appConfig);
    log.info("check pagevisit index done");

    log.info("check hostname index status ...");
    IndexBuilder.hostNameIndex(appConfig);
    log.info("check hostname index done");

    AppLogger.urlPersistor.trace("loger started");

    // deploy redis
    JsonObject redisCfg = new JsonObject();
    redisCfg.putString("address", appConfig.getRedisAddress())
        .putString("host", appConfig.getRedisHost()).putString("encode", appConfig.getCharset())
        .putNumber("port", appConfig.getRedisPort());

    container.deployModule(appConfig.getRedisModuleName(), redisCfg, appConfig.getRedisInstance(),
        new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            if (asyncResult.succeeded()) {
              AppUtils.recordDeployed(vertx.sharedData(), "redis", asyncResult.result());
              log.info("redis module has successly deployed:" + asyncResult.result());
            } else {
              log.info("redis module deploy failure.");
            }
          }
        });


    container.deployVerticle(LogSaverVerticle.VERTICLE_NAME, configJson,
        appConfig.getLogSaverInstance(), new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            if (asyncResult.succeeded()) {
              AppUtils.recordDeployed(vertx.sharedData(), LogSaverVerticle.VERTICLE_NAME,
                  asyncResult.result());
              log.info("loggersaver verticle has successly deployed:" + asyncResult.result());
            } else {
              log.info("loggersaver verticle deploy failure.");
            }
          }
        });

    container.deployVerticle(CounterVerticle.VERTICLE_NAME, configJson,
        appConfig.getHttpInstance(), new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            if (asyncResult.succeeded()) {
              AppUtils.recordDeployed(vertx.sharedData(), CounterVerticle.VERTICLE_NAME,
                  asyncResult.result());
              log.info("counter verticle has successly deployed:" + asyncResult.result());
            } else {
              log.info("counter verticle deploy failure.");
            }
          }
        });


    // deploy mongodb
    if (!appConfig.isOnlyLog()) {
      JsonObject mongodbCfg = new JsonObject();
      mongodbCfg.putString("address", appConfig.getMongoAddress())
          .putString("host", appConfig.getMongoHost()).putString("db_name", appConfig.getRepoDbName())
          .putNumber("port", appConfig.getMongoPort());

      container.deployModule(appConfig.getMongoModuleName(), mongodbCfg,
          appConfig.getMongoInstance(), new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
              if (asyncResult.succeeded()) {
                AppUtils.recordDeployed(vertx.sharedData(), "mongo-persistor", asyncResult.result());
                log.info("mongo-persistor module has successly deployed:" + asyncResult.result());
              } else {
                log.info("mongo-persistor module deploy failure.");
              }
            }
          });

      appConfig.closeMongoClient();

      container.deployVerticle("mapreduce_verticle.js", 1);

      container.deployVerticle(LogCheckVerticle.VERTICLE_NAME, configJson, 1,
          new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
              if (asyncResult.succeeded()) {
                AppUtils.recordDeployed(vertx.sharedData(), LogCheckVerticle.VERTICLE_NAME,
                    asyncResult.result());
                log.info("logchecker verticle has successly deployed:" + asyncResult.result());
              } else {
                log.info("logchecker verticle deploy failure.");
              }
            }
          });

      container.deployWorkerVerticle(LogProcessorWorkVerticle.VERTICLE_NAME, configJson, 1, false,
          new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
              if (asyncResult.succeeded()) {
                AppUtils.recordDeployed(vertx.sharedData(), LogProcessorWorkVerticle.VERTICLE_NAME,
                    asyncResult.result());
                log.info("logprocessor verticle has successly deployed:" + asyncResult.result());
              } else {
                log.info("logprocessor verticle deploy failure.");
              }
            }
          });
    }
  }
}
