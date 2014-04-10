package com.m3958.visitrank.unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.m3958.visitrank.Utils.FieldNameAbbreviation;
import com.m3958.visitrank.Utils.LogItem;
import com.m3958.visitrank.integration.java.TestConstants;
import com.m3958.visitrank.uaparser.Parser;
import com.mongodb.DBObject;

public class LogItemTest {

  private int testNum = 1000;

  private static ExecutorService initedPool;

  private static int initPoolSize = 500;

  @BeforeClass
  public static void classSetup() {
    initedPool = Executors.newFixedThreadPool(initPoolSize);
  }

  @AfterClass
  public static void classAfter() {
    initedPool = null;
  }


  @Test
  public void t1() throws IOException {
    Parser uaParser = new Parser();
    DBObject dbo = new LogItem(uaParser, TestConstants.logItemSample).toDbObject();
    Assert.assertEquals("http://www.fhsafety.gov.cn/article.ftl?article=112167&ms=84224",
        dbo.get(FieldNameAbbreviation.URL_ABBREV));

    Assert.assertEquals(new Date(1396587508489L), dbo.get(FieldNameAbbreviation.TS_ABBREV));

    Assert.assertEquals("*/*", dbo.get(FieldNameAbbreviation.ACCEPT_ABBREV));

    Assert.assertEquals("zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,zh-TW;q=0.2",
        dbo.get(FieldNameAbbreviation.ACCEPT_LANGUAGE_ABBREV));

    DBObject uaob = (DBObject) dbo.get(FieldNameAbbreviation.USER_AGENT_ABBREV);

    Assert.assertEquals("Windows 7", ((DBObject) uaob.get("os")).get("family"));

    Assert.assertEquals("10.74.111.254", dbo.get(FieldNameAbbreviation.IP));
  }

  @Test
  public void t2() throws IOException {
    Parser uaParser = new Parser();
    long start = System.currentTimeMillis();
    List<DBObject> dbos = new ArrayList<>();
    for (int i = 0; i < testNum; i++) {
      dbos.add(new LogItem(uaParser, TestConstants.logItemSample).toDbObject());
    }
    System.out.print(System.currentTimeMillis() - start);
    System.out.println("ms,serial execute.");
  }

  @Test
  public void t3() throws InterruptedException, ExecutionException, IOException {
    poolExecute(testNum, 10, false);
  }

  @Test
  public void t4() throws InterruptedException, ExecutionException, IOException {
    poolExecute(testNum, 100, false);
  }

  @Test
  public void t5() throws InterruptedException, ExecutionException, IOException {
    poolExecute(10000, 200, false);
  }

  @Test
  public void t6() throws InterruptedException, ExecutionException, IOException {
    poolExecute(10000, initPoolSize, true);
  }

  @Test
  public void t7() throws InterruptedException, ExecutionException {
    long start = System.currentTimeMillis();
    Executors.newFixedThreadPool(10);
    System.out.println("create execute 10: " + (System.currentTimeMillis() - start));
  }

  @Test
  public void t8() throws InterruptedException, ExecutionException {
    long start = System.currentTimeMillis();
    Executors.newFixedThreadPool(1000);
    System.out.println("create execute 1000: " + (System.currentTimeMillis() - start));
  }

  private void poolExecute(int testNum, int poolSize, boolean useInitedPool)
      throws InterruptedException, ExecutionException, IOException {
    long start = System.currentTimeMillis();
    Parser uaParser = new Parser();
    ExecutorService executorPool;
    if (useInitedPool) {
      executorPool = initedPool;
    } else {
      executorPool = Executors.newFixedThreadPool(poolSize);
    }


    List<LogItem> logItems = new ArrayList<>();
    for (int i = 0; i < testNum; i++) {
      logItems.add(new LogItem(uaParser, TestConstants.logItemSample));
    }
    List<Future<DBObject>> futures = executorPool.invokeAll(logItems);
    executorPool.shutdown();
    while (!executorPool.isTerminated()) {}

    List<DBObject> results = new ArrayList<>();

    for (Future<DBObject> fu : futures) {
      results.add(fu.get());
    }
    System.out.print("total items:" + testNum + ", poolSize:" + poolSize + ",take: ");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println("ms,pool execute.");
  }
}
