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

import java.util.Date;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/*
 * This is a simple Java verticle which receives `ping` messages on the event bus and sends back
 * `pong` replies
 */
public class SaveToMongoVerticle extends Verticle {

  public static String RECEIVER_ADDR = "visitrank-mongo-receiver";

  public void start() {
    final EventBus eb = vertx.eventBus();
    final Logger log = container.logger();
    
//    jo.putString("siteid", siteid).putString("catid", catid).putNumber("ts", new Date().getTime())
//    .putString("title", mm.get("title")).putString("ip", ip).putObject("headers", headerJo);
    
    eb.registerHandler(RECEIVER_ADDR, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        JsonObject body = message.body();
        //find site
        JsonObject findSiteCmd = new SiteFinder(body.getString("siteid")).getCmd();
        
        eb.send(CounterVerticle.MOD_MONGO_PERSIST_ADDRESS, findSiteCmd, new Handler<Message<JsonObject>>() {
          public void handle(Message<JsonObject> message) {
            JsonObject body = message.body();
            if ("ok".equals(body.getString("status"))) {
              JsonArray ja = body.getArray("results");
              if(ja.size() > 0){
                JsonObject siteJo = ja.get(0);
                log.info(siteJo);
              }
            } else {
              System.out.println(body.getString("message"));
            }
          }
        });
      }
    });
    container.logger().info("SaveToMongoVerticle started");

  }
  
  public static class SiteFinder{
    
    private String siteid;
    public SiteFinder(String siteid){
      this.siteid = siteid;
    }
    
    public JsonObject getCmd(){
      JsonObject jo = new JsonObject();
      JsonObject matcherJo = new JsonObject();
      matcherJo.putString("_id", this.siteid);
      jo.putString("action", "find");
      jo.putString("collection", "site");
      jo.putObject("matcher", matcherJo);
      return jo;
    }
  }
}
