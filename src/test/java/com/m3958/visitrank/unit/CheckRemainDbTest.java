package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.LogCheckVerticle;
import com.m3958.visitrank.Utils.Locker;
import com.m3958.visitrank.testutils.TestUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class CheckRemainDbTest {

  private String dailydbname = "t-2014-03-02";
  
  private String newerdbname = "t-2014-03-03";
  
  private Locker locker;

  @Before
  public void setup() throws IOException {
    locker = new Locker();
    TestUtils.createSampleDailyDb(dailydbname, 10);
    TestUtils.dropDb(newerdbname);
  }

  @After
  public void cleanup() throws IOException {
    locker.releaseLock(dailydbname);
    locker = null;
    TestUtils.dropDb(dailydbname);
    TestUtils.dropDb(newerdbname);
  }


  @Test
  public void t1() throws UnknownHostException {
    //has no newerdb.
    String fn = new LogCheckVerticle.RemainDailyDbFinder(locker).findOne("t-\\d{4}-\\d{2}-\\d{2}");
    Assert.assertNull(fn);
    locker.releaseLock(dailydbname);
  }
  
  @Test
  public void t2() throws UnknownHostException {
    //has newerdb.no hourly collection
    TestUtils.createSampleDailyDb(newerdbname, 10);
    String fn = new LogCheckVerticle.RemainDailyDbFinder(locker).findOne("t-\\d{4}-\\d{2}-\\d{2}");
    Assert.assertNull(fn);
    locker.releaseLock(dailydbname);
  }
  
  @Test
  public void t3() throws UnknownHostException {
    //has newerdb.no hourly collection
    TestUtils.createSampleDailyDb(newerdbname, 10);
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB dailyDb = mongoClient.getDB(dailydbname);

    DBCollection hourlyCol = dailyDb.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
    for (int idx = 24; idx > 12; idx--) {
      DBObject dbo =
          new BasicDBObject().append(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY, idx).append(
              AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY, "end");
      hourlyCol.insert(dbo);
    }
    mongoClient.close();
    String fn = new LogCheckVerticle.RemainDailyDbFinder(locker).findOne("t-\\d{4}-\\d{2}-\\d{2}");
    Assert.assertEquals(dailydbname, fn);
    locker.releaseLock(dailydbname);
  }


}
