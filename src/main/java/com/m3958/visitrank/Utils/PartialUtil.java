package com.m3958.visitrank.Utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.Utils.FileLineReader.FindLineResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class PartialUtil {

  public static FindLineResult findLastPosition(Path logfile) throws IOException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(AppConstants.MongoNames.REPOSITORY_DB_NAME);
    DBCollection coll = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    DBObject query = new BasicDBObject();
    query.put("$natural", -1);
    DBCursor cursor = coll.find().sort(query).limit(1);
    if (cursor.hasNext()) {
      DBObject dbo = cursor.next();
      cursor.close();
      FileLineReader flr = new FileLineReader(logfile);
      Date d = (Date) dbo.get(FieldNameAbbreviation.TS);
      return flr.getLogItem((String)dbo.get(FieldNameAbbreviation.URL), d.getTime());
    }
    mongoClient.close();
    return null;
  }

}
