package com.m3958.visitrank.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Date;

import org.vertx.java.core.json.JsonObject;

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
      Date d = (Date) dbo.get(FieldNameAbbreviation.PageVisit.TS);
      return flr.getLogItem((String) dbo.get(FieldNameAbbreviation.PageVisit.URL), d.getTime());
    }
    mongoClient.close();
    return null;
  }

  public static long findLastProcessDbObject(Path logfile) throws IOException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(AppConstants.MongoNames.REPOSITORY_DB_NAME);
    DBCollection coll = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    DBObject query = new BasicDBObject();
    query.put("$natural", -1);
    DBCursor cursor = coll.find().sort(query).limit(1);
    long count = 0;
    if (cursor.hasNext()) {
      DBObject dbo = cursor.next();
      cursor.close();
      String url = (String) dbo.get(FieldNameAbbreviation.PageVisit.URL);
      long ts = ((Date) dbo.get(FieldNameAbbreviation.PageVisit.TS)).getTime();
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(logfile.toFile()), "UTF-8"));
      String line;

      while ((line = reader.readLine()) != null) {
        count++;
        JsonObject jo = new JsonObject(line);
        if (jo.getString(FieldNameAbbreviation.PageVisit.URL).equals(url)
            && ts == jo.getLong(FieldNameAbbreviation.PageVisit.TS)) {
          reader.close();
          return count;
        }
      }
      reader.close();
    }
    
    mongoClient.close();
    return count;
  }

}
