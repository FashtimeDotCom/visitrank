package com.m3958.visitrank.unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.Utils.LogItem;
import com.m3958.visitrank.integration.java.TestConstants;
import com.m3958.visitrank.uaparser.CachingParser;
import com.m3958.visitrank.uaparser.Parser;
import com.mongodb.DBObject;

public class LogItemEcsTest {

  private Parser uaParser;

  private Parser cacheingpParser;

  @Before
  public void setup() throws IOException {
    uaParser = new Parser();
    cacheingpParser = new CachingParser();
  }

  @After
  public void cleanup() {
    uaParser = null;
    cacheingpParser = null;
  }
  
  @Test
  public void t(){
    Assert.assertTrue(true);
  }

//  @Test
//  public void t1() throws InterruptedException, ExecutionException {
//    ecsPatternRun(uaParser,10000, 10);
//  }
//  
//  @Test
//  public void t2() throws InterruptedException, ExecutionException {
//    ecsPatternRun(cacheingpParser,10000, 10);
//  }
//
//
//  @Test
//  public void t3() throws InterruptedException, ExecutionException {
//    ecsPatternRun(uaParser,10000, 50);
//  }
//  
//  @Test
//  public void t4() throws InterruptedException, ExecutionException {
//    ecsPatternRun(cacheingpParser,10000, 50);
//  }
//
//  @Test
//  public void t5() throws InterruptedException, ExecutionException {
//    ecsPatternRun(uaParser,10000, 5);
//  }
//  
//  public void t6() throws InterruptedException, ExecutionException {
//    ecsPatternRun(cacheingpParser,10000, 5);
//  }
//
//
//  @Test
//  public void t7() throws InterruptedException, ExecutionException {
//    long start = System.currentTimeMillis();
//    List<DBObject> results = new ArrayList<>();
//    for (int i = 0; i < 10000; i++) {
//      results.add(new LogItem(uaParser, TestConstants.logItemSample).transform());
//    }
//    System.out.println("shared parser costs:" + (System.currentTimeMillis() - start));
//  }
//
//  @Test
//  public void t8() throws InterruptedException, ExecutionException, IOException {
//    long start = System.currentTimeMillis();
//    List<DBObject> results = new ArrayList<>();
//    for (int i = 0; i < 10000; i++) {
//      results.add(new LogItem(cacheingpParser, TestConstants.logItemSample).transform());
//    }
//    System.out.println("shared cache parser costs:" + (System.currentTimeMillis() - start));
//  }
//  
//  private void ecsPatternRun(Parser parser, long testNum, int poolsize) {
//    ExecutorService initedPool = Executors.newFixedThreadPool(poolsize);
//    long start = System.currentTimeMillis();
//    CompletionService<DBObject> ecs = new ExecutorCompletionService<DBObject>(initedPool);
//
//    List<DBObject> results = new ArrayList<>();
//
//    for (int i = 0; i < testNum; i++) {
//      ecs.submit(new LogItem(uaParser, TestConstants.logItemSample));
//    }
//    int counter = 0;
//    for (int i = 0; i < testNum; ++i) {
//      DBObject r = null;
//      try {
//        r = ecs.take().get();
//      } catch (InterruptedException | ExecutionException e) {
//        e.printStackTrace();
//      }
//      if (r != null) {
//        counter++;
//        results.add(r);
//        if (counter % 999 == 0) {
//          results.clear();
//        }
//      }
//    }
//
//    if (results.size() > 0) {
//      System.out.println("last position:" + counter);
//    }
//    System.out.println(testNum + " items at poolsize " + poolsize + " costs:"
//        + (System.currentTimeMillis() - start));
//  }
}
