package com.m3958.visitrank.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
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

  private static int initPoolSize = 100;

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
    ecsPatternRun(10000);
    System.out.println("ecs execute 10000: " + (System.currentTimeMillis() - start));
  }

  private void ecsPatternRun(int testNum) {
    CompletionService<DBObject> ecs = new ExecutorCompletionService<DBObject>(initedPool);
    List<LogItem> logItems = new ArrayList<>();
    for (int i = 0; i < testNum; i++) {
      logItems.add(new LogItem(TestConstants.logItemSample));
    }

    for (Callable<DBObject> s : logItems)
      ecs.submit(s);
    int n = logItems.size();
    for (int i = 0; i < n; ++i) {
      DBObject r = null;
      try {
        r = ecs.take().get();
      } catch (InterruptedException | ExecutionException e) {
      }
      if (r != null) {
//        System.out.println(r);
      }
    }
  }
}
