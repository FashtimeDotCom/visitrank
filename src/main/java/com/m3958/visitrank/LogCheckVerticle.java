package com.m3958.visitrank;

import org.vertx.java.core.Handler;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class LogCheckVerticle extends Verticle {

  @Override
  public void start() {
    final Logger log = container.logger();
    
    long timerID = vertx.setPeriodic(30000, new Handler<Long>() {
      public void handle(Long timerID) {
        log.info("And every second this is printed");
      }
    });

    log.info("First this is printed");
  }

}
