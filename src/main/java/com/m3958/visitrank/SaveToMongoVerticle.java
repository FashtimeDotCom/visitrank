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

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.mongocmd.VisitMongoCmd;
import com.m3958.visitrank.rediscmd.INCR;

public class SaveToMongoVerticle extends Verticle {

  public static String RECEIVER_ADDR = "visitrank-mongo-receiver";

  public void start() {
    final EventBus eb = vertx.eventBus();
//    final Logger log = container.logger();

    eb.registerHandler(RECEIVER_ADDR, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        JsonObject body = message.body();
        String siteid = body.getString("siteid");
//        log.info(body);
        eb.send(AppConstants.MOD_MONGO_PERSIST_ADDRESS, new VisitMongoCmd(body).saveCmd());
        eb.send(AppConstants.MOD_REDIS_ADDRESS, new INCR(siteid).getCmd());
      }
    });
    container.logger().info("SaveToMongoVerticle started");

  }
}
