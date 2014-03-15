package com.m3958.visitrank.httpentry;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.ResponseGenerator;
import com.m3958.visitrank.rediscmd.GET;

public class CatCountProceesor {

  private HttpServerRequest req;

  private Logger log;

  private EventBus eb;

  public CatCountProceesor(EventBus eb, HttpServerRequest req, Logger log) {
    this.eb = eb;
    this.req = req;
    this.log = log;
  }

  public void process() {
    MultiMap mm = req.params();
    String siteid = mm.get("siteid");

    if (siteid == null || siteid.isEmpty()) {
      new ResponseGenerator(req, "0").sendResponse();
      return;
    }

    String catid = mm.get("catid");

    if (catid == null || catid.isEmpty()) {
      new ResponseGenerator(req, "0").sendResponse();
      return;
    }

    JsonObject msg = new GET(siteid + catid).getCmd();
    this.eb.send(AppConstants.MOD_REDIS_ADDRESS, msg, new Handler<Message<JsonObject>>() {
      public void handle(Message<JsonObject> message) {
        JsonObject redisResultBody = message.body();
        if ("ok".equals(redisResultBody.getString("status"))) {
          String value;
          try {
            value = redisResultBody.getString("value");
          } catch (Exception e) {
            value = String.valueOf(redisResultBody.getLong("value"));
          }
          new ResponseGenerator(req, value).sendResponse();
        } else {
          log.info(redisResultBody.getString("message"));
          new ResponseGenerator(req, "0").sendResponse();
        }
      }
    });
  }
}
