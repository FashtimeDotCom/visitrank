package com.m3958.visitrank;


import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.httpentry.SinglePageProceesor;
import com.m3958.visitrank.httpentry.WholeSiteCountProceesor;
import com.m3958.visitrank.interf.TestableVerticle;

/**
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class CounterVerticle extends Verticle implements TestableVerticle {

  public static String VERTICLE_NAME = CounterVerticle.class.getName();

  /**
   * 如果默认是referer的主机名，那么siteid将不再是必须，反而可以引入sitegroup概念，从而增加系统的灵活性。
   */
  public void start() {
    if (!AppUtils.deployTestableVerticle(this, container)) {
      vertx.eventBus().send(AppConfigVerticle.VERTICLE_ADDRESS, new JsonObject(),
          new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> msg) {
              AppConfig gcfg = new AppConfig(msg.body(), true);
              deployMe(gcfg);
            }
          });
      container.logger().info("CounterVerticle started");
    }
  }

  public void deployMe(final AppConfig appconfig) {
    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
      public void handle(final HttpServerRequest req) {
        MultiMap mm = req.params();
        String out = mm.get("out");
        String referer = req.headers().get("referer");

        boolean noReferer = (referer == null || referer.isEmpty());

        EventBus eb = vertx.eventBus();
        Logger log = container.logger();

        if ("wholesite".equals(out)) {
          if (noReferer) {
            new ResponseGenerator(req, "0").sendResponse();
          } else {
            new WholeSiteCountProceesor(appconfig, eb, req, log, referer).process();
          }
        } else {
          if (noReferer) {
            new ResponseGenerator(req, "0").sendResponse();
            return;
          } else {
            new SinglePageProceesor(appconfig, eb, req, log, referer).process();
          }
        }
      }
    }).listen(appconfig.getHttpPort());
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
// http://localhost:8333?siteid=c0f36d3e-00a4-4139-882c-022c8034f58d
// vertx runzip target/visitrank-0.0.1-SNAPSHOT-mod.zip
// vertx runmod com.m3958~visitrank~0.0.3-SNAPSHOT -conf conf.json
// -Dmaven.test.skip=true
// db.collection.getIndexes()
