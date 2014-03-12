package com.m3958.visitrank;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.rediscmd.INCR;

public class CounterVerticle extends Verticle {

  public static String counterRedisAddress = "visit_counter.redis";

  public JsonObject getParamsOb(HttpServerRequest req) {
    String siteid = req.params().get("siteid");

    if (siteid == null || siteid.isEmpty()) {
      return null;
    }

    String catid = req.params().get("catid");
    String ip = getClientIpAddr(req);

    JsonObject jo = new JsonObject();

    jo.putString("siteid", siteid).putString("catid", catid).putString("ip", ip)
        .putNumber("ts", new Date().getTime());

    return jo;
  }

  public String getClientIpAddr(HttpServerRequest req) {
    MultiMap mm = req.headers();
    String ip = mm.get("X-Forwarded-For");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = mm.get("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = mm.get("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = mm.get("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = mm.get("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = req.remoteAddress().getAddress().getHostAddress();
    }
    return ip;
  }

  public String callback(HttpServerRequest req, String v) {
    String cb = req.params().get("callback");
    if (cb == null || cb.isEmpty()) {
      return v;
    } else {
      return cb + "(" + v + ");";
    }
  }

  public void start() {

    final EventBus eb = vertx.eventBus();
    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {

      public void handle(final HttpServerRequest req) {
        String referer = req.headers().get("referer");
        final HttpServerResponse resp = req.response();


        System.out.println(referer);
        if (referer == null || referer.isEmpty()) {
          resp.end(callback(req, "0"));
        } else {
          JsonObject pjo = getParamsOb(req);
          if (pjo == null) {
            resp.end(callback(req, "0"));
          } else {
            System.out.println(pjo.toString());
            String referermd5 = DigestUtils.md5Hex(referer);
            eb.send(counterRedisAddress, new INCR(referermd5).getCmd(),
                new Handler<Message<JsonObject>>() {
                  public void handle(Message<JsonObject> message) {
                    if ("ok".equals(message.body().getString("status"))) {
                      String value = String.valueOf(message.body().getLong("value"));
                      System.out.println(value);
                      resp.end(callback(req, value));
                    } else {
                      System.out.println(message.body().getString("message"));
                      resp.end(callback(req, "0"));
                    }
                  }
                });
          }
        }
      }
    }).listen(8333);

    JsonObject counterRedisCfg = new JsonObject();
    counterRedisCfg.putString("address", counterRedisAddress).putString("host", "10.74.111.20")
        .putString("encodeing", "UTF-8").putNumber("port", 6379);

    container.deployModule("io.vertx~mod-redis~1.1.3", counterRedisCfg, 1);
  }
}

// vertx runmod com.m3958~visitrank~0.0.1-SNAPSHOT
