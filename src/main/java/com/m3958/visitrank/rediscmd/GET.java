package com.m3958.visitrank.rediscmd;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

public class GET {
  
  private String key;
  
  public GET(String key){
    this.key = key;
  }
  
  public JsonObject getCmd(){
    JsonObject jo = new JsonObject();
    jo.putString("command", "get");
    JsonArray ja = new JsonArray();
    ja.add(this.key);
    jo.putArray("args", ja);
    return jo;
  }
}
