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
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.Utils.FieldNameAbbreviation;
import com.m3958.visitrank.Utils.IndexBuilder;
import com.m3958.visitrank.Utils.LogItemTransformer;
import com.m3958.visitrank.uaparser.Parser;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

public class TestUtils {

  public static Pattern dailyDbPtn = Pattern.compile("(.*\\d{4}-\\d{2}-\\d{2})(.*)");

   public static String[] hostnames = new String[] {"http://www.m3958.com",
   "http://www.fh.gov.cn",
   "http://www.nb.gov.cn"};

  public static void dropDailyDb(String testlogname) throws UnknownHostException {
    dropDb(AppUtils.getDailyDbName(testlogname, dailyDbPtn));
  }

  public static void dropDb(String dbname) throws UnknownHostException {
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    mongoClient.dropDatabase(dbname);
    mongoClient.close();
  }

  public static boolean DbExists(String dbname) throws UnknownHostException {
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    List<String> dbns = mongoClient.getDatabaseNames();
    boolean exist = false;
    for (String db : dbns) {
      if (db.equals(dbname)) {
        exist = true;
        break;
      }
    }
    mongoClient.close();
    return exist;
  }

  public static void createSampleDb(String dbname, int items, boolean journal, int step)
      throws IOException {
    long start = System.currentTimeMillis();
    Parser uaParser = new Parser();
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(dbname);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    col.createIndex(IndexBuilder.getPageVisitColIndexKeys());
    List<DBObject> obs = new ArrayList<>();
    for (int i = 1; i <= items; i++) {
      JsonObject jo = LogItemTransformer.transformToLog4j(getRandomJo(), uaParser);
      obs.add((DBObject) JSON.parse(jo.toString()));
      if (i % step == 0) {
        col.insert(obs, new WriteConcern(0, 0, false, journal, true));
        obs.clear();
      }
    }

    if (obs.size() > 0) {
      col.insert(obs, new WriteConcern(0, 0, false, journal, true));
      obs.clear();
    }
    mongoClient.close();
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms, create sampledb.");
  }

  public static void createSampleDb(String dbname, List<DBObject> dbos) throws UnknownHostException {
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(dbname);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    col.insert(dbos, new WriteConcern(0, 0, false, true, true));
    mongoClient.close();
  }

  public static void createSampleDb(String dbname, int items, int repeat, boolean journal)
      throws IOException {
    for (int i = 0; i < repeat; i++) {
      createSampleDb(dbname, items, journal, 5000);
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
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(
            logDir, testlogname)), "UTF-8")));

    for (int i = 0; i < number; i++) {
      out.println(getRandomJo().toString());
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
    assertDbItemEqual(AppUtils.getDailyDbName(testlogname, dailyDbPtn), 1000l);
  }

  public static void assertDbItemEqual(String dbname, long itemnumber) throws UnknownHostException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(dbname);
    DBCollection coll = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    Assert.assertEquals(itemnumber, coll.getCount());
    mongoClient.close();
  }

  private static JsonObject getRandomJo() {
    String ht = hostnames[RandomUtils.nextInt(3)];
    JsonObject joraw = new JsonObject();
    joraw.putString(FieldNameAbbreviation.PageVisit.URL, ht + "/?article=" + RandomUtils.nextInt());
    joraw.putNumber(FieldNameAbbreviation.PageVisit.TS, new Date().getTime());
    joraw.putObject(
        "headers",
        new JsonObject().putString("Connection", "keep-alive").putString("Host", "localhost:8333")
            .putString("User-Agent", "Apache-HttpClient/4.2.6 (java 1.5")
            .putString("ip", "127.0.0.1"));
    return joraw;
  }

  public static void createMRSampleDb(String dbname, int items, boolean journal, int step)
      throws IOException {
    long start = System.currentTimeMillis();
    Parser uaParser = new Parser();
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(dbname);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    col.createIndex(IndexBuilder.getPageVisitColIndexKeys());

    List<DBObject> obs = new ArrayList<>();
    for (int i = 1; i <= items; i++) {
      JsonObject jo = LogItemTransformer.transformToLog4j(getRandomJo(), uaParser);
      obs.add((DBObject) JSON.parse(jo.toString()));
      if (i % step == 0) {
        col.insert(obs, new WriteConcern(0, 0, false, journal, true));
        obs.clear();
      }
    }

    if (obs.size() > 0) {
      col.insert(obs, new WriteConcern(0, 0, false, journal, true));
      obs.clear();
    }
    mongoClient.close();
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms, create sampledb.");
  }
}
