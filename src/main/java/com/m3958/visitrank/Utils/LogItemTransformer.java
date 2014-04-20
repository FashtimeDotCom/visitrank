package com.m3958.visitrank.Utils;

import java.sql.Date;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.uaparser.Parser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class LogItemTransformer {

  private static IncreamentString incStr;

  private static boolean inited = false;

  @SuppressWarnings("unchecked")
  private Map<String, String> cachedHost = new LRUMap(10000);

  private AppConfig appConfig;

  public LogItemTransformer(AppConfig appConfig) {
    this.appConfig = appConfig;
  }

  public DBObject transformToDb(String line) {
    if (!inited) {
      inited = true;
      DB db = appConfig.getMongoClient().getDB(appConfig.getMetaDbName());
      DBCollection coll = db.getCollection(AppConstants.MongoNames.HOST_NAME_COLLECTION_NAME);
      // hostname.find({},{hs:1}).sort({hs:-1}).limit(1);
      DBCursor dbc =
          coll.find(new BasicDBObject(),
              new BasicDBObject(FieldNameAbbreviation.HostName.HOST_SHORT, 1))
              .sort(new BasicDBObject(FieldNameAbbreviation.HostName.HOST_SHORT, -1)).limit(1);
      if (dbc.hasNext()) {
        String s = (String) dbc.next().get(FieldNameAbbreviation.HostName.HOST_SHORT);
        incStr = new IncreamentString(s);
      } else {
        incStr = new IncreamentString();
      }
    }
    DBObject dbo = (DBObject) JSON.parse(line);
    String url = (String) dbo.get(FieldNameAbbreviation.PageVisit.URL);
    dbo.put(FieldNameAbbreviation.PageVisit.HOST, getShortHostname(HostExtractor.getHost(url)));
    dbo.put(FieldNameAbbreviation.PageVisit.URL, HostExtractor.getUri(url));
    dbo.put(FieldNameAbbreviation.PageVisit.TS,
        new Date((long) dbo.get(FieldNameAbbreviation.PageVisit.TS)));
    return dbo;
  }

  private String getShortHostname(String longhostname) {
    String sh = cachedHost.get(longhostname);
    if (sh != null) {
      return sh;
    }
    DB db = appConfig.getMongoClient().getDB(appConfig.getMetaDbName());
    DBCollection coll = db.getCollection(AppConstants.MongoNames.HOST_NAME_COLLECTION_NAME);
    DBObject dbo =
        coll.findOne(new BasicDBObject(FieldNameAbbreviation.HostName.HOST, longhostname));
    if (dbo != null) {
      cachedHost.put((String) dbo.get(FieldNameAbbreviation.HostName.HOST),
          (String) dbo.get(FieldNameAbbreviation.HostName.HOST_SHORT));
    }
    // hostname.find({},{hs:1}).sort({hs:-1}).limit(1);
    try {
      coll.insert(new BasicDBObject(FieldNameAbbreviation.HostName.HOST, longhostname).append(
          FieldNameAbbreviation.HostName.HOST_SHORT, incStr.getNext()));
      sh = incStr.getCurrent();
    } catch (Exception e) {
      return getShortHostname(longhostname);
    }
    cachedHost.put(longhostname, sh);
    return sh;
  }

  public JsonObject transformToLog4j(JsonObject item, Parser uaparser) {
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
