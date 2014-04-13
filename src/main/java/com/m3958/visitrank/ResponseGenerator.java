package com.m3958.visitrank;

import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

public class ResponseGenerator {

  private static long fiveMinutesInMilli = 5L * 60 * 1000L;

  private static long fiveMinutesInInSeconds = 300L;

  private HttpServerRequest req;

  private String result;

  // (function(){
  // var enc = encodeURIComponent,
  // turl = "http://www.fhfl.org.cn/referer1.ftl?out=wholesite",
  // pr = enc(document.referrer),
  // src = turl + "&pr=" + pr;
  // document.write('<scr' + 'ipt type="text/javascript" src="' + src + '"></scr' + 'ipt>');
  // })();
  private String scriptStr =
      "(function(){"
          + "var enc = encodeURIComponent,"
          + "turl = \"%s\","
          + "pr = enc(document.referrer),"
          + "src = turl + \"&pr=\" + pr;"
          + "document.write('<scr' + 'ipt  type=\"text/javascript\" src=\"' + src + '\"></scr' + 'ipt>');"
          + "})();";

  public ResponseGenerator(HttpServerRequest req, String result) {
    this.req = req;
    this.result = result;
  }

  // ot, output type,possible values: text(default),documentwrite
  public void sendResponse() {
    final HttpServerResponse resp = req.response();
    resp.headers().set("Content-Type", "application/javascript; charset=UTF-8");
    MultiMap mm = req.params();

    // String record = mm.get("record");
    // if(record == null || record.isEmpty()){
    // resp.headers()
    // .set("Expires", String.valueOf(System.currentTimeMillis() + fiveMinutesInMilli));
    // resp.headers().set("Cache-Control", "max-age=" + fiveMinutesInInSeconds);
    // }else{
    resp.headers().set("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
    resp.headers().set("Pragma", "no-cache"); // HTTP 1.0.
    resp.headers().set("Expires", "0"); // Proxies.
    // }

    StringBuilder tobesend = new StringBuilder();
    String cb = mm.get("callback");
    String domid = mm.get("domid");
    String silent = mm.get("silent");
    String out = mm.get("out");
    // default is documentwrite.
    if (!(silent == null || silent.isEmpty())) {
      tobesend.append("");
    } else if (!(cb == null || cb.isEmpty())) {
      tobesend.append(cb).append("(").append(result).append(");");
    } else if (!(domid == null || domid.isEmpty())) {
      tobesend.append("(function(){var domid=document.getElementById(\"").append(domid)
          .append("\").innerHTML = ").append(result).append(";})();");
    } else {
      tobesend.append("document.write(").append(result).append(");");
    }

    if ("wholesite".equals(out)) {
      tobesend.append(String.format(scriptStr, req.absoluteURI().toString()));
    }
    resp.end(tobesend.toString());
  }
}
