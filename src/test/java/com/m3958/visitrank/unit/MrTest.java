package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.testutils.TestUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceCommand.OutputType;

public class MrTest {

  private static String mrsourcedb = "mr-source-db";

  private static String mrResultColName = "mrresult";

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
    TestUtils.dropDb(appConfig, mrsourcedb);
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.dropDb(appConfig, mrsourcedb);
  }

  // after reduce,result is sort by _id ASC;
  @Test
  public void t2() throws UnknownHostException, InterruptedException {
    List<DBObject> obs =
        getDboList(1396587525833L, 1000 * 60 * 15, 3, "www.m3958.com", "www.fhsafety.gov.cn");
    TestUtils.createSampleDb(appConfig, mrsourcedb, obs);
    DB db = appConfig.getMongoClient().getDB(mrsourcedb);
    Map<String, String> funcmap = AppUtils.getMrFunctions(this.getClass(), "/mrfuncs/countmr.js");
    String mapjs = funcmap.get(AppConstants.MapReduceFunctionName.MAP);
    // String mapjs =
    // "function mapFunction() {" + "var key = this.url," + "value = {" + "url : this.url,"
    // + "count : 1" + "};" + "emit(key, value);" + "}";
    // String reducejs =
    // "function reduceFunction(key, values) {" + "var reducedObject = {" + "url : key,"
    // + "count : 0" + "};" +
    //
    // "values.forEach(function(value) {" + "reducedObject.count += value.count;" + "});"
    // + "return reducedObject;" + "}";
    String reducejs = funcmap.get(AppConstants.MapReduceFunctionName.REDUCE);
    execMapReduce(db, mapjs, reducejs);

    DBCollection mrCol = db.getCollection(mrResultColName);
    DBCursor cur = mrCol.find().limit(1).skip(0);
    Assert.assertTrue(cur.hasNext());
    DBObject dbo = cur.next();
    Assert.assertEquals("http://" + "www.fhsafety.gov.cn"
        + "/article.ftl?article=111891&ms=84224&section=84153", dbo.get("_id"));
    long count = ((Double) ((DBObject) dbo.get("value")).get("count")).longValue();
    Assert.assertEquals(3, count);
  }

  // daily count: {url:"",values:{date:d,count:1}}
  // db.collection.ensureIndex({"attrs.nested.value": 1}) nest field index.

  // twice mapreduce
  @Test
  public void t3() throws UnknownHostException, InterruptedException {
    List<DBObject> obs =
        getDboList(1396587525833L, 1000 * 60 * 15, 3, "www.m3958.com", "www.fhsafety.gov.cn");
    TestUtils.createSampleDb(appConfig, mrsourcedb, obs);
    DB db = appConfig.getMongoClient().getDB(mrsourcedb);
    String mapjs =
        "function mapFunction() {" + "var key = this.url," + "value = {" + "url : this.url,"
            + "count : 1" + "};" + "emit(key, value);" + "}";
    String reducejs =
        "function reduceFunction(key, values) {" + "var reducedObject = {" + "url : key,"
            + "count : 0" + "};" +

            "values.forEach(function(value) {" + "reducedObject.count += value.count;" + "});"
            + "return reducedObject;" + "}";
    execMapReduce(db, mapjs, reducejs);
    execMapReduce(db, mapjs, reducejs);
    DBCollection mrCol = db.getCollection(mrResultColName);
    DBCursor cur = mrCol.find().limit(1).skip(0);
    Assert.assertTrue(cur.hasNext());
    DBObject dbo = cur.next();
    Assert.assertEquals("http://" + "www.fhsafety.gov.cn"
        + "/article.ftl?article=111891&ms=84224&section=84153", dbo.get("_id"));
    long count = ((Double) ((DBObject) dbo.get("value")).get("count")).longValue();
    Assert.assertEquals(6, count);
  }

  private void execMapReduce(DB db, String mapjs, String reducejs) {
    long start = System.currentTimeMillis();
    DBCollection pagevisitCol = db.getCollection(AppConstants.MongoNames.PAGE_VISIT_COL_NAME);
    DBObject query = new BasicDBObject();

    MapReduceCommand mrc =
        new MapReduceCommand(pagevisitCol, mapjs, reducejs, mrResultColName, OutputType.REDUCE,
            query);
    // mrc.setOutputDB("");
    db.command(mrc.toDBObject());
    System.out.println("mr costs: " + (System.currentTimeMillis() - start) + "ms");
  }

  // 1396587525833L
  private List<DBObject> getDboList(long tsbase, long tsgap, int repeat, String... hostnames) {
    List<DBObject> obs = new ArrayList<>();
    int length = hostnames.length;
    for (int j = 0; j < repeat; j++) {
      for (int i = 0; i < length; i++) {
        obs.add(oneDbo(hostnames[i], tsbase + tsgap * i));
      }
    }
    return obs;
  }

  private DBObject oneDbo(String hostname, long ts) {
    DBObject dbo = new BasicDBObject();
    dbo.put("url", "http://" + hostname + "/article.ftl?article=111891&ms=84224&section=84153");
    dbo.put("ts", ts);
    DBObject headDbo = new BasicDBObject();
    headDbo.put("Host", "vr.fh.gov.cn:8333");
    headDbo.put("Accept", "*/*");
    headDbo
        .put(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
    headDbo.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,zh-TW;q=0.2");
    headDbo.put("Cookie", "");
    headDbo.put("Accept-Encoding", "gzip");
    headDbo.put("X-Forwarded-For", "10.74.111.254, 10.74.111.254, 127.0.0.1");
    headDbo.put("X-Varnish", "374563815");
    headDbo.put("X-Forwarded-Host", "vr.fh.gov.cn");
    headDbo.put("X-Forwarded-Server", "vr.fh.gov.cn");
    headDbo.put("Connection", "Keep-Alive");
    headDbo.put("ip", "127.0.0.1");
    dbo.put("headers", headDbo);
    return dbo;
  }

}
