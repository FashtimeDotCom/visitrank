package com.m3958.visitrank.Utils;

import java.net.UnknownHostException;

import com.m3958.visitrank.AppConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class IndexBuilder {


  public static DBObject getPageVisitColIndexKeys() {
    DBObject indexer = new BasicDBObject();
    indexer.put(FieldNameAbbreviation.PageVisit.HOST, 1);
    indexer.put(FieldNameAbbreviation.PageVisit.URL, 1);
    indexer.put(FieldNameAbbreviation.PageVisit.TS, -1);
    return indexer;
  }

  public static void pageVisitIndex() {
    MongoClient mongoClient;
    try {
      mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
      DB db = mongoClient.getDB(AppConstants.MongoNames.REPOSITORY_DB_NAME);
      DBCollection pagevisitCol = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
      pagevisitCol.createIndex(getPageVisitColIndexKeys());
    } catch (UnknownHostException e) {}
  }

  public static void hostNameIndex() {
    MongoClient mongoClient;
    try {
      mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
      DB db = mongoClient.getDB(AppConstants.MongoNames.META_DB_NAME);
      DBCollection hostnameCol =
          db.getCollection(AppConstants.MongoNames.HOST_NAME_COLLECTION_NAME);
      hostnameCol.createIndex(new BasicDBObject(FieldNameAbbreviation.HostName.HOST, 1),
          new BasicDBObject("unique", true));
      hostnameCol.createIndex(new BasicDBObject(FieldNameAbbreviation.HostName.HOST_SHORT, 1),
          new BasicDBObject("unique", true));
    } catch (UnknownHostException e) {}
  }
}
