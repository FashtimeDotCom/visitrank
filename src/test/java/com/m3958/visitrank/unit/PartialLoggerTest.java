package com.m3958.visitrank.unit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.logger.AppLogger;

public class PartialLoggerTest {

  @Test
  public void t1() {
    for (int i = 0; i < 10000*100; i++) {
      AppLogger.paritalLogger.info("abc" + i);
    }
//    try {
//      List<String> ss = Files.readAllLines(Paths.get("partiallog", "partial.log"), Charset.forName("UTF-8"));
//      Assert.assertEquals(10, ss.size());
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
    
  }
}
