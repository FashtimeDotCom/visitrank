package com.m3958.visitrank.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vertx.java.core.json.JsonObject;

public class UrlPersistorLogger {

  public static final Logger urlPersistor = LogManager.getLogger("UrlPersistor");


  // Set up a simple configuration that logs on the console.
  public static void main(String[] args) {
    JsonObject jo = new JsonObject().putString("abc", "hello world").putNumber("number", 55);
    urlPersistor.trace("Entering application.");
    urlPersistor.warn(jo);
    urlPersistor.trace("Exiting application.");
  }

}
