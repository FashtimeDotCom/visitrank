package com.m3958.visitrank.unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.DailyCopyWorkVerticle;
import com.m3958.visitrank.LogCheckVerticle;
import com.m3958.visitrank.Utils.Locker;
import com.m3958.visitrank.testutils.TestUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * First we prepare a daily db named t-2014-03-02,insert 1k items. test insert to repository db.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class DailyProcessorTest {

  private String repositoryDbName = "t-visitrank";
  private String dailyDbName = "t-2014-03-02";
  private String newerdbname = "t-2014-03-03";

  private String dailyPartialDir = "t-" + AppConstants.DAILY_PARTIAL_DIR;

  @Before
  public void setup() throws IOException {
    TestUtils.deleteDirs(dailyPartialDir);
    TestUtils.dropDb(repositoryDbName);
    TestUtils.createSampleDb(dailyDbName, 1005, false, 5000);
    TestUtils.dropDb(newerdbname);
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.deleteDirs(dailyPartialDir);
    TestUtils.dropDb(repositoryDbName);
    TestUtils.dropDb(dailyDbName);
    TestUtils.dropDb(newerdbname);
  }

  /**
   * hourlydb not completed,even newerdb exist,repositoryDb should not change.
   * @throws IOException 
   */
  @Test
  public void t() throws IOException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB dailyDb = mongoClient.getDB(dailyDbName);

    DBCollection hourlyCol = dailyDb.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
    for (int idx = 24; idx > 12; idx--) {
      DBObject dbo =
          new BasicDBObject().append(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY, idx).append(
              AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY, "start");
      hourlyCol.insert(dbo);
    }

    DBObject dbo =
        new BasicDBObject().append(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY, 10).append(
            AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY, "end");
    hourlyCol.insert(dbo);

    TestUtils.createSampleDb(newerdbname, 10, false, 5000);

    new DailyCopyWorkVerticle.DailyCopyProcessor(mongoClient, dailyDbName, repositoryDbName,
        dailyPartialDir, new JsonObject().putNumber("dailydbreadgap", 1000)).process();
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(repositoryDbName);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    Assert.assertEquals(0, col.count());
  }

  @Test
  public void t1() throws IOException {
    TestUtils.createSampleDb(newerdbname, 10, false, 5000);
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB dailyDb = mongoClient.getDB(dailyDbName);

    DBCollection hourlyCol = dailyDb.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
    for (int idx = 24; idx > 12; idx--) {
      DBObject dbo =
          new BasicDBObject().append(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY, idx + "")
              .append(AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY, "end");
      hourlyCol.insert(dbo);
    }
    String dbname =
        new LogCheckVerticle.RemainDailyDbFinder(new Locker()).findOne("^t-\\d{4}-\\d{2}-\\d{2}$");
    boolean b =
        new DailyCopyWorkVerticle.DailyCopyProcessor(mongoClient, dailyDbName, new JsonObject())
            .isDailyDbComplete();
    Assert.assertTrue(b);
    Assert.assertNotNull(dbname);
    mongoClient.close();
  }

  /**
   * hourlyjob completed, and has newerdb exists,item will insert to repository db.and partial file
   * will delete, dailydb will delete.
   * @throws IOException 
   */
  @Test
  public void t2() throws IOException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB dailyDb = mongoClient.getDB(dailyDbName);

    DBCollection hourlyCol = dailyDb.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
    for (int idx = 24; idx > 12; idx--) {
      DBObject dbo =
          new BasicDBObject().append(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY, idx).append(
              AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY, "end");
      hourlyCol.insert(dbo);
    }

    TestUtils.createSampleDb(newerdbname, 10, false, 5000);
    new DailyCopyWorkVerticle.DailyCopyProcessor(mongoClient, dailyDbName, repositoryDbName,
        dailyPartialDir, new JsonObject().putNumber("dailydbreadgap", 1000)).process();

    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(repositoryDbName);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    Assert.assertEquals(1005, col.count());
    Assert.assertFalse(Files.exists(Paths.get(dailyPartialDir, dailyDbName)));
  }
}
