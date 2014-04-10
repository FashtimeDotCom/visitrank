package com.m3958.visitrank.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.m3958.visitrank.Utils.LogItem;
import com.m3958.visitrank.integration.java.TestConstants;
import com.mongodb.DBObject;

public class LogItemEcsTest {

  @Test
  public void t1() throws InterruptedException, ExecutionException {
    ecsPatternRun(10000, 10);
  }
  
  @Test
  public void t2() throws InterruptedException, ExecutionException {
    ecsPatternRun(10000, 50);
  }

  private void ecsPatternRun(int testNum, int poolsize) {
    ExecutorService initedPool = Executors.newFixedThreadPool(poolsize);
    long start = System.currentTimeMillis();
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
        if (counter % 999 == 0) {
          results.clear();
        }
      }
    }

    if (results.size() > 0) {
      System.out.println("last position:" + counter);
    }
    System.out.println(testNum + " items at poolsize " + poolsize + " costs:"
        + (System.currentTimeMillis() - start));
  }
}
