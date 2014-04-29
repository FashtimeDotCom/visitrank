package com.m3958.visitrank;

/*
 * @author jianglibo@gmail.com
 */

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.AppUtils;

public class MainVerticle extends Verticle {
  
  public static String VERTICLE_NAME = MainVerticle.class.getName();
  
  public void start() {
    final Logger log = container.logger();
    
    container.deployVerticle(SecondStartVerticle.VERTICLE_NAME,1,new AsyncResultHandler<String>() {
      @Override
      public void handle(AsyncResult<String> asyncResult) {
        if (asyncResult.succeeded()) {
          AppUtils.recordDeployed(vertx.sharedData(), SecondStartVerticle.VERTICLE_NAME, asyncResult.result());
          log.info("secondstart verticle has successly deployed:" + asyncResult.result());
          deployAppConfigVerticle(log);
        } else {
          log.info("secondstart verticle deploy failure.");
        }
      }});
  }

  protected void deployAppConfigVerticle(final Logger log) {
    container.deployVerticle(AppConfigVerticle.VERTICLE_NAME,
      new JsonObject().putString("address", AppConfigVerticle.VERTICLE_ADDRESS), 1,
      new AsyncResultHandler<String>() {
        @Override
        public void handle(AsyncResult<String> asyncResult) {
          if (asyncResult.succeeded()) {
            AppUtils.recordDeployed(vertx.sharedData(), AppConfigVerticle.VERTICLE_NAME, asyncResult.result());
            log.info("appconfig verticle has successly deployed:" + asyncResult.result());
          } else {
            log.info("appconfig verticle deploy failure.");
          }
        }
      });
    
  }
}
