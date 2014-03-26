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

import org.junit.Assert;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.AppUtils;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class TestUtils {

  public static void dropDailyDb(String testlogname) throws UnknownHostException {
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    mongoClient.dropDatabase(AppUtils.getDailyDbName(testlogname));
    mongoClient.close();
  }
  
  
  public static void createSampleDailyDb(String dbname,int number) throws UnknownHostException {
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(dbname);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    
    String sampleItemPre = "{\"url\":\"http://sb.m3958.com";
    String sampleItemFix =
        "\",\"siteid\":\"fa5f2e1d-092a-4c8c-9518-f5b7600f8f80\",\"record\":\"true\",\"ts\":1395291463536,\"headers\":{\"Connection\":\"keep-alive\",\"\":\"\",\"Host\":\"localhost:8333\",\"User-Agent\":\"Apache-HttpClient/4.2.6 (java 1.5)\",\"ip\":\"127.0.0.1\"}}";

    for (int i = 0; i < number; i++) {
      DBObject dbo = (DBObject) JSON.parse(sampleItemPre + "?article=" + i + sampleItemFix);
      col.insert(dbo);
    }
    mongoClient.close();
  }
  
  public static void dropSampleDailyDb(String dbname) throws UnknownHostException {
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    mongoClient.dropDatabase(dbname);
    mongoClient.close();
  }

  public static void dropTestRepositoryDb(String repositoryDbName) throws UnknownHostException {
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    mongoClient.dropDatabase(repositoryDbName);
    mongoClient.close();
  }

  public static void deleteTestDirs(String logDir, String archiveDir) throws IOException {
    if (Files.exists(Paths.get(logDir))) {
      File[] files = new File(logDir).listFiles();
      for (File f : files) {
        f.delete();
      }
    }

    if (Files.exists(Paths.get(archiveDir))) {
      File[] afiles = new File(archiveDir).listFiles();
      for (File f : afiles) {
        f.delete();
      }
      Files.deleteIfExists(Paths.get(archiveDir));
      Files.deleteIfExists(Paths.get(logDir));
    }
  }

  public static void deleteDailyPartials(String dailyPartialDir) throws IOException {
    if (Files.exists(Paths.get(dailyPartialDir))) {
      File[] files = new File(dailyPartialDir).listFiles();
      for (File f : files) {
        f.delete();
      }
    }
    Files.deleteIfExists(Paths.get(dailyPartialDir));
  }

  public static void createDirs(String logDir, String archiveDir) throws IOException {
    if (!Files.exists(Paths.get(logDir))) {
      Files.createDirectories(Paths.get(logDir));
    }
    if (!Files.exists(Paths.get(archiveDir))) {
      Files.createDirectories(Paths.get(archiveDir));
    }
  }

  public static void createSampleLogs(String logDir, String testlogname)
      throws UnsupportedEncodingException, FileNotFoundException {
    String sampleItemPre = "{\"url\":\"http://sb.m3958.com";
    String sampleItemFix =
        "\",\"siteid\":\"fa5f2e1d-092a-4c8c-9518-f5b7600f8f80\",\"record\":\"true\",\"ts\":1395291463536,\"headers\":{\"Connection\":\"keep-alive\",\"\":\"\",\"Host\":\"localhost:8333\",\"User-Agent\":\"Apache-HttpClient/4.2.6 (java 1.5)\",\"ip\":\"127.0.0.1\"}}";

    PrintWriter out =
        new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(
            logDir, testlogname)), "UTF-8")));

    for (int i = 0; i < 1000; i++) {
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

  public static void assertDbItemEqual(String testlogname) throws UnknownHostException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(AppUtils.getDailyDbName(testlogname));
    DBCollection coll = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    Assert.assertEquals(1000, coll.getCount());
    mongoClient.close();

  }
}
