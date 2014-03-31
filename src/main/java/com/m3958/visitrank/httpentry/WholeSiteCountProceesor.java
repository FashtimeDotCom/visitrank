package com.m3958.visitrank.httpentry;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.ResponseGenerator;
import com.m3958.visitrank.Utils.HostExtractor;
import com.m3958.visitrank.rediscmd.GET;
import com.m3958.visitrank.rediscmd.INCR;

public class WholeSiteCountProceesor {

  private HttpServerRequest req;

  private Logger log;

  private EventBus eb;

  private boolean record;

  private String referer;

  public WholeSiteCountProceesor(EventBus eb, HttpServerRequest req, Logger log, boolean record,
      String referer) {
    this.eb = eb;
    this.req = req;
    this.log = log;
    this.record = record;
    this.referer = referer;
  }

  public void process() {

    String host = HostExtractor.getHost(referer);

    JsonObject wholeSiteCountCmd;
    if (record) {
      wholeSiteCountCmd = new INCR(host).getCmd();
    } else {
      wholeSiteCountCmd = new GET(host).getCmd();
    }

    this.eb.send(AppConstants.MOD_REDIS_ADDRESS, wholeSiteCountCmd,
        new Handler<Message<JsonObject>>() {
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
