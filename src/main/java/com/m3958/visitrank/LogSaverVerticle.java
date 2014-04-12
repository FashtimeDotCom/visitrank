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
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.LogItem;
import com.m3958.visitrank.logger.AppLogger;
import com.m3958.visitrank.uaparser.Parser;

public class LogSaverVerticle extends Verticle {

  public static String RECEIVER_ADDR = "log-saver";
  
  public static String VERTICLE_NAME = LogSaverVerticle.class.getName();

  public void start() {
    final EventBus eb = vertx.eventBus();
    try {
      final Parser uaparser = new Parser();
      eb.registerHandler(RECEIVER_ADDR, new Handler<Message<JsonObject>>() {
        @Override
        public void handle(Message<JsonObject> message) {
          JsonObject jo = new LogItem(message.body()).transform(uaparser);
          AppLogger.urlPersistor.info(jo);
        }
      });
      container.logger().info("LogSaverVerticle started");
    } catch (IOException e) {
      container.logger().error("LogSaverVerticle failure");
    }
  }
}
