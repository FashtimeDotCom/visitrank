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
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.LogProcessorWorkVerticle;
import com.m3958.visitrank.testutils.TestUtils;

/**
 * I debug this test,then break it at half of processing,then run this test again,test must pass.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class PartialLogTest {

  private String testlogname = "t-2014-03-02-01.log";
  private String logDir = "testlogs";
  private String archiveDir = "tarchives";

  @Before
  public void setup() throws IOException {
    if (!Files.exists(Paths.get(logDir, testlogname + AppConstants.PARTIAL_POSTFIX))) {
      TestUtils.deleteDirs(logDir, archiveDir);
      TestUtils.createDirs(logDir, archiveDir);
      TestUtils.createSampleLogs(logDir, testlogname, 1000);
      TestUtils.dropDailyDb(testlogname);
    }

  }

  @After
  public void cleanup() throws IOException {
    if (!Files.exists(Paths.get(logDir, testlogname + AppConstants.PARTIAL_POSTFIX))) {
      TestUtils.deleteDirs(logDir, archiveDir);
      TestUtils.dropDailyDb(testlogname);
    }
  }

  @Test
  public void t() throws UnknownHostException {
    new LogProcessorWorkVerticle.LogProcessor(logDir, archiveDir, testlogname,
        new JsonObject().putNumber("logfilereadgap", 10)).process();
    Assert.assertTrue(Files.exists(Paths.get(archiveDir), LinkOption.NOFOLLOW_LINKS));
    Assert.assertTrue(Files.exists(Paths.get(archiveDir, testlogname), LinkOption.NOFOLLOW_LINKS));
    TestUtils.assertDailyDbItemEqual(testlogname);
  }

}
