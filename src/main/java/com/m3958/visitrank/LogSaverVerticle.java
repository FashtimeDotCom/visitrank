package com.m3958.visitrank;

/*
 * Copyright 2013 Red Hat, Inc.
 * 
 * Red Hat licenses this file to you under the Apache License, version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import java.io.IOException;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.LogItemTransformer;
import com.m3958.visitrank.logger.AppLogger;
import com.m3958.visitrank.uaparser.Parser;

public class LogSaverVerticle extends Verticle {

  public static String VERTICLE_ADDRESS = "log-saver";

  public static String VERTICLE_NAME = LogSaverVerticle.class.getName();

  public void start() {
    vertx.eventBus().send(AppConfigVerticle.VERTICLE_ADDRESS, new JsonObject(),
        new Handler<Message<JsonObject>>() {
          @Override
          public void handle(Message<JsonObject> msg) {
            final AppConfig gcfg = new AppConfig(msg.body(), true);
            deployMe(gcfg);
          }
        });
  }

  private void deployMe(final AppConfig appConfig) {
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
