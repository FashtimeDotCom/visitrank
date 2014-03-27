package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.AppUtils;
import com.m3958.visitrank.DailyCopyWorkVerticle;
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
  private String dailyPartialDir = "t-" + AppConstants.DAILY_PARTIAL_DIR;

  @Before
  public void setup() throws IOException {
    TestUtils.deleteDirs(dailyPartialDir);
    TestUtils.dropTestRepositoryDb(repositoryDbName);
    TestUtils.createSampleDailyDb(dailyDbName, 1005);
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.deleteDirs(dailyPartialDir);
    TestUtils.dropTestRepositoryDb(repositoryDbName);
    TestUtils.dropSampleDailyDb(dailyDbName);
  }

  /**
   * if hourlyjob is not completed,no item will insert to repository db.
   * 
   * @throws UnknownHostException
   */
  @Test
  public void t() throws UnknownHostException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB dailyDb = mongoClient.getDB(dailyDbName);

    DBCollection hourlyCol = dailyDb.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
    for (int idx = 24; idx > 12; idx--) {
      DBObject dbo =
          new BasicDBObject().append(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY, idx).append(
              AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY, "start");
      hourlyCol.insert(dbo);
    }
    new DailyCopyWorkVerticle.DailyCopyProcessor(mongoClient, dailyDbName, repositoryDbName,
        dailyPartialDir).process();
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(repositoryDbName);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    Assert.assertEquals(0, col.count());
  }

  /**
   * if hourlyjob had completed some,but has gap in hourly, no item will insert to repository db.
   * 
   * @throws UnknownHostException
   */
  @Test
  public void t1() throws UnknownHostException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB dailyDb = mongoClient.getDB(dailyDbName);

    DBCollection hourlyCol = dailyDb.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
    for (int idx = 24; idx > 12; idx--) {
      DBObject dbo =
          new BasicDBObject().append(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY, idx).append(
              AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY, "end");
      hourlyCol.insert(dbo);
    }

    DBObject dbo =
        new BasicDBObject().append(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY, 10).append(
            AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY, "end");
    hourlyCol.insert(dbo);

    new DailyCopyWorkVerticle.DailyCopyProcessor(mongoClient, dailyDbName, repositoryDbName,
        dailyPartialDir).process();
    
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(repositoryDbName);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    Assert.assertEquals(0, col.count());
    mongoClient.close();
  }

  /**
   * if hourlyjob has completed,item will insert to repository db.and partial file will delete,
   * dailydb will delete.
   * 
   * @throws UnknownHostException
   */
  @Test
  public void t2() throws UnknownHostException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB dailyDb = mongoClient.getDB(dailyDbName);

    DBCollection hourlyCol = dailyDb.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
    for (int idx = 24; idx > 12; idx--) {
      DBObject dbo =
          new BasicDBObject().append(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY, idx).append(
              AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY, "end");
      hourlyCol.insert(dbo);
    }
    new DailyCopyWorkVerticle.DailyCopyProcessor(mongoClient, dailyDbName, repositoryDbName,
        dailyPartialDir).process();
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(repositoryDbName);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    Assert.assertEquals(1005, col.count());
    Assert.assertFalse(Files.exists(Paths.get(dailyPartialDir, dailyDbName)));
    Assert.assertNull(findDb(mongoClient, dailyDbName));
  }

  private String findDb(MongoClient mongoClient, String dbnametofind) {
    for (String dbname : mongoClient.getDatabaseNames()) {
      if (AppUtils.isDailyDb(dbname)) {
        if(dbnametofind.equals(dbname)){
          return dbname;
        }
      }
    }
    return null;
  }

}
