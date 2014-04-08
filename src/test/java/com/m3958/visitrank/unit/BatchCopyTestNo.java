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
import com.m3958.visitrank.AppUtils;
import com.m3958.visitrank.Utils.IndexBuilder;
import com.m3958.visitrank.testutils.TestUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

public class BatchCopyTestNo {

  private String logDir = "testlogs";
  private String archiveDir = "testarchive";

  private static String testlogname = "t-2014-03-27-01.log";

  private static String repositoryDbName = "t-visitrank";

  private static int testNumber = 1000 * 10;

  @Before
  public void setup() throws IOException {
    TestUtils.deleteDirs(logDir, archiveDir);
    TestUtils.createDirs(logDir, archiveDir);
    TestUtils.dropDb(repositoryDbName);
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.deleteDirs(logDir, archiveDir);
    TestUtils.dropDb(repositoryDbName);
  }

  @BeforeClass
  public static void sss() throws UnknownHostException {
    TestUtils.dropDailyDb(testlogname);
    TestUtils.createSampleDb(AppUtils.getDailyDbName(testlogname, TestUtils.dailyDbPtn),
        testNumber, false, 500);
  }

  @AfterClass
  public static void ccc() throws UnknownHostException {
    TestUtils.dropDailyDb(testlogname);
  }

  @Test
  public void t1() throws UnknownHostException, InterruptedException {
    new Ttt(10000, -1, true).start();
    Thread.sleep(1000);
    TestUtils.assertDbItemEqual(repositoryDbName, testNumber);
  }

  @Test
  public void t2() throws UnknownHostException, InterruptedException {
    new Ttt(10000, -1, false).start();
    Thread.sleep(1000);
    TestUtils.assertDbItemEqual(repositoryDbName, testNumber);
  }


  @Test
  public void t3() throws UnknownHostException, InterruptedException {
    new Ttt(10000, 10000, true).start();
    Thread.sleep(1000);
    TestUtils.assertDbItemEqual(repositoryDbName, testNumber);
  }

  @Test
  public void t4() throws UnknownHostException, InterruptedException {
    new Ttt(10000, 10000, false).start();
    Thread.sleep(1000);
    TestUtils.assertDbItemEqual(repositoryDbName, testNumber);
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
      MongoClient mongoClient;
      mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);

      DB dailyDb = mongoClient.getDB(AppUtils.getDailyDbName(testlogname, TestUtils.dailyDbPtn));
      DBCollection dailyColl = dailyDb.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);

      DB repositoryDb = mongoClient.getDB(repositoryDbName);
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
      mongoClient.close();
    }

  }
}
