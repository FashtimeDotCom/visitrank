package com.m3958.visitrank;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class LogProcessorWorkVerticle extends Verticle {

  @Override
  public void start() {
    JsonObject cfg = container.config();

    final String address = cfg.getString("address");
    final String mongoAddress = cfg.getString("mongoAddress");
    final String filename = cfg.getString("filename");
    final String momongodeployid = cfg.getString("mongodeployid");

    vertx.eventBus().registerHandler(address, new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> message) {
        try {
          final String thisDeployId = message.body();
          container.logger().info(thisDeployId + " deployed");
          Thread.sleep(10000);
          //这里可以最后验证一下条目是否相符。
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
