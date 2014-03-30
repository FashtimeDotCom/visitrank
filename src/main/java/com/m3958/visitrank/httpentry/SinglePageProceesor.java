package com.m3958.visitrank.httpentry;

import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.ResponseGenerator;
import com.m3958.visitrank.Utils.HostExtractor;
import com.m3958.visitrank.logger.AppLogger;
import com.m3958.visitrank.rediscmd.GET;
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

  private boolean record;

  private String referer;

  public SinglePageProceesor(EventBus eb, HttpServerRequest req, Logger log, boolean record,
      String referer) {
    this.eb = eb;
    this.req = req;
    this.log = log;
    this.record = record;
    this.referer = referer;
  }

  public void process() {
    String referermd5 = DigestUtils.md5Hex(referer);
    JsonObject cmd;
    if (record) {
      cmd = new INCR(referermd5).getCmd();
    } else {
      cmd = new GET(referermd5).getCmd();
    }

    // redis incr
    this.eb.send(AppConstants.MOD_REDIS_ADDRESS, cmd, new Handler<Message<JsonObject>>() {
      public void handle(Message<JsonObject> message) {
        JsonObject redisResultBody = message.body();
        if ("ok".equals(redisResultBody.getString("status"))) {
          if (record) {
            JsonObject pjo = getParamsHeadersOb(req);
            AppLogger.urlPersistor.info(pjo);
            pjo.removeField("record");
            increateSiteCounter();
          }

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

      private void increateSiteCounter() {
        String host = HostExtractor.getHost(referer);
        JsonObject wholeSiteCountCmd = new INCR(host).getCmd();
        SinglePageProceesor.this.eb.send(AppConstants.MOD_REDIS_ADDRESS, wholeSiteCountCmd);
      }
    });
  }

  private JsonObject getParamsHeadersOb(HttpServerRequest req) {
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
    jo.putNumber("ts", new Date().getTime()).putObject("headers", headerJo);

    return jo;
  }

}
