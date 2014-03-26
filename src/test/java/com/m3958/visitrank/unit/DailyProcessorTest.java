package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.DailyCopyWorkVerticle;
import com.m3958.visitrank.testutils.TestUtils;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * First we prepare a daily db named t-2014-03-02,insert 1k items.
 * test insert to repository db.
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
    TestUtils.deleteDailyPartials(dailyPartialDir);
    TestUtils.dropTestRepositoryDb(repositoryDbName);
    TestUtils.createSampleDailyDb(dailyDbName, 1005);
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.deleteDailyPartials(dailyPartialDir);
    TestUtils.dropTestRepositoryDb(repositoryDbName);
    TestUtils.dropSampleDailyDb(dailyDbName);
  }

  @Test
  public void t() throws UnknownHostException {
    MongoClient mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    new DailyCopyWorkVerticle.DailyCopyProcessor(mongoClient, dailyDbName, repositoryDbName,dailyPartialDir).process();
    
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    
    DB db = mongoClient.getDB(repositoryDbName);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    Assert.assertEquals(1005, col.count());
    
  }
}
