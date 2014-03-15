package com.m3958.visitrank;

/*
 * @author jianglibo@gmail.com
 */

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class MainVerticle extends Verticle {

  public void start() {

    JsonObject config = container.config();
    
    final Logger log = container.logger();

    int http_port = config.getInteger("httpport", 0);

    if (http_port == 0) {
      http_port = AppConstants.HTTP_PORT;
    }

    int redis_port = config.getInteger("redisport", 0);

    if (redis_port == 0) {
      redis_port = AppConstants.REDIS_PORT;
    }

    int mongodb_port = config.getInteger("mongodbport", 0);

    if (mongodb_port == 0) {
      mongodb_port = AppConstants.MONGODB_PORT;
    }

    JsonObject httpCfg = new JsonObject();
    httpCfg.putNumber("port", http_port);

    container.deployVerticle("com.m3958.visitrank.CounterVerticle", httpCfg, config.getInteger("httpinstance",5));

    // deploy redis
    JsonObject redisCfg = new JsonObject();
    redisCfg.putString("address", AppConstants.MOD_REDIS_ADDRESS).putString("host", "127.0.0.1")
        .putString("encodeing", "UTF-8").putNumber("port", redis_port);

    container.deployModule("io.vertx~mod-redis~1.1.3", redisCfg, 1,
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
    mongodbCfg.putString("address", AppConstants.MOD_MONGO_PERSIST_ADDRESS).putString("host", "localhost")
        .putString("db_name", "visitrank").putNumber("port", mongodb_port);

    container.deployModule("io.vertx~mod-mongo-persistor~2.1.1", mongodbCfg, 1,
        new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            if (asyncResult.succeeded()) {
              log.info("mongo-persistor module has successly deployed:"
                  + asyncResult.result());
            } else {
              log.info("mongo-persistor module deploy failure.");
            }
          }
        });

    container.deployVerticle("com.m3958.visitrank.SaveToMongoVerticle", 1);

    container.deployVerticle("mapreduce_verticle.js", 1);
  }
}
