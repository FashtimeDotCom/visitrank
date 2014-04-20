package com.m3958.visitrank.Utils;

import com.m3958.visitrank.AppConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class IndexBuilder {


  public static DBObject getPageVisitColIndexKeys() {
    DBObject indexer = new BasicDBObject();
    indexer.put(FieldNameAbbreviation.PageVisit.HOST, 1);
    indexer.put(FieldNameAbbreviation.PageVisit.URL, 1);
    indexer.put(FieldNameAbbreviation.PageVisit.TS, -1);
    return indexer;
  }

  public static void pageVisitIndex(AppConfig appConfig) {
    DB db = appConfig.getMongoClient().getDB(appConfig.getRepoDbName());
    DBCollection pagevisitCol = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    pagevisitCol.createIndex(getPageVisitColIndexKeys());
  }

  public static void hostNameIndex(AppConfig appConfig, String dbname) {
    DB db = appConfig.getMongoClient().getDB(dbname);
    DBCollection hostnameCol;
    if (AppUtils.colExist(appConfig, dbname, AppConstants.MongoNames.HOST_NAME_COLLECTION_NAME)) {
      hostnameCol = db.getCollection(AppConstants.MongoNames.HOST_NAME_COLLECTION_NAME);
    } else {
      hostnameCol =
          db.createCollection(AppConstants.MongoNames.HOST_NAME_COLLECTION_NAME,
              new BasicDBObject());
    }

    hostnameCol.ensureIndex(new BasicDBObject(FieldNameAbbreviation.HostName.HOST, 1), null, true);
    hostnameCol.ensureIndex(new BasicDBObject(FieldNameAbbreviation.HostName.HOST_SHORT, 1), null,
        true);
  }

  public static void hostNameIndex(AppConfig appConfig) {
    hostNameIndex(appConfig, appConfig.getMetaDbName());
  }
}
