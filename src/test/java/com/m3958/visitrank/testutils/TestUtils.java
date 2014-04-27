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
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.Utils.FieldNameAbbreviation;
import com.m3958.visitrank.Utils.IndexBuilder;
import com.m3958.visitrank.Utils.LogItemTransformer;
import com.m3958.visitrank.uaparser.Parser;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

public class TestUtils {

  public static String[] hostnames = new String[] {"http://www.m3958.com", "http://www.fh.gov.cn",
      "http://www.nb.gov.cn"};

  public static void dropDailyDb(AppConfig appConfig, String testlogname)
      throws UnknownHostException {
    dropDb(appConfig, AppUtils.getDailyDbName(testlogname, appConfig.getDailyDbPtn()));
  }

  public static void dropDb(AppConfig appConfig, String dbname) throws UnknownHostException {
    appConfig.getMongoClient().dropDatabase(dbname);
  }

  public static void createSampleDb(AppConfig appConfig, String dbname, int items, boolean journal,
      int step) throws IOException {
    long start = System.currentTimeMillis();
    Parser uaParser = new Parser();
    LogItemTransformer logItemTransformer = new LogItemTransformer(appConfig);
    DB db = appConfig.getMongoClient().getDB(dbname);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    col.createIndex(IndexBuilder.getPageVisitColIndexKeys());
    List<DBObject> obs = new ArrayList<>();
    for (int i = 1; i <= items; i++) {
      JsonObject jo = logItemTransformer.transformToLog4j(getRandomJo(i), uaParser);
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
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms, create sampledb.");
  }

  public static void createSampleDb(AppConfig appConfig, String dbname, List<DBObject> dbos)
      throws UnknownHostException {
    DB db = appConfig.getMongoClient().getDB(dbname);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    col.insert(dbos, new WriteConcern(0, 0, false, true, true));
  }

  public static void createSampleDb(AppConfig appConfig, String dbname, int items, int repeat,
      boolean journal) throws IOException {
    for (int i = 0; i < repeat; i++) {
      createSampleDb(appConfig, dbname, items, journal, 5000);
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
      out.println(getRandomJo(i).toString());
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

  public static void assertDailyDbItemEqual(AppConfig appConfig, String testlogname)
      throws UnknownHostException {
    assertDbItemEqual(appConfig, AppUtils.getDailyDbName(testlogname, appConfig.getDailyDbPtn()),
        1000l);
  }

  public static void assertDbItemEqual(AppConfig appConfig, String dbname, long itemnumber)
      throws UnknownHostException {
    DB db = appConfig.getMongoClient().getDB(dbname);
    DBCollection coll = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    Assert.assertEquals(itemnumber, coll.getCount());

  }

  private static JsonObject getRandomJo(int i) {
    String ht = hostnames[RandomUtils.nextInt(3)];
    JsonObject joraw = new JsonObject();
    joraw.putString(FieldNameAbbreviation.PageVisit.URL, ht + "/?article=" + RandomUtils.nextInt());
    joraw.putNumber(FieldNameAbbreviation.PageVisit.TS, getDate(i));
    joraw
        .putObject(
            "headers",
            new JsonObject().putString("Connection", "keep-alive")
                .putString("Host", "localhost:8333")
                .putString("User-Agent", "Apache-HttpClient/4.2.6 (java 1.5")
                .putString("ip", getIp(i)));
    return joraw;
  }

  private static String getIp(int i) {
    return "127.0.0." + (i % 10);
  }

  /**
   * possible year,2013,2014 month is 0-11 date is 1-31 hour_of_day 0-23
   * 
   * @param i
   * @return
   */
  private static long getDate(int i) {
    Calendar c = Calendar.getInstance();
    if (i % 2 == 0) {
      c.set(Calendar.YEAR, 2014);
    } else {
      c.set(Calendar.YEAR, 2013);
    }
    c.set(Calendar.MONTH, i % 11);
    c.set(Calendar.DATE, i % 28);
    c.set(Calendar.HOUR_OF_DAY, i % 23);
    c.set(Calendar.MINUTE, i % 59);
    c.set(Calendar.SECOND, i % 59);
    c.set(Calendar.MILLISECOND, i % 999);
    return c.getTimeInMillis();
  }

  public static void createMRSampleDb(AppConfig appConfig, String dbname, int items,
      boolean journal, int step) throws IOException {
    if (AppUtils.DbExists(appConfig, dbname)) {
      return;
    }
    long start = System.currentTimeMillis();
    Parser uaParser = new Parser();
    DB db = appConfig.getMongoClient().getDB(dbname);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    col.createIndex(IndexBuilder.getPageVisitColIndexKeys());
    LogItemTransformer logItemTransformer = new LogItemTransformer(appConfig);
    List<DBObject> obs = new ArrayList<>();
    for (int i = 1; i <= items; i++) {
      JsonObject jo = logItemTransformer.transformToLog4j(getRandomJo(i), uaParser);
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
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms, create sampledb.");
  }
}
