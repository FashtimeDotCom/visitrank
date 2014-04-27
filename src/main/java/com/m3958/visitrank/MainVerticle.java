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
  
  public static String VERTICLE_NAME = MainVerticle.class.getName();
  
  public void start() {
    final Logger log = container.logger();
    
    container.deployVerticle(SecondStartVerticle.VERTICLE_NAME);

    container.deployVerticle(AppConfigVerticle.VERTICLE_NAME,
        new JsonObject().putString("address", AppConfigVerticle.VERTICLE_ADDRESS), 1,
        new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            if (asyncResult.succeeded()) {
              log.info("appconfig verticle has successly deployed:" + asyncResult.result());
            } else {
              log.info("appconfig verticle deploy failure.");
            }
          }
        });
  }
}
