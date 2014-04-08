package com.m3958.visitrank.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.mongodb.DBObject;

public class LogItemParser {

  private final ExecutorService pool;
  
  public LogItemParser(int poolSize){
    this.pool = Executors.newFixedThreadPool(poolSize);
  }
  
  public List<DBObject> getLogItems(List<LogItem> logItems){
    List<DBObject> results = new ArrayList<>();
    
    List<Future<DBObject>> futures;
    try {
      futures = pool.invokeAll(logItems);
    } catch (InterruptedException e) {
      return results;
    }
    pool.shutdown();
    while (!pool.isTerminated()) {}
    for (Future<DBObject> fu : futures) {
      try {
        results.add(fu.get());
      } catch (InterruptedException | ExecutionException e) {
      }
    }
    return results;
  }
  
}
