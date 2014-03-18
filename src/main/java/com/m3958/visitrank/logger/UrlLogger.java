package com.m3958.visitrank.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UrlLogger {

  static final Logger urllogger = LogManager.getLogger("urllogger");


  // Set up a simple configuration that logs on the console.
  public static void main(String[] args) {

    urllogger.trace("Entering application.");
    urllogger.info("abc");
    urllogger.error("Didn't do it.");

    urllogger.trace("Exiting application.");
  }

}
