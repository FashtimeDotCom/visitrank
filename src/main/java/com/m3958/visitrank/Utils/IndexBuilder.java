package com.m3958.visitrank.Utils;

import java.net.UnknownHostException;

import com.m3958.visitrank.AppConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class IndexBuilder {
  
  
  public static DBObject getPageVisitColIndexKeys(){
    DBObject indexer = new BasicDBObject();
    indexer.put(FieldNameAbbreviation.URL, 1);
    indexer.put(FieldNameAbbreviation.TS, -1);
    return indexer;
  }

  public static void pageVisitIndex(){
    MongoClient mongoClient;
    try {
      mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
      DB db = mongoClient.getDB(AppConstants.MongoNames.REPOSITORY_DB_NAME);
      DBCollection pagevisitCol = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
      pagevisitCol.createIndex(getPageVisitColIndexKeys());
    } catch (UnknownHostException e) {
    }
  }
}
