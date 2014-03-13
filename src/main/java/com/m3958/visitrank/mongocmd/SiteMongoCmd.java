package com.m3958.visitrank.mongocmd;

import org.vertx.java.core.json.JsonObject;

public class SiteMongoCmd {

  private String siteid;

  public SiteMongoCmd(String siteid) {
    this.siteid = siteid;
  }

  public JsonObject findListCmd() {
    JsonObject jo = new JsonObject();
    jo.putString("action", "find");
    jo.putString("collection", "site");

    JsonObject matcher = new JsonObject();
    matcher.putString("_id", this.siteid);
    jo.putValue("matcher", matcher);
    return jo;
  }

  public JsonObject findOneCmd() {
    JsonObject jo = new JsonObject();
    jo.putString("action", "findone");
    jo.putString("collection", "site");

    JsonObject matcher = new JsonObject();
    matcher.putString("_id", this.siteid);
    jo.putValue("matcher", matcher);
    return jo;
  }

  public JsonObject countCmd() {
    JsonObject jo = new JsonObject();
    jo.putString("action", "count");
    jo.putString("collection", "pagevisit");

    JsonObject matcher = new JsonObject();
    matcher.putString("siteid", this.siteid);
    jo.putValue("matcher", matcher);
    return jo;
  }
}
