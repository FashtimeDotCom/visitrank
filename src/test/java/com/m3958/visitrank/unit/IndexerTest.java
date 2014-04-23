package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.Utils.FieldNameAbbreviation;
import com.m3958.visitrank.Utils.IndexBuilder;
import com.m3958.visitrank.testutils.TestUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class IndexerTest {

  public String indexDbname = "index-test";

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

  @After
  public void cleanup() throws UnknownHostException {
    TestUtils.dropDb(appConfig, indexDbname);
  }

  @Test
  public void t1() throws UnknownHostException {
    IndexBuilder.hostNameIndex(appConfig, indexDbname);
    DB db = appConfig.getMongoClient().getDB(indexDbname);
    DBCollection col = db.getCollection(AppConstants.MongoNames.HOST_NAME_COLLECTION_NAME);
    List<DBObject> idxes = col.getIndexInfo();
    Assert.assertEquals(3, idxes.size());
    for (DBObject dbo : idxes) {
      String name = (String) dbo.get("name");
      if ("h_1".equals(name) || "s_1".equals(name)) {
        Assert.assertTrue((boolean) dbo.get("unique"));
      }
    }
    col.insert(new BasicDBObject(FieldNameAbbreviation.HostName.HOST, "www.m3958.com").append(
        FieldNameAbbreviation.HostName.HOST_SHORT, "0"));

    try {
      col.insert(new BasicDBObject(FieldNameAbbreviation.HostName.HOST, "www.m3958.com").append(
          FieldNameAbbreviation.HostName.HOST_SHORT, "0"));
    } catch (Exception e) {}
  }
}
