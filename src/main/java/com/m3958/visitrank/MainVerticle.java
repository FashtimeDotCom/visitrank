package com.m3958.visitrank;

/*
 * @author jianglibo@gmail.com
 */

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.logger.UrlPersistorLogger;

public class MainVerticle extends Verticle {

  public void start() {

    AppConstants.initConfigConstants(container.config());

    UrlPersistorLogger.urlPersistor.trace("loger started");

    final Logger log = container.logger();

    JsonObject httpCfg = new JsonObject();
    httpCfg.putNumber("port", AppConstants.HTTP_PORT);

    container.deployVerticle("com.m3958.visitrank.CounterVerticle", httpCfg,
        AppConstants.HTTP_INSTANCE);

    // deploy redis
    JsonObject redisCfg = new JsonObject();
    redisCfg.putString("address", AppConstants.MOD_REDIS_ADDRESS)
        .putString("host", AppConstants.REDIS_HOST).putString("encodeing", "UTF-8")
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

    container.deployVerticle("com.m3958.visitrank.SaveToMongoVerticle",
        AppConstants.SAVETO_MONGO_INSTANCE);

    container.deployVerticle("mapreduce_verticle.js", 1);

    container.deployWorkerVerticle("com.m3958.visitrank.LogProcessorWorkVerticle",
        new JsonObject(), 1);
  }
}
