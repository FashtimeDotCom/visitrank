package com.m3958.visitrank;

import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

public class ResponseGenerator {

  
  private static long fiveMinutesInMilli = 5L * 60 * 1000L;
  
  private static long fiveMinutesInInSeconds = 300L;
  
  private HttpServerRequest req;

  private String result;

  public ResponseGenerator(HttpServerRequest req, String result) {
    this.req = req;
    this.result = result;
  }

  // ot, output type,possible values: text(default),documentwrite
  public void sendResponse() {
    final HttpServerResponse resp = req.response();
    resp.headers().set("Content-Type", "application/javascript; charset=UTF-8");
    MultiMap mm = req.params();

    String out = mm.get("out");

    // out, output,possible values:
    // pagecount(default),sitecount,catcount,sitehotest,cathotest,none
    if ("sitecount".equals(out)) {
      resp.headers().set("Expires", String.valueOf(System.currentTimeMillis() + fiveMinutesInMilli));
      resp.headers().set("Cache-Control", "max-age="+ fiveMinutesInInSeconds);
    } else if ("catcount".equals(out)) {

    } else if ("sitehotest".equals(out)) {

    } else if ("cathotest".equals(out)) {

    } else {
      resp.headers().set("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
      resp.headers().set("Pragma", "no-cache"); // HTTP 1.0.
      resp.headers().set("Expires", "0"); // Proxies.
    }

    String tobesend;
    String cb = mm.get("callback");
    if (!(cb == null || cb.isEmpty())) {
      tobesend = cb + "(" + result + ");";
    } else {
      String ot = mm.get("ot");
      if ("documentwrite".equals(ot)) {
        tobesend = "document.write('" + result + "')";
      } else {
        tobesend = result;
      }
      resp.end(tobesend);
    }
  }

}
