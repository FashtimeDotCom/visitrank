package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.DailyCopyWorkVerticle;
import com.m3958.visitrank.testutils.TestUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * run in debug mode, then break vm in half,see if next time will continue process.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class DailyProcessorPartialTest {

  private String repositoryDbName = "t-visitrank";
  private String dailyDbName = "t-2014-03-02";
  private String dailyPartialDir = "t-" + AppConstants.DAILY_PARTIAL_DIR;

  @Before
  public void setup() throws IOException {
    if (!Files.exists(Paths.get(dailyPartialDir, dailyDbName))) {
      TestUtils.deleteDirs(dailyPartialDir);
      TestUtils.dropDb(repositoryDbName);
      TestUtils.createSampleDb(dailyDbName, 10005);
    }
  }

  @After
  public void cleanup() throws IOException {
    if (!Files.exists(Paths.get(dailyPartialDir, dailyDbName))) {
      TestUtils.deleteDirs(dailyPartialDir);
      TestUtils.dropDb(repositoryDbName);
      TestUtils.dropDb(dailyDbName);
    }
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
    //must drop,or complete status detect will wrong.
    hourlyCol.drop();
    for (int idx = 24; idx > 12; idx--) {
      DBObject dbo =
          new BasicDBObject().append(AppConstants.MongoNames.HOURLY_JOB_NUMBER_KEY, idx + "").append(
              AppConstants.MongoNames.HOURLY_JOB_STATUS_KEY, "end");
      hourlyCol.insert(dbo);
    }
    new DailyCopyWorkVerticle.DailyCopyProcessor(mongoClient, dailyDbName, repositoryDbName,
        dailyPartialDir,new JsonObject().putNumber("dailydbreadgap", 1000)).process();
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(repositoryDbName);
    DBCollection col = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    Assert.assertEquals(10005, col.count());
    Assert.assertFalse(Files.exists(Paths.get(dailyPartialDir, dailyDbName)));
  }

}
