package com.m3958.visitrank.interf;

import org.vertx.java.core.logging.Logger;

import com.m3958.visitrank.Utils.AppConfig;

public interface TestableVerticle {
  public void deployMe(AppConfig appConfig,Logger log);
}
