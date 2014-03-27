package com.m3958.visitrank.unit;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.AppUtils;
import com.m3958.visitrank.LogCheckVerticle;
import com.m3958.visitrank.testutils.TestUtils;

public class CheckRemainLogFileTest {

  private String testlogname = "t-2014-03-02-01.log";
  private String logDir = "testlogs";
  private String archiveDir = "tarchives";

  @Before
  public void setup() throws IOException {
    TestUtils.deleteDirs(logDir, archiveDir);
    TestUtils.createDirs(logDir, archiveDir);
    TestUtils.createSampleLogs(logDir, testlogname);
  }

  @After
  public void cleanup() throws IOException {
    AppUtils.releaseLock(testlogname);
    TestUtils.deleteDirs(logDir, archiveDir);
  }


  @Test
  public void t() {
    String fn = new LogCheckVerticle.RemainLogFileFinder(logDir).findOne();
    Assert.assertEquals("t-2014-03-02-01.log", fn);

    fn = new LogCheckVerticle.RemainLogFileFinder(logDir).findOne();
    Assert.assertNull(fn);
  }

}
