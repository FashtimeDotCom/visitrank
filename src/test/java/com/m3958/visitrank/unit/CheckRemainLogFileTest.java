package com.m3958.visitrank.unit;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.LogCheckVerticle;
import com.m3958.visitrank.Utils.Locker;
import com.m3958.visitrank.testutils.TestUtils;

public class CheckRemainLogFileTest {

  private String testlogname = "t-2014-03-02-01.log";
  private String logDir = "testlogs";
  private String archiveDir = "tarchives";

  private Locker locker;

  @Before
  public void setup() throws IOException {
    locker = new Locker();
    TestUtils.deleteDirs(logDir, archiveDir);
    TestUtils.createDirs(logDir, archiveDir);
    TestUtils.createSampleLogs(logDir, testlogname, 1000);
  }

  @After
  public void cleanup() throws IOException {
    locker.releaseLock(testlogname);
    locker = null;
    TestUtils.deleteDirs(logDir, archiveDir);
  }


  @Test
  public void t() {
    String fn = new LogCheckVerticle.RemainLogFileFinder(logDir, locker).findOne();
    Assert.assertEquals("t-2014-03-02-01.log", fn);

    fn = new LogCheckVerticle.RemainLogFileFinder(logDir, locker).findOne();
    Assert.assertNull(fn);
  }

}
