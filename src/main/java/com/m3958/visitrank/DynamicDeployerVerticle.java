package com.m3958.visitrank;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.logger.AppLogger;

/**
 * when timer detect an logfile,it will send a message to this verticle, then deploy a
 * logprocessverticle, then send a message to logprocessverticle.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class DynamicDeployerVerticle extends Verticle {


  public static String DYNAMIC_DEPLOYER;

  public void start() {
    vertx.eventBus().registerHandler(DYNAMIC_DEPLOYER, new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> message) {
        final String filename = message.body();



      }
    });
    container.logger().info("MongodbDeployerVerticle started");
  }
}
