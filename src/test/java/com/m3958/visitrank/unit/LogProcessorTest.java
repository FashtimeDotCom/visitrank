package com.m3958.visitrank.unit;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.AppUtils;
import com.m3958.visitrank.LogProcessorWorkVerticle;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class LogProcessorTest {

  private String testlogname = "t-2014-03-02-01.log";
  private String logDir = "testlogs";
  private String archiveDir = "tarchives";

  @Before
  public void setup() throws IOException {
    if(Files.exists(Paths.get(logDir))){
      File[] files = new File(logDir).listFiles();
      for (File f : files) {
        if (f.getName().endsWith(".doing")) {
          f.delete();
        }
      }
    }


    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    mongoClient.dropDatabase(AppUtils.getDailyDbName(testlogname));
    mongoClient.close();

    Files.deleteIfExists(Paths.get(archiveDir, testlogname));
    Files.deleteIfExists(Paths.get(archiveDir));

    if (!Files.exists(Paths.get(logDir))) {
      Files.createDirectories(Paths.get(logDir));
    }

    if (!Files.exists(Paths.get(archiveDir))) {
      Files.createDirectories(Paths.get(archiveDir));
    }
  }

  @After
  public void cleanup() throws IOException {
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

  @Test
  public void t() {
    new LogProcessorWorkVerticle.LogProcessor(logDir, archiveDir, testlogname).process();
    Assert.assertTrue(Files.exists(Paths.get(archiveDir), LinkOption.NOFOLLOW_LINKS));
    Assert.assertTrue(Files.exists(Paths.get(archiveDir, testlogname), LinkOption.NOFOLLOW_LINKS));

    try {
      MongoClient mongoClient =
          new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
      DB db = mongoClient.getDB(AppUtils.getDailyDbName(testlogname));
      DBCollection coll = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
      Assert.assertTrue(coll.getCount() > 0);
      mongoClient.close();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }
}
