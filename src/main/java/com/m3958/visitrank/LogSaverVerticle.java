package com.m3958.visitrank;

/*
 * 
 * @author jianglibo@gmail.com
 */

import java.io.IOException;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.LogItemTransformer;
import com.m3958.visitrank.logger.AppLogger;
import com.m3958.visitrank.uaparser.Parser;

public class LogSaverVerticle extends Verticle {

  public static String VERTICLE_ADDRESS = "log-saver";

  public static String VERTICLE_NAME = LogSaverVerticle.class.getName();

  public void start() {
    final Logger log = container.logger();
    final AppConfig appConfig = new AppConfig(container.config(), false);
    deployMe(appConfig, log);
  }

  private void deployMe(final AppConfig appConfig,Logger log) {
    try {
      final Parser uaparser = new Parser();
      vertx.eventBus().registerHandler(VERTICLE_ADDRESS, new Handler<Message<JsonObject>>() {
        @Override
        public void handle(Message<JsonObject> message) {
          JsonObject jo =
              new LogItemTransformer(appConfig).transformToLog4j(message.body(), uaparser);
          AppLogger.urlPersistor.info(jo);
        }
      });
      container.logger().info("LogSaverVerticle started");
    } catch (IOException e) {
      container.logger().error("LogSaverVerticle failure");
    }
  }
}
