package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.Utils.IndexBuilder;
import com.m3958.visitrank.testutils.TestUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

public class BatchCopyTestNo {

  private static String testlogname = "t-2014-03-27-01.log";

  private static int testNumber = 1000 * 10;

  private static AppConfig appConfig;

  @Before
  public void setup() throws IOException {
    TestUtils.deleteDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
    TestUtils.createDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
    TestUtils.dropDb(appConfig, appConfig.getRepoDbName());
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.deleteDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
    TestUtils.dropDb(appConfig, appConfig.getRepoDbName());

  }

  @BeforeClass
  public static void sss() throws IOException {
    appConfig =
        new AppConfig(AppUtils.loadJsonResourceContent(BatchCopyTestNo.class, "testconf.json"),
            true);
    TestUtils.dropDailyDb(appConfig, testlogname);
    TestUtils.createSampleDb(appConfig,
        AppUtils.getDailyDbName(testlogname, appConfig.getDailyDbPtn()), testNumber, false, 500);
  }

  @AfterClass
  public static void ccc() throws UnknownHostException {
    TestUtils.dropDailyDb(appConfig, testlogname);
    appConfig.closeMongoClient();
  }

  @Test
  public void t1() throws UnknownHostException, InterruptedException {
    new Ttt(10000, -1, true).start();
    Thread.sleep(1000);
    TestUtils.assertDbItemEqual(appConfig, appConfig.getRepoDbName(), testNumber);
  }

  @Test
  public void t2() throws UnknownHostException, InterruptedException {
    new Ttt(10000, -1, false).start();
    Thread.sleep(1000);
    TestUtils.assertDbItemEqual(appConfig, appConfig.getRepoDbName(), testNumber);
  }


  @Test
  public void t3() throws UnknownHostException, InterruptedException {
    new Ttt(10000, 10000, true).start();
    Thread.sleep(1000);
    TestUtils.assertDbItemEqual(appConfig, appConfig.getRepoDbName(), testNumber);
  }

  @Test
  public void t4() throws UnknownHostException, InterruptedException {
    new Ttt(10000, 10000, false).start();
    Thread.sleep(1000);
    TestUtils.assertDbItemEqual(appConfig, appConfig.getRepoDbName(), testNumber);
  }


  public class Ttt {
    private int gap;
    private int batchSize;

    private boolean journal;

    public Ttt(int gap, int batchSize, boolean journal) throws UnknownHostException {
      super();
      this.gap = gap;
      this.batchSize = batchSize;
      this.journal = journal;
    }

    public void start() throws UnknownHostException {

      DB dailyDb =
          appConfig.getMongoClient().getDB(
              AppUtils.getDailyDbName(testlogname, appConfig.getDailyDbPtn()));
      DBCollection dailyColl = dailyDb.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);

      DB repositoryDb = appConfig.getMongoClient().getDB(appConfig.getRepoDbName());
      DBCollection repositoryCol =
          repositoryDb.createCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME,
              new BasicDBObject());

      repositoryCol.createIndex(IndexBuilder.getPageVisitColIndexKeys());
      WriteConcern wc = new WriteConcern(0, 0, false, journal, true);

      DBCursor cursor;
      if (batchSize == -1) {
        cursor = dailyColl.find();
      } else {
        cursor = dailyColl.find().batchSize(batchSize);
      }

      int counter = 0;
      List<DBObject> obs = new ArrayList<>();
      while (cursor.hasNext()) {
        DBObject item = cursor.next();
        counter++;
        obs.add(item);
        if (counter % gap == 0) {
          repositoryCol.insert(obs, wc);
          obs.clear();
        }
      }
      if (obs.size() > 0) {
        repositoryCol.insert(obs, wc);
      }
      obs.clear();
    }
  }
}
