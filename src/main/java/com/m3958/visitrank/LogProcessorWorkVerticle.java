package com.m3958.visitrank;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class LogProcessorWorkVerticle extends Verticle {

  @Override
  public void start() {
    vertx.eventBus().registerHandler("log-processor",new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        try {
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        message.reply(new JsonObject().putString("status", "ok"));
        container.logger().info("Sent back pong");
      }
    });
    container.logger().info("LogProcessorWorkVerticle started");
  }
}
