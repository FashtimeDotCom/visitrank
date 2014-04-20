package com.m3958.visitrank;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.AppUtils;

public class AppConfigVerticle extends Verticle {

  public static String VERTICLE_NAME = AppConfigVerticle.class.getName();

  public static String VERTICLE_ADDRESS = "app-config";

  public JsonObject appConfigJson;

  @Override
  public void start() {
    Logger log = container.logger();
    appConfigJson = AppUtils.loadJsonResourceContent(this.getClass(), "conf.json");
    final EventBus eb = vertx.eventBus();
    eb.registerHandler(VERTICLE_ADDRESS, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        JsonObject body = message.body();
        String action = body.getString("action", "");

        if (action.isEmpty()) {
          message.reply(appConfigJson);
        }

      }
    });
    log.info("AppConfigVerticle started");
  }
}
