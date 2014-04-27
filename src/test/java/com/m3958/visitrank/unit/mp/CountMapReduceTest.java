package com.m3958.visitrank.unit.mp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.testutils.TestUtils;
import com.m3958.visitrank.unit.BatchCopyTestNo;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceCommand.OutputType;

public class CountMapReduceTest {
  // An index covers a query, a covered query, when:

  // all the fields in the query are part of that index, and
  // all the fields returned in the documents that match the query are in the same index.
  // db.pagevisit.find({url:/^http:\/\/www.fh/,ts:{$gte: 100}},{url:1,_id:0}).sort({"url":
  // 1,"ts":-1}).limit(100).explain()
  // after delete sort ts:-1,still return IndexOnly:true.
  // h,u,t

  private static AppConfig appConfig;

  @BeforeClass
  public static void sss() throws IOException {
    appConfig =
        new AppConfig(AppUtils.loadJsonResourceContent(BatchCopyTestNo.class, "testconf.json"),
            true);
  }

  @AfterClass
  public static void ccc() throws UnknownHostException {
    appConfig.closeMongoClient();
  }


  @Before
  public void setup() throws IOException {
    TestUtils.createMRSampleDb(appConfig, appConfig.getMrSrcDbName(), 10000 * 100, true, 10000);
  }
  
  public void cleanup() throws UnknownHostException{
    TestUtils.dropDb(appConfig, appConfig.getMrDbName());
  }

  @Test
  public void t1() throws UnknownHostException {
    TestUtils.assertDbItemEqual(appConfig, appConfig.getMrSrcDbName(), 10000 * 100);
  }

  // after reduce,result is sort by _id ASC;
  @Test
  public void t2() throws UnknownHostException, InterruptedException {
    Map<String, String> funcmap = AppUtils.getMrFunctions(appConfig, this.getClass(), "count10m.js");
    String mapjs = funcmap.get(AppConstants.MapReduceFunctionName.MAP);

    String reducejs = funcmap.get(AppConstants.MapReduceFunctionName.REDUCE);
    execMapReduce(mapjs, reducejs);

    DBCollection mrCol = getMrDb().getCollection(AppConstants.MapReduceColName.COUNT_TEN_M);
    DBCursor cur = mrCol.find().limit(1).skip(0);
    Assert.assertTrue(cur.hasNext());
    DBObject dbo = cur.next();
    Assert.assertEquals("http://" + "www.fhsafety.gov.cn"
        + "/article.ftl?article=111891&ms=84224&section=84153", dbo.get("_id"));
    long count = ((Double) ((DBObject) dbo.get("value")).get("count")).longValue();
    Assert.assertEquals(3, 3);
  }

  private void execMapReduce(String mapjs, String reducejs) {
    DB srcDb = appConfig.getMongoClient().getDB(appConfig.getMrSrcDbName());
    long start = System.currentTimeMillis();
    DBCollection pagevisitCol = srcDb.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    DBObject query = new BasicDBObject();

    MapReduceCommand mrc =
        new MapReduceCommand(pagevisitCol, mapjs, reducejs,
            AppConstants.MapReduceColName.COUNT_TEN_M, OutputType.REDUCE, query);
    mrc.setOutputDB(appConfig.getMrDbName());
    srcDb.command(mrc.toDBObject());
    System.out.println("mr costs: " + (System.currentTimeMillis() - start) + "ms");
  }
  
  private DB getMrDb(){
    return appConfig.getMongoClient().getDB(appConfig.getMrDbName());
  }
}
