package com.m3958.visitrank;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.file.FileSystem;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.AppUtils;

public class AppConfigVerticle extends Verticle {

  public static String VERTICLE_NAME = AppConfigVerticle.class.getName();

  public static String VERTICLE_ADDRESS = "app-config";

  public JsonObject appConfig;


  @Override
  public void start() {
    final EventBus eb = vertx.eventBus();
    eb.registerHandler(VERTICLE_ADDRESS, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        JsonObject body = message.body();
        String action = body.getString("action", "");

        if (action.isEmpty()) {
          if (appConfig == null) {
            initConfig();
          }
          message.reply(appConfig);
        }

      }
    });
    container.logger().info("LogSaverVerticle started");

  }

  private synchronized void initConfig() {
    FileSystem fs = vertx.fileSystem();
    if (fs.existsSync("conf.json")) {
      Buffer bf = fs.readFileSync("conf.json");
      appConfig = new JsonObject(bf.toString("UTF-8"));
    } else {
      appConfig = new JsonObject(AppUtils.loadResourceContent(this.getClass(), "conf.json"));
    }
  }
}
