package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.LogProcessorWorkVerticle;
import com.m3958.visitrank.testutils.TestUtils;

public class LogProcessorTest {

  private String testlogname = "t-2014-03-02-01.log";
  private String logDir = "testlogs";
  private String archiveDir = "tarchives";

  @Before
  public void setup() throws IOException {
    TestUtils.deleteTestDirs(logDir, archiveDir);
    TestUtils.dropDailyDb(testlogname);
    TestUtils.createDirs(logDir, archiveDir);
    TestUtils.createSampleLogs(logDir, testlogname);
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.deleteTestDirs(logDir, archiveDir);
    TestUtils.dropDailyDb(testlogname);
  }

  @Test
  public void t() throws UnknownHostException {
    new LogProcessorWorkVerticle.LogProcessor(logDir, archiveDir, testlogname).process();
    Assert.assertTrue(Files.exists(Paths.get(archiveDir), LinkOption.NOFOLLOW_LINKS));
    Assert.assertTrue(Files.exists(Paths.get(archiveDir, testlogname), LinkOption.NOFOLLOW_LINKS));
    TestUtils.assertDbItemEqual(testlogname);
  }
}
