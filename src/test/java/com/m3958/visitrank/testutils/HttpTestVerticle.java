package com.m3958.visitrank.testutils;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.integration.java.TestConstants;

/**
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class HttpTestVerticle extends Verticle {

  public static String VERTICLE_NAME = "com.m3958.visitrank.testutils.HttpTestVerticle";

  public void start() {
    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
      public void handle(final HttpServerRequest req) {
        String dowhat = req.params().get("dowhat");
        if("url".equals(dowhat)){
          req.response().end(req.absoluteURI().toString());
        }
      }
    }).listen(TestConstants.HTTP_PORT);

    container.logger().info("HttpTestVerticle started");
  }
}
