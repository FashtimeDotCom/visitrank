package com.m3958.visitrank;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.rediscmd.INCR;

public class CounterVerticle extends Verticle {

  public static String counterRedisAddress = "visit_counter.redis";
  public static String counterMongoDbAddress = "visit_counter.mongodb";

  public JsonObject getParamsOb(HttpServerRequest req) {
    MultiMap mm = req.params();
    String siteid = mm.get("siteid");

    if (siteid == null || siteid.isEmpty()) {
      return null;
    }

    String catid = mm.get("catid");
    String ip = getClientIpAddr(req);

    JsonObject jo = new JsonObject();

    jo.putString("siteid", siteid).putString("catid", catid).putString("ip", ip)
        .putNumber("ts", new Date().getTime()).putString("title", mm.get("title"));

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
    final Logger log = container.logger();
    final EventBus eb = vertx.eventBus();
    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {

      public void handle(final HttpServerRequest req) {
        String referer = req.headers().get("referer");
        final HttpServerResponse resp = req.response();


        System.out.println(referer);
        if (referer == null || referer.isEmpty()) {
          resp.end(callback(req, "0"));
        } else {
          final JsonObject pjo = getParamsOb(req);
          if (pjo == null) {
            resp.end(callback(req, "0"));
          } else {
            String referermd5 = DigestUtils.md5Hex(referer);
            log.info(referermd5);
            // eb.send(SaveToMongoVerticle.RECEIVER_ADDR, pjo);
            // log.info(SaveToMongoVerticle.RECEIVER_ADDR + " sended.");
            JsonObject msg = new INCR(referermd5).getCmd();
            log.info(msg);
            eb.send(counterRedisAddress, msg,
                new Handler<Message<JsonObject>>() {
                  public void handle(Message<JsonObject> message) {
                    if ("ok".equals(message.body().getString("status"))) {
                      String value = String.valueOf(message.body().getLong("value"));
                      System.out.println(value);
                      // use publish pattern,no wait.
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

    // deploy redis
    JsonObject counterRedisCfg = new JsonObject();
    counterRedisCfg.putString("address", counterRedisAddress).putString("host", "10.74.111.20")
        .putString("encodeing", "UTF-8").putNumber("port", 6379);

    container.deployModule("io.vertx~mod-redis~1.1.3", counterRedisCfg, 1,
        new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            if (asyncResult.succeeded()) {
              System.out.println("redis module has successly deployed:" + asyncResult.result());
            } else {
              System.out.println("redis module deploy failure.");
            }
          }
        });

    // deploy mongodb
    JsonObject counterMongodbCfg = new JsonObject();
    counterRedisCfg.putString("address", counterMongoDbAddress).putString("host", "localhost")
        .putString("db_name", "visitrank").putNumber("port", 27017);

    container.deployModule("io.vertx~mod-mongo-persistor~2.1.1", counterMongodbCfg, 1,
        new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            if (asyncResult.succeeded()) {
              System.out.println("mongo-persistor module has successly deployed:"
                  + asyncResult.result());
            } else {
              System.out.println("mongo-persistor module deploy failure.");
            }
          }
        });

    container.deployVerticle("com.m3958.visitrank.SaveToMongoVerticle", 1);
  }
}

// vertx runmod com.m3958~visitrank~0.0.1-SNAPSHOT
// curl --referer http://www.example.com http://localhost:8333?siteid=xxx
