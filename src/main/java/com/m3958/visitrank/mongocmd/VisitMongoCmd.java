package com.m3958.visitrank.mongocmd;

/**
 * { "_id" : "5b41bc31-3a83-437e-8486-6b67a4bf3698", "urlid" :
 * "5b41bc31-3a83-437e-8486-6b67a4bf3698" , "siteid" : "fa5f2e1d-092a-4c8c-9518-f5b7600f8f80",
 * "catid" : null, "ts" : Nu mberLong("1394684081338"), "title" : null, "ip" : "127.0.0.1",
 * "headers" : { "Us er-Agent" : "curl/7.23.1 (x86_64-pc-win32) libcurl/7.23.1 OpenSSL/0.9.8r
 * zlib/1. 2.5", "Host" : "localhost:8333", "Accept" : "*\/*" } }
 * 
 * @author jianglibo@gmail.com
 * 
 */
import org.vertx.java.core.json.JsonObject;

public class VisitMongoCmd {
  private JsonObject requestJso;
  private String pageUrl;

  public VisitMongoCmd(JsonObject requestJso, String pageUrl) {
    this.requestJso = requestJso;
    this.pageUrl = pageUrl;
  }

  public VisitMongoCmd() {}

  public JsonObject saveCmd() {
    JsonObject jo = new JsonObject();
    jo.putString("action", "save");
    jo.putString("collection", "pagevisit");
    this.requestJso.removeField("url");
    this.requestJso.removeField("title");
    this.requestJso.putString("urlid", this.pageUrl);
    jo.putObject("document", this.requestJso);
    return jo;
  }

  public JsonObject getSiteAllCmd(String siteid) {
    JsonObject jo = new JsonObject();
    jo.putString("action", "count");
    jo.putString("collection", "pagevisit");
    JsonObject matcher = new JsonObject().putString("siteid", siteid);

    jo.putObject("matcher", matcher);
    return jo;
  }
}
