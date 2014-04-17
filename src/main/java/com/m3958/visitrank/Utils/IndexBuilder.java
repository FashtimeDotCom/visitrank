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

  public static void hostNameIndex(String dbname) {
    MongoClient mongoClient;
    try {
      mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
      DB db = mongoClient.getDB(dbname);
      DBCollection hostnameCol;
      if(AppUtils.colExist(mongoClient, db, dbname)){
        hostnameCol = db.getCollection(AppConstants.MongoNames.HOST_NAME_COLLECTION_NAME);
      }else{
        hostnameCol = db.createCollection(AppConstants.MongoNames.HOST_NAME_COLLECTION_NAME,new BasicDBObject());
      }
      
      hostnameCol.ensureIndex(new BasicDBObject(FieldNameAbbreviation.HostName.HOST, 1), null, true);
      hostnameCol.ensureIndex(new BasicDBObject(FieldNameAbbreviation.HostName.HOST_SHORT, 1), null, true);
    } catch (UnknownHostException e) {}

  }

  public static void hostNameIndex() {
    hostNameIndex(AppConstants.MongoNames.META_DB_NAME);
  }
}
