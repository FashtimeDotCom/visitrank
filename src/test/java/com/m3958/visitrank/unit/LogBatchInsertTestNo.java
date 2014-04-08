package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.LogCheckVerticle.WriteConcernParser;
import com.m3958.visitrank.LogProcessorWorkVerticle.LogProcessor;
import com.m3958.visitrank.testutils.TestUtils;

public class LogBatchInsertTestNo {

  private String logDir = "testlogs";
  private String archiveDir = "testarchive";
  
  private String testlogname = "t-2014-03-27-01.log";
  
  private static int testNumber = 1000*10;
  
  @Before
  public void setup() throws IOException{
    TestUtils.deleteDirs(logDir, archiveDir);
    TestUtils.dropDailyDb(testlogname);
    TestUtils.createDirs(logDir, archiveDir);
    TestUtils.createSampleLogs(logDir, testlogname,testNumber);
  }
  
  @After
  public void cleanup() throws IOException{
    TestUtils.deleteDirs(logDir, archiveDir);
    TestUtils.dropDailyDb(testlogname);
  }
  
  @Test
  public void t() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 20000);
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 20000,write concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  
  @Test
  public void twconcern() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 10000);
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 10000,write concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  

  
  @Test
  public void twconcern0() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 10000);
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 10000,write concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  
  @Test
  public void twconcern2() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 1000);
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 1000,write concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  
  @Test
  public void tnowconcern1() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 20000).putObject("writeconcern", new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 20000,journal concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  
  @Test
  public void tnowconcern2() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 10000).putObject("writeconcern", new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 10000,journal concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  
  @Test
  public void tnowconcern3() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 5000).putObject("writeconcern", new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 5000,journal concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  
  @Test
  public void tnowconcern4() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 1000).putObject("writeconcern", new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 1000,journal concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  
  @Test
  public void tnowconcern5() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 20000).putObject("writeconcern", new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 20000,journal concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  
  @Test
  public void tnowconcern6() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 10000).putObject("writeconcern", new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 10000,journal concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  
  @Test
  public void tnowconcern7() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 5000).putObject("writeconcern", new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 5000,journal concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  
  @Test
  public void tnowconcern8() throws UnknownHostException{
    JsonObject cfg = new JsonObject().putNumber("gap", 1000).putObject("writeconcern", new WriteConcernParser(AppConstants.WRITE_CONCERN).parse());
    long start = System.currentTimeMillis();
    LogProcessor lp = new LogProcessor(logDir, archiveDir, testlogname, cfg);
    lp.process();
    System.out.println("gap 1000,journal concern:");
    System.out.print(System.currentTimeMillis() - start);
    System.out.println(" ms");
  }
  
}
