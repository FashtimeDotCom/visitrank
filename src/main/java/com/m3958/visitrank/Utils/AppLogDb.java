package com.m3958.visitrank.Utils;

import java.net.UnknownHostException;

import com.m3958.visitrank.AppConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class AppLogDb {

  private static String APP_LOG_DB_NAME = "applog";

  private static String LOG_PROCESS_COL_NAME = "logprocessor";

  private MongoClient mongoClient;

  private DB logProcessorDb;
  
  private DBCollection logProcessCol;

  public AppLogDb() {
    try {
      mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
      initDb();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

  public void initDb() {
    logProcessorDb = mongoClient.getDB(APP_LOG_DB_NAME);
    if (!AppUtils.colExist(mongoClient, logProcessorDb, LOG_PROCESS_COL_NAME)) {
      // { capped: true, size: 100000 }
      // {logfile: "xxx",position: 5555,finished:false}
      DBObject ccfg = new BasicDBObject();
      ccfg.put("capped", true);
      ccfg.put("size", 10000);
      logProcessCol = logProcessorDb.createCollection(LOG_PROCESS_COL_NAME, ccfg);
    }else{
      logProcessCol = logProcessorDb.getCollection(LOG_PROCESS_COL_NAME);
    }
    mongoClient.close();
  }
  
  public DBObject getLast(){
    //db.cappedCollection.find().sort( { $natural: -1 } )
    DBObject sorto = new BasicDBObject();
    sorto.put("$natural", -1);
    DBCursor dbcur = logProcessCol.find().sort(sorto);
    if(dbcur.hasNext()){
      return dbcur.next();
    }else{
      return null;
    }
  }

  public void saveLogProcessPosition(String logname, long position, boolean finished) {
    DBObject dbo = new BasicDBObject();
    dbo.put("logfile", logname);
    dbo.put("position", position);
    dbo.put("finished", finished);
    logProcessCol.insert(dbo);
  }
  
  public void closeMongoClient(){
    this.mongoClient.close();
  }
}
