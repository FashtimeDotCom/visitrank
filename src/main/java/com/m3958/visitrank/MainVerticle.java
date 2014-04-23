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
import com.m3958.visitrank.Utils.IndexBuilder;
import com.m3958.visitrank.Utils.RemainLogFileFinder;
import com.m3958.visitrank.logger.AppLogger;

public class MainVerticle extends Verticle {

  public void start() {
    final Logger log = container.logger();

    container.deployVerticle(AppConfigVerticle.VERTICLE_NAME,
        new JsonObject().putString("address", AppConfigVerticle.VERTICLE_ADDRESS), 1,
        new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            if (asyncResult.succeeded()) {
              log.info("appconfig verticle has successly deployed:" + asyncResult.result());
              vertx.eventBus().send(AppConfigVerticle.VERTICLE_ADDRESS, new JsonObject(),
                  new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> message) {
                      AppConfig appConfig = new AppConfig(message.body(), true);
                      deployAll(appConfig, log);
                    }
                  });
            } else {
              log.info("appconfig verticle deploy failure.");
            }
          }
        });
  }

  protected void deployAll(AppConfig appConfig, final Logger log) {

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

    container.deployVerticle(LogSaverVerticle.VERTICLE_NAME, appConfig.getLogSaverInstance());

    container.deployVerticle(CounterVerticle.VERTICLE_NAME, appConfig.getHttpInstance());

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
              log.info("redis module has successly deployed:" + asyncResult.result());
            } else {
              log.info("redis module deploy failure.");
            }
          }
        });
    // deploy mongodb
    if (!appConfig.isOnlyLog()) {
      JsonObject mongodbCfg = new JsonObject();
      mongodbCfg.putString("address", appConfig.getMongoAddress())
          .putString("host", appConfig.getMongoHost()).putString("db_name", "visitrank")
          .putNumber("port", appConfig.getMongoPort());

      container.deployModule(appConfig.getMongoModuleName(), mongodbCfg,
          appConfig.getMongoInstance(), new AsyncResultHandler<String>() {
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

      container.deployVerticle(LogCheckVerticle.VERTICLE_NAME, 1);

      container.deployWorkerVerticle(LogProcessorWorkVerticle.VERTICLE_NAME, new JsonObject(), 1,
          false);
    }
  }
}
