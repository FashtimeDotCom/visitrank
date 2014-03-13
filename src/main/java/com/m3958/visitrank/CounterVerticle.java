package com.m3958.visitrank;

import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.rediscmd.INCR;

public class CounterVerticle extends Verticle {

  public static String MOD_REDIS_ADDRESS = "visit_counter.redis";
  public static String MOD_MONGO_PERSIST_ADDRESS = "visit_counter.mongodb";

  public JsonObject getParamsOb(HttpServerRequest req) {
    MultiMap mm = req.params();
    String siteid = mm.get("siteid");

    if (siteid == null || siteid.isEmpty()) {
      return null;
    }

    String catid = mm.get("catid");

    // String ip = getClientIpAddr(req);

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
    headerJo.putString("ip", req.remoteAddress().getAddress().getHostAddress());

    jo.putString("siteid", siteid).putString("catid", catid).putNumber("ts", new Date().getTime())
        .putString("title", mm.get("title")).putObject("headers", headerJo);

    return jo;
  }

  // public String getClientIpAddr(HttpServerRequest req) {
  // MultiMap mm = req.headers();
  // String ip = mm.get("X-Forwarded-For");
  // if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
  // ip = mm.get("Proxy-Client-IP");
  // }
  // if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
  // ip = mm.get("WL-Proxy-Client-IP");
  // }
  // if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
  // ip = mm.get("HTTP_CLIENT_IP");
  // }
  // if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
  // ip = mm.get("HTTP_X_FORWARDED_FOR");
  // }
  // if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
  // ip = req.remoteAddress().getAddress().getHostAddress();
  // }
  // return ip;
  // }

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
    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {

      public void handle(final HttpServerRequest req) {
        String referer = req.headers().get("referer");
        final HttpServerResponse resp = req.response();

        if (referer == null || referer.isEmpty()) {
          resp.end(callback(req, "0"));
        } else {
          final JsonObject pjo = getParamsOb(req);
          if (pjo == null) {
            resp.end(callback(req, "0"));
          } else {
            String referermd5 = DigestUtils.md5Hex(referer);

            JsonObject msg = new INCR(referermd5).getCmd();
            vertx.eventBus().send(MOD_REDIS_ADDRESS, msg, new Handler<Message<JsonObject>>() {
              public void handle(Message<JsonObject> message) {
                JsonObject redisResultBody = message.body();
                if ("ok".equals(redisResultBody.getString("status"))) {
                  String value = String.valueOf(redisResultBody.getLong("value"));
                  // use publish pattern,no wait.
                  vertx.eventBus().send(SaveToMongoVerticle.RECEIVER_ADDR, pjo);
                  resp.end(callback(req, value));
                } else {
                  log.info(redisResultBody.getString("message"));
                  resp.end(callback(req, "0"));
                }
              }
            });
          }
        }
      }
    }).listen(8333);

    // deploy redis
    JsonObject redisCfg = new JsonObject();
    redisCfg.putString("address", MOD_REDIS_ADDRESS).putString("host", "127.0.0.1")
        .putString("encodeing", "UTF-8").putNumber("port", 6379);

    container.deployModule("io.vertx~mod-redis~1.1.3", redisCfg, 1,
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
    JsonObject mongodbCfg = new JsonObject();
    mongodbCfg.putString("address", MOD_MONGO_PERSIST_ADDRESS).putString("host", "localhost")
        .putString("db_name", "visitrank").putNumber("port", 27017);

    container.deployModule("io.vertx~mod-mongo-persistor~2.1.1", mongodbCfg, 1,
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

    container.deployVerticle("mapreduce_verticle.js", 1);
  }
}

// vertx runmod com.m3958~visitrank~0.0.1-SNAPSHOT
// office fa5f2e1d-092a-4c8c-9518-f5b7600f8f80
// curl --referer http://www.example.com
// http://localhost:8333?siteid=fa5f2e1d-092a-4c8c-9518-f5b7600f8f80
// vertx runzip target/visitrank-0.0.1-SNAPSHOT-mod.zip
