package com.m3958.visitrank.Utils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.uaparser.Parser;

public class LogItemSaver {

  private ExecutorService execPool;
  
  private Parser parser;
  
  public LogItemSaver(int poolSize) throws IOException{
    execPool = Executors.newFixedThreadPool(poolSize);
    parser = new Parser();
  }
  
  public void save(final JsonObject item){
    execPool.execute(new Runnable() {
      @Override
      public void run() {
        LogItemTransformer.transformToLog4j(item, parser);
      }
    });
  }

}
