package com.m3958.visitrank.httpentry;

import org.apache.commons.codec.digest.DigestUtils;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.ResponseGenerator;
import com.m3958.visitrank.rediscmd.INCR;

/**
 * This class handle persiste record to mongodb and return proper content to client. referer head is
 * a must.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class SinglePageProceesor {

  private HttpServerRequest req;

  private Logger log;

  private EventBus eb;


  private String referer;

  public SinglePageProceesor(EventBus eb, HttpServerRequest req, Logger log, String referer) {
    this.eb = eb;
    this.req = req;
    this.log = log;
    this.referer = referer;
  }

  public void process() {
    String referermd5 = DigestUtils.md5Hex(referer);
    JsonObject cmd;
    cmd = new INCR(referermd5).getCmd();

    this.eb.send(AppConstants.MOD_REDIS_ADDRESS, cmd, new Handler<Message<JsonObject>>() {
      public void handle(Message<JsonObject> message) {
        JsonObject redisResultBody = message.body();
        if ("ok".equals(redisResultBody.getString("status"))) {
          String value;
          try {
            value = String.valueOf(redisResultBody.getLong("value"));
          } catch (Exception e) {
            value = redisResultBody.getString("value");
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
