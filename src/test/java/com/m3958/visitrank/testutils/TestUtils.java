package com.m3958.visitrank.testutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Assert;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.AppUtils;
import com.m3958.visitrank.Utils.IndexBuilder;
import com.m3958.visitrank.Utils.LogItem;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

public class TestUtils {

  public static Pattern dailyDbPtn = Pattern.compile("(.*\\d{4}-\\d{2}-\\d{2})(.*)");

  public static void dropDailyDb(String testlogname) throws UnknownHostException {
    dropDb(AppUtils.getDailyDbName(testlogname, dailyDbPtn));
  }

  public static void dropDb(String dbname) throws UnknownHostException {
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    mongoClient.dropDatabase(dbname);
    mongoClient.close();
  }

  public static void createSampleDb(String dbname, int items) throws UnknownHostException {
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(dbname);
    DBCollection col =
        db.createCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME, new BasicDBObject());
    col.createIndex(IndexBuilder.getPageVisitColIndexKeys());
    String sampleItemPre = "{\"url\":\"http://sb.m3958.com";
    String sampleItemFix =
        "\",\"ts\":1395291463536,\"headers\":{\"Connection\":\"keep-alive\",\"\":\"\",\"Host\":\"localhost:8333\",\"User-Agent\":\"Apache-HttpClient/4.2.6 (java 1.5)\",\"ip\":\"127.0.0.1\"}}";

    List<DBObject> obs = new ArrayList<>();
    for (int i = 1; i <= items; i++) {
      DBObject dbo = new LogItem(sampleItemPre + "?article=" + i + sampleItemFix).toDbObject();
      obs.add(dbo);
      if (i % 5000 == 0) {
        col.insert(obs, new WriteConcern(0, 0, false, true, true));
        obs.clear();
      }
    }

    if (obs.size() > 0) {
      col.insert(obs, new WriteConcern(0, 0, false, true, true));
    }
    mongoClient.close();
  }

  public static void createSampleDb(String dbname, List<DBObject> dbos) throws UnknownHostException {
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(dbname);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    col.insert(dbos, new WriteConcern(0, 0, false, true, true));
    mongoClient.close();
  }

  public static void createSampleDb(String dbname, int items, int repeat)
      throws UnknownHostException {
    for (int i = 0; i < repeat; i++) {
      createSampleDb(dbname, items);
    }
  }

  public static void deleteDirs(String... dirs) throws IOException {
    for (String dir : dirs) {
      if (Files.exists(Paths.get(dir))) {
        File[] files = new File(dir).listFiles();
        for (File f : files) {
          f.delete();
        }
        Files.deleteIfExists(Paths.get(dir));
      }
    }
  }

  public static void createDirs(String... dirs) throws IOException {
    for (String dir : dirs) {
      if (!Files.exists(Paths.get(dir))) {
        Files.createDirectory(Paths.get(dir));
      }
    }
  }

  public static void createSampleLogs(String logDir, String testlogname, long number)
      throws UnsupportedEncodingException, FileNotFoundException {
    String sampleItemPre = "{\"url\":\"http://sb.m3958.com";
    String sampleItemFix =
        "\",\"ts\":1396173397887,\"headers\":{\"Connection\":\"keep-alive\",\"\":\"\",\"Host\":\"localhost:8333\",\"User-Agent\":\"Apache-HttpClient/4.2.6 (java 1.5)\",\"ip\":\"127.0.0.1\"}}";

    PrintWriter out =
        new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(
            logDir, testlogname)), "UTF-8")));

    for (int i = 0; i < number; i++) {
      out.println(sampleItemPre + "?article=" + i + sampleItemFix);
    }
    out.close();
  }

  public static void createSamplePartialLogs(String logDir, String testlogname) throws IOException {
    List<String> lines = new ArrayList<>();
    int i = 100;
    for (; i < 600; i += 100) {
      lines.add(i + "," + i + "\n");
    }

    Files.write(Paths.get(logDir, testlogname + AppConstants.PARTIAL_POSTFIX), lines,
        Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW);
  }

  public static void assertDailyDbItemEqual(String testlogname) throws UnknownHostException {
    assertDbItemEqual(AppUtils.getDailyDbName(testlogname,dailyDbPtn), 1000l);
  }

  public static void assertDbItemEqual(String dbname, long itemnumber) throws UnknownHostException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(dbname);
    DBCollection coll = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    Assert.assertEquals(itemnumber, coll.getCount());
    mongoClient.close();
  }


}
