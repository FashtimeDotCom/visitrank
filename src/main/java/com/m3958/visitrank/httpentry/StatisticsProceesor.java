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
import com.m3958.visitrank.rediscmd.GET;

/**
 * This class only handle return proper content to client. Not change any status on server. referer
 * head is optional when out is wholesite,
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class StatisticsProceesor {

  private HttpServerRequest req;

  private Logger log;

  private EventBus eb;

  public StatisticsProceesor(EventBus eb, HttpServerRequest req, Logger log) {
    this.eb = eb;
    this.req = req;
    this.log = log;
  }

  public void process() {

    String out = req.params().get("out");

    if ("wholesite".equals(out)) {
      new WholeSiteCountProceesor(eb, req, log).process();
    } else {
      String referer = req.headers().get("referer");
      if (referer == null || referer.isEmpty()) {
        new ResponseGenerator(req, "0").sendResponse();
      } else {
        String referermd5 = DigestUtils.md5Hex(referer);
        JsonObject getCmd = new GET(referermd5).getCmd();
        // redis incr
        this.eb.send(AppConstants.MOD_REDIS_ADDRESS, getCmd, new Handler<Message<JsonObject>>() {
          public void handle(Message<JsonObject> message) {
            JsonObject redisResultBody = message.body();
            if ("ok".equals(redisResultBody.getString("status"))) {
              String value;
              try{
                value = redisResultBody.getString("value");
              }catch(Exception e){
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
  }

}
