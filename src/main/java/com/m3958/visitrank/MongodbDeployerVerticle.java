package com.m3958.visitrank;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class MongodbDeployerVerticle extends Verticle {

  public static String MONGODB_DEPLOYER;

  public void start() {
    vertx.eventBus().registerHandler(MONGODB_DEPLOYER, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        String filename = message.body().getString("filename");
        
      }
    });
    container.logger().info("MongodbDeployerVerticle started");
  }
}
