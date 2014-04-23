package com.m3958.visitrank.unit.mp;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.testutils.TestUtils;
import com.m3958.visitrank.unit.BatchCopyTestNo;

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
    if (!AppUtils.DbExists(appConfig, appConfig.getMrDbName())) {
      TestUtils.createMRSampleDb(appConfig, appConfig.getMrDbName(), 10000 * 100, true, 10000);
    }
  }

  @Test
  public void t1() throws UnknownHostException {
    TestUtils.assertDbItemEqual(appConfig, appConfig.getMrDbName(), 10000 * 100);
  }
}
