package com.m3958.visitrank.rediscmd;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

public class INCR {
  
  private String key;
  
  public INCR(String key){
    this.key = key;
  }
  
  public JsonObject getCmd(){
    JsonObject jo = new JsonObject();
    jo.putString("command", "INCR");
    JsonArray ja = new JsonArray();
    ja.add(this.key);
    jo.putArray("args", ja);
    return jo;
  }
}
