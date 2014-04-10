package com.m3958.visitrank.Utils;

import java.util.Date;
import java.util.concurrent.Callable;

import com.m3958.visitrank.uaparser.Client;
import com.m3958.visitrank.uaparser.Parser;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class LogItem implements Callable<DBObject> {

  private String line;

  private Parser uaParser;

//  public LogItem(String line) {
//    this.line = line;
//  }

  public LogItem(Parser uaParser, String line) {
    this.uaParser = uaParser;
    this.line = line;
  }

  public DBObject toDbObject() {
    return transform((DBObject) JSON.parse(line));
  }

  private DBObject transform(DBObject dbo) {
    DBObject tdbo = new BasicDBObject();
    DBObject headers = (DBObject) dbo.get("headers");
    tdbo.put(FieldNameAbbreviation.URL_ABBREV, dbo.get(FieldNameAbbreviation.URL));
    tdbo.put(FieldNameAbbreviation.TS_ABBREV, new Date((long) dbo.get(FieldNameAbbreviation.TS)));

    tdbo.put(FieldNameAbbreviation.ACCEPT_ABBREV, headers.get(FieldNameAbbreviation.ACCEPT));

    tdbo.put(FieldNameAbbreviation.ACCEPT_LANGUAGE_ABBREV,
        headers.get(FieldNameAbbreviation.ACCEPT_LANGUAGE));
    tdbo.put(FieldNameAbbreviation.USER_AGENT_ABBREV,
        parseUa((String) headers.get(FieldNameAbbreviation.USER_AGENT)));
    tdbo.put(FieldNameAbbreviation.IP, getIp(headers));

    return tdbo;
  }

  public DBObject transform() {
    DBObject dbo = (DBObject) JSON.parse(line);
    DBObject tdbo = new BasicDBObject();
    DBObject headers = (DBObject) dbo.get("headers");
    tdbo.put(FieldNameAbbreviation.URL_ABBREV, dbo.get(FieldNameAbbreviation.URL));
    tdbo.put(FieldNameAbbreviation.TS_ABBREV, new Date((long) dbo.get(FieldNameAbbreviation.TS)));

    tdbo.put(FieldNameAbbreviation.ACCEPT_ABBREV, headers.get(FieldNameAbbreviation.ACCEPT));

    tdbo.put(FieldNameAbbreviation.ACCEPT_LANGUAGE_ABBREV,
        headers.get(FieldNameAbbreviation.ACCEPT_LANGUAGE));
    tdbo.put(FieldNameAbbreviation.USER_AGENT_ABBREV,
        parseUa((String) headers.get(FieldNameAbbreviation.USER_AGENT)));
    tdbo.put(FieldNameAbbreviation.IP, getIp(headers));

    return tdbo;
  }

  private DBObject parseUa(String uas) {
    DBObject o = new BasicDBObject();
    Client c = uaParser.parse(uas);
    o = (DBObject) JSON.parse(c.toString());
    return o;
  }


//  private DBObject parseUa(String uas) {
//    DBObject o = new BasicDBObject();
//    Parser uaParser;
//    try {
//      uaParser = new Parser();
//      Client c = uaParser.parse(uas);
//      o = (DBObject) JSON.parse(c.toString());
//    } catch (IOException e) {}
//    return o;
//  }

  private String getIp(DBObject headers) {
    if (headers.containsField("X-Forwarded-For")) {
      String xff = (String) headers.get("X-Forwarded-For");
      if (xff != null && !xff.isEmpty()) {
        String[] ss = xff.split(",");
        if (ss.length > 0) {
          return ss[0];
        }
      }
    }
    return (String) headers.get(FieldNameAbbreviation.IP);
  }

  @Override
  public DBObject call() throws Exception {
    return transform((DBObject) JSON.parse(line));
  }

}
