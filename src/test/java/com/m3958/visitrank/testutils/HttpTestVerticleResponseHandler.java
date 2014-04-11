package com.m3958.visitrank.testutils;

import static org.vertx.testtools.VertxAssert.testComplete;

import java.util.Map;

import org.junit.Assert;
import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;

public class HttpTestVerticleResponseHandler implements Handler<HttpClientResponse> {
  private Logger log;

  public HttpTestVerticleResponseHandler(Container container) {
    this.log = container.logger();
  }

  @Override
  public void handle(HttpClientResponse resp) {
    log.info("Got a response: " + resp.statusCode());
    log.info("Got a message: " + resp.statusMessage());
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> header : resp.headers().entries()) {
      sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
    }
    log.info("Headers: " + sb);

    final Buffer body = new Buffer(0);
    final HttpClientResponse respf = resp;
    resp.dataHandler(new Handler<Buffer>() {
      public void handle(Buffer data) {
        body.appendBuffer(data);
      }
    });
    resp.endHandler(new VoidHandler() {
      public void handle() {
        // The entire response body has been received
        log.info("The total body received was " + body.length() + " bytes");
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> header : respf.trailers().entries()) {
          sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
        }
        log.info("Trailers: " + sb);
        Assert.assertEquals(String.valueOf(body.length()), respf.trailers().get("total-send-bytes"));
        testComplete();
      }
    });
  }
}
