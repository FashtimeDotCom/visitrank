package com.m3958.visitrank.httpentry;

import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.ResponseGenerator;
import com.m3958.visitrank.SaveToMongoVerticle;
import com.m3958.visitrank.logger.AppLogger;
import com.m3958.visitrank.rediscmd.INCR;

/**
 * This class handle persiste record to mongodb and return proper content to client. referer head is
 * a must.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class RecordAndStatisticsProceesor {

  private HttpServerRequest req;

  private Logger log;

  private EventBus eb;

  public RecordAndStatisticsProceesor(EventBus eb, HttpServerRequest req, Logger log) {
    this.eb = eb;
    this.req = req;
    this.log = log;
  }

  public void process() {
    String referer = req.headers().get("referer");

    if (referer == null || referer.isEmpty()) {
      new ResponseGenerator(req, "0").sendResponse();
    } else {
      final JsonObject pjo = getParamsHeadersOb(req);
      if (pjo == null) {
        new ResponseGenerator(req, "0").sendResponse();
      } else {
        String referermd5 = DigestUtils.md5Hex(referer);

        JsonObject incrCmd = new INCR(referermd5).getCmd();
        // redis incr
        this.eb.send(AppConstants.MOD_REDIS_ADDRESS, incrCmd, new Handler<Message<JsonObject>>() {
          public void handle(Message<JsonObject> message) {
            JsonObject redisResultBody = message.body();
            if ("ok".equals(redisResultBody.getString("status"))) {

              // save to mongodb
              // RecordAndStatisticsProceesor.this.eb.send(SaveToMongoVerticle.RECEIVER_ADDR, pjo);
              AppLogger.urlPersistor.info(pjo);
              pjo.removeField("record");
              String out = req.params().get("out");

              if ("wholesite".equals(out)) {
                new WholeSiteCountProceesor(eb, req, log).process();
              } else { // default return this referer's count
                String value = String.valueOf(redisResultBody.getLong("value"));
                new ResponseGenerator(req, value).sendResponse();
              }
            } else {
              log.info(redisResultBody.getString("message"));
              new ResponseGenerator(req, "0").sendResponse();
            }
          }
        });
      }
    }
  }

  private JsonObject getParamsHeadersOb(HttpServerRequest req) {
    MultiMap mm = req.params();
    String siteid = mm.get("siteid");

    if (siteid == null || siteid.isEmpty()) {
      return null;
    }

    JsonObject jo = new JsonObject();

    JsonObject headerJo = new JsonObject();

    for (Map.Entry<String, String> header : req.headers().entries()) {
      String key = header.getKey();
      String value = header.getValue();
      if ("referer".equalsIgnoreCase(key)) {
        jo.putString("url", value);
      } else {
        headerJo.putString(key, value);
      }
    }

    for (Map.Entry<String, String> param : req.params().entries()) {
      String key = param.getKey();
      String value = param.getValue();
      jo.putString(key, value);
    }
    headerJo.putString("ip", req.remoteAddress().getAddress().getHostAddress());
    jo.putString("siteid", siteid);
    jo.putNumber("ts", new Date().getTime()).putObject("headers", headerJo);

    return jo;
  }

}
