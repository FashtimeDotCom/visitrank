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
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.rediscmd.INCR;

/**
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class CounterVerticle extends Verticle {

  public static String MOD_REDIS_ADDRESS = "visit_counter.redis";
  public static String MOD_MONGO_PERSIST_ADDRESS = "visit_counter.mongodb";
  
  public static int HTTP_PORT = 8333;
  public static int REDIS_PORT = 6379;
  public static int MONGODB_PORT = 27017;

  public void start() {
    
    JsonObject config = container.config();
    
    int http_port = config.getInteger("httpport", 0);
    
    if(http_port == 0){
      http_port = HTTP_PORT;
    }
    
    int redis_port = config.getInteger("redisport", 0);
    
    if(redis_port == 0){
      redis_port = REDIS_PORT;
    }
    
    int mongodb_port = config.getInteger("mongodbport", 0);
    
    if(mongodb_port == 0){
      mongodb_port = MONGODB_PORT;
    }
    
    
    final Logger log = container.logger();
    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {

      public void handle(final HttpServerRequest req) {
        MultiMap mm = req.params();
        String out = mm.get("out");

        // out, output,possible values:
        // pagecount(default),sitecount,catcount,sitehotest,cathotest,none
        if ("sitecount".equals(out)) {

        } else if ("catcount".equals(out)) {

        } else if ("sitehotest".equals(out)) {

        } else if ("cathotest".equals(out)) {

        } else {
          processPageCount(log, req);
        }
      }
    }).listen(http_port);

    // deploy redis
    JsonObject redisCfg = new JsonObject();
    redisCfg.putString("address", MOD_REDIS_ADDRESS).putString("host", "127.0.0.1")
        .putString("encodeing", "UTF-8").putNumber("port", redis_port);

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
        .putString("db_name", "visitrank").putNumber("port", mongodb_port);

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

  public JsonObject getParamsHeadersOb(HttpServerRequest req) {
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

    jo.putNumber("ts", new Date().getTime()).putObject("headers", headerJo);

    return jo;
  }

  private void processPageCount(final Logger log, final HttpServerRequest req) {
    String referer = req.headers().get("referer");

    if (referer == null || referer.isEmpty()) {
      new ResponseGenerator(req, "0").sendResponse();
    } else {
      final JsonObject pjo = getParamsHeadersOb(req);
      if (pjo == null) {
        new ResponseGenerator(req, "0").sendResponse();
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

// vertx runmod com.m3958~visitrank~0.0.1-SNAPSHOT
// office fa5f2e1d-092a-4c8c-9518-f5b7600f8f80
// curl --referer http://www.example.com
// http://localhost:8333?siteid=fa5f2e1d-092a-4c8c-9518-f5b7600f8f80
// vertx runzip target/visitrank-0.0.1-SNAPSHOT-mod.zip


