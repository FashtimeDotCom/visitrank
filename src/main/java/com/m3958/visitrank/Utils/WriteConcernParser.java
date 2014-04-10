package com.m3958.visitrank.Utils;

import org.vertx.java.core.json.JsonObject;

import com.mongodb.WriteConcern;

public class WriteConcernParser {
  private String raw;

  public WriteConcernParser(String raw) {
    this.raw = raw;
  }

  public JsonObject parse() {
    if (raw.isEmpty()) {
      return null;
    }
    // WriteConcern(int w, int wtimeout, boolean fsync, boolean j, boolean continueOnInsertError)
    String[] ss = raw.split(",");
    JsonObject jo = new JsonObject();
    jo.putNumber("w", Integer.parseInt(ss[0], 10));
    jo.putNumber("wtimeout", Long.parseLong(ss[1], 10));
    jo.putBoolean("fsync", "true".equals(ss[2]) ? true : false);
    jo.putBoolean("j", "true".equals(ss[3]) ? true : false);
    jo.putBoolean("continueOnInsertError", "true".equals(ss[4]) ? true : false);
    return jo;
  }

  public static WriteConcern getWriteConcern(JsonObject jo) {
    if (jo != null) {
      return new WriteConcern(jo.getInteger("w"), jo.getInteger("wtimeout"),
          jo.getBoolean("fsync"), jo.getBoolean("j"), jo.getBoolean("continueOnInsertError"));
    } else {
      return null;
    }
  }
}
