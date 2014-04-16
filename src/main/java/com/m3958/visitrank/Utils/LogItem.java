package com.m3958.visitrank.Utils;

import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.uaparser.Parser;

public class LogItem {

  private JsonObject item;

  public LogItem(JsonObject item) {
    this.item = item;
  }

  public JsonObject transform(Parser uaparser) {
    JsonObject headers = item.getObject("headers");
    String ua = headers.getString(FieldNameAbbreviation.PageVisit.USER_AGENT);
    UaParserClientWrapper uawrap = new UaParserClientWrapper(uaparser.parse(ua));
    headers.putObject(FieldNameAbbreviation.PageVisit.USER_AGENT, uawrap.toJson());
    item.putString(FieldNameAbbreviation.PageVisit.IP, getIp(headers));
    item.removeField("out");
    headers.removeField(FieldNameAbbreviation.PageVisit.IP);
    return item;
  }

  private String getIp(JsonObject headers) {
    if (headers.containsField("X-Forwarded-For")) {
      String xff = headers.getString("X-Forwarded-For");
      if (xff != null && !xff.isEmpty()) {
        String[] ss = xff.split(",");
        if (ss.length > 0) {
          return ss[0];
        }
      }
    }
    return headers.getString(FieldNameAbbreviation.PageVisit.IP);
  }
}
