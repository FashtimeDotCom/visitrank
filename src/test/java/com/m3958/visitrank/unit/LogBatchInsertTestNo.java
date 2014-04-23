package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.m3958.visitrank.LogProcessorWorkVerticle.LogProcessor;
import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.Utils.LogItemTransformer;
import com.m3958.visitrank.testutils.TestUtils;

public class LogBatchInsertTestNo {

  private String testlogname = "t-2014-03-27-01.log";

  private static int testNumber = 1000 * 10;


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
    TestUtils.deleteDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
    TestUtils.dropDailyDb(appConfig, testlogname);
    TestUtils.createDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
    TestUtils.createSampleLogs(appConfig.getLogDir(), testlogname, testNumber);
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.deleteDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
    TestUtils.dropDb(appConfig, appConfig.getRepoDbName());
    TestUtils.dropDailyDb(appConfig, testlogname);
  }

  @Test
  public void t() throws UnknownHostException {
    appConfig.setLogFileReadGap(20000);
    long start = System.currentTimeMillis();
    LogItemTransformer logItemTransformer = new LogItemTransformer(appConfig);
    LogProcessor lp = new LogProcessor(appConfig, logItemTransformer, testlogname);
    lp.process();
    System.out.println("gap 20000,write concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }

  @Test
  public void twconcern() throws UnknownHostException {
    appConfig.setLogFileReadGap(10000);
    long start = System.currentTimeMillis();
    LogItemTransformer logItemTransformer = new LogItemTransformer(appConfig);
    LogProcessor lp = new LogProcessor(appConfig, logItemTransformer, testlogname);
    lp.process();
    System.out.println("gap 10000,write concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }



  @Test
  public void twconcern0() throws UnknownHostException {
    appConfig.setLogFileReadGap(10000);
    long start = System.currentTimeMillis();
    LogItemTransformer logItemTransformer = new LogItemTransformer(appConfig);
    LogProcessor lp = new LogProcessor(appConfig, logItemTransformer, testlogname);
    lp.process();
    System.out.println("gap 10000,write concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }

  @Test
  public void twconcern2() throws UnknownHostException {
    appConfig.setLogFileReadGap(1000);
    long start = System.currentTimeMillis();
    LogItemTransformer logItemTransformer = new LogItemTransformer(appConfig);
    LogProcessor lp = new LogProcessor(appConfig, logItemTransformer, testlogname);
    lp.process();
    System.out.println("gap 1000,write concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }

  // @Test
  // public void tnowconcern1() throws UnknownHostException {
  // JsonObject cfg =
  // new JsonObject().putNumber("gap", 20000).putObject("writeconcern",
  // new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
  // appConfig.setLogFileReadGap(20000);
  // appConfig.setWriteConcern(writeConcern)
  // long start = System.currentTimeMillis();
  // LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg, 100);
  // lp.process();
  // System.out.println("gap 20000,journal concern:");
  // System.out.print(System.currentTimeMillis() - start);
  // System.out.println(" ms");
  // }
  //
  // @Test
  // public void tnowconcern2() throws UnknownHostException {
  // JsonObject cfg =
  // new JsonObject().putNumber("gap", 10000).putObject("writeconcern",
  // new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
  // long start = System.currentTimeMillis();
  // LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg, 100);
  // lp.process();
  // System.out.println("gap 10000,journal concern:");
  // System.out.print(System.currentTimeMillis() - start);
  // System.out.println(" ms");
  // }
  //
  // @Test
  // public void tnowconcern3() throws UnknownHostException {
  // JsonObject cfg =
  // new JsonObject().putNumber("gap", 5000).putObject("writeconcern",
  // new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
  // long start = System.currentTimeMillis();
  // LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg, 100);
  // lp.process();
  // System.out.println("gap 5000,journal concern:");
  // System.out.print(System.currentTimeMillis() - start);
  // System.out.println(" ms");
  // }
  //
  // @Test
  // public void tnowconcern4() throws UnknownHostException {
  // JsonObject cfg =
  // new JsonObject().putNumber("gap", 1000).putObject("writeconcern",
  // new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
  // long start = System.currentTimeMillis();
  // LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg, 100);
  // lp.process();
  // System.out.println("gap 1000,journal concern:");
  // System.out.print(System.currentTimeMillis() - start);
  // System.out.println(" ms");
  // }
  //
  // @Test
  // public void tnowconcern5() throws UnknownHostException {
  // JsonObject cfg =
  // new JsonObject().putNumber("gap", 20000).putObject("writeconcern",
  // new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
  // long start = System.currentTimeMillis();
  // LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg, 100);
  // lp.process();
  // System.out.println("gap 20000,journal concern:");
  // System.out.print(System.currentTimeMillis() - start);
  // System.out.println(" ms");
  // }
  //
  // @Test
  // public void tnowconcern6() throws UnknownHostException {
  // JsonObject cfg =
  // new JsonObject().putNumber("gap", 10000).putObject("writeconcern",
  // new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
  // long start = System.currentTimeMillis();
  // LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg, 100);
  // lp.process();
  // System.out.println("gap 10000,journal concern:");
  // System.out.print(System.currentTimeMillis() - start);
  // System.out.println(" ms");
  // }
  //
  // @Test
  // public void tnowconcern7() throws UnknownHostException {
  // JsonObject cfg =
  // new JsonObject().putNumber("gap", 5000).putObject("writeconcern",
  // new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
  // long start = System.currentTimeMillis();
  // LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg, 100);
  // lp.process();
  // System.out.println("gap 5000,journal concern:");
  // System.out.print(System.currentTimeMillis() - start);
  // System.out.println(" ms");
  // }
  //
  // @Test
  // public void tnowconcern8() throws UnknownHostException {
  // JsonObject cfg =
  // new JsonObject().putNumber("gap", 1000).putObject("writeconcern",
  // new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
  // long start = System.currentTimeMillis();
  // LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg, 100);
  // lp.process();
  // System.out.println("gap 1000,journal concern:");
  // System.out.print(System.currentTimeMillis() - start);
  // System.out.println(" ms");
  // }

}
