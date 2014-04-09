package com.m3958.visitrank.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.m3958.visitrank.Utils.LogItem;
import com.m3958.visitrank.integration.java.TestConstants;
import com.mongodb.DBObject;

public class LogItemEcsTest {

  private static ExecutorService initedPool;

  private static int initPoolSize = 10;

  @BeforeClass
  public static void classSetup() {
    initedPool = Executors.newFixedThreadPool(initPoolSize);
  }

  @AfterClass
  public static void classAfter() {
    initedPool = null;
  }

  @Test
  public void t9() throws InterruptedException, ExecutionException {
    long start = System.currentTimeMillis();
    ecsPatternRun(10000 * 10);
    System.out.println("ecs execute 10000: " + (System.currentTimeMillis() - start));
  }

  private void ecsPatternRun(int testNum) {
    CompletionService<DBObject> ecs = new ExecutorCompletionService<DBObject>(initedPool);

    List<DBObject> results = new ArrayList<>();
    
    for (int i = 0; i < testNum; i++) {
      ecs.submit(new LogItem(TestConstants.logItemSample));
    }
    int counter = 0;
    for (int i = 0; i < testNum; ++i) {
      DBObject r = null;
      try {
        r = ecs.take().get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
      if (r != null) {
        counter++;
        results.add(r);
        if(counter % 999 == 0){
          System.out.println("at position:" + counter);
          results.clear();
        }
      }
    }
    
    if(results.size() > 0){
      System.out.println("last position:" + counter);
    }
    
  }
}
