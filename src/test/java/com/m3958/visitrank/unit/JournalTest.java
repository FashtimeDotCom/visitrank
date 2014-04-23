package com.m3958.visitrank.unit;


import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.testutils.TestUtils;

public class JournalTest {

  private int times = 1000;

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

  @Test
  public void t1() throws InterruptedException, IOException {
    System.out.println("journal true,step 5000:");
    TestUtils.createSampleDb(appConfig, "ttt", times, true, 5000);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb(appConfig, "ttt");
  }

  @Test
  public void t2() throws InterruptedException, IOException {
    System.out.println("journal false,step 5000:");
    TestUtils.createSampleDb(appConfig, "ttt", times, false, 5000);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb(appConfig, "ttt");
  }

  @Test
  public void t3() throws InterruptedException, IOException {
    System.out.println("journal true,step 5000:");
    TestUtils.createSampleDb(appConfig, "ttt", times, true, 5000);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb(appConfig, "ttt");
  }

  @Test
  public void t4() throws InterruptedException, IOException {
    System.out.println("journal false,step 5000:");
    TestUtils.createSampleDb(appConfig, "ttt", times, false, 5000);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb(appConfig, "ttt");
  }

  @Test
  public void t5() throws InterruptedException, IOException {
    System.out.println("journal false,step 1:");
    TestUtils.createSampleDb(appConfig, "ttt", times, false, 1);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb(appConfig, "ttt");
  }

  @Test
  public void t6() throws InterruptedException, IOException {
    System.out.println("journal false,step 10:");
    TestUtils.createSampleDb(appConfig, "ttt", times, false, 10);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb(appConfig, "ttt");
  }

}
