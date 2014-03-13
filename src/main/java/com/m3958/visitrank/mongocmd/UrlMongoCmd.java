package com.m3958.visitrank.mongocmd;

import org.vertx.java.core.json.JsonObject;

/**
 * saved pageUrl's _id is uuid string,not ObjectId.
 * 
 * { "_id" : "5b41bc31-3a83-437e-8486-6b67a4bf3698", "url" : "http://www.example.co
 * m", "title" : null, "ip" : "127.0.0.1"}
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class UrlMongoCmd {
  private JsonObject counterUrl;

  public UrlMongoCmd(JsonObject counterUrl) {
    this.counterUrl = counterUrl;
  }

  public JsonObject findOneCmd() {
    JsonObject jo = new JsonObject();
    JsonObject matcherJo = new JsonObject();
    matcherJo.putString("url", this.counterUrl.getString("url"));
    jo.putString("action", "findone");
    jo.putString("collection", "pageurl");
    jo.putObject("matcher", matcherJo);
    return jo;
  }

  public JsonObject saveCmd() {
    JsonObject jo = new JsonObject();
    jo.putString("action", "save");
    jo.putString("collection", "pageurl");
    JsonObject doc = new JsonObject();
    doc.putString("url", this.counterUrl.getString("url")).putString("title",
        this.counterUrl.getString("title"));

    jo.putObject("document", doc);
    return jo;
  }
}
