package com.m3958.visitrank.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ser.std.StdJdkSerializers.FileSerializer;
import com.m3958.visitrank.AppConstants;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class PartialUtil {

  public static long findLastPosition(Path logfile) throws UnknownHostException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(AppConstants.MongoNames.REPOSITORY_DB_NAME);
    DBCollection coll = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    DBObject query = new BasicDBObject();
    query.put("$natural", -1);
    DBCursor cursor = coll.find().sort(query).limit(1);
    if (cursor.hasNext()) {
      DBObject dbo = cursor.next();
      cursor.close();
//      return searchFile(logfile, dbo);
    }
    mongoClient.close();
    return 0;
  }

}
