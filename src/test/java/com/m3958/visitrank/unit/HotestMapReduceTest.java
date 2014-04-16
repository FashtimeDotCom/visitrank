package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.testutils.TestUtils;

public class HotestMapReduceTest {
  // An index covers a query, a covered query, when:

  // all the fields in the query are part of that index, and
  // all the fields returned in the documents that match the query are in the same index.
  // db.pagevisit.find({url:/^http:\/\/www.fh/,ts:{$gte: 100}},{url:1,_id:0}).sort({"url":
  // 1,"ts":-1}).limit(100).explain()
  // after delete sort ts:-1,still return IndexOnly:true.
  // h,u,t
  

  public String dbname = "mr-visitrank";

  @Before
  public void setup() throws IOException {
    if (!TestUtils.DbExists(dbname)) {
      TestUtils.createMRSampleDb(dbname, 10000 * 100, true, 10000);
    }
  }

  @Test
  public void t1() throws UnknownHostException {
    TestUtils.assertDbItemEqual(dbname, 10000 * 100);
  }
}
