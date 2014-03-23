package com.m3958.visitrank.unit;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.LogCheckVerticle;

public class CheckRemainLogFileTest {

  @Before
  public void setup() {
    File[] files = new File("testlogs").listFiles();
    for (File f : files) {
      if (f.getName().endsWith(".doing")) {
        f.delete();
      }
    }
  }

  @After
  public void cleanup() {
    File[] files = new File("testlogs").listFiles();
    for (File f : files) {
      if (f.getName().endsWith(".doing")) {
        f.delete();
      }
    }
  }

  @Test
  public void t() {
    String f = new LogCheckVerticle.RemainLogFileFinder("testlogs").findOne();
    Assert.assertEquals("t-2014-03-02-01.log", f);

    f = new LogCheckVerticle.RemainLogFileFinder("testlogs").findOne();
    Assert.assertNull(f);
  }

}
