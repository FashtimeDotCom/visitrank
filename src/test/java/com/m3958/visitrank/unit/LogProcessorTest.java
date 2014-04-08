package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.LogProcessorWorkVerticle;
import com.m3958.visitrank.testutils.TestUtils;

public class LogProcessorTest {

  private String testlogname = "t-2014-03-02-01.log";
  private String logDir = "testlogs";
  private String archiveDir = "tarchives";

  @Before
  public void setup() throws IOException {
    TestUtils.deleteDirs(logDir, archiveDir);
    TestUtils.dropDailyDb(testlogname);
    TestUtils.createDirs(logDir, archiveDir);
    TestUtils.createSampleLogs(logDir, testlogname, 1000);
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.deleteDirs(logDir, archiveDir);
    TestUtils.dropDailyDb(testlogname);
  }

  @Test
  public void t() throws UnknownHostException {
    AppConstants.dailyDbPtn = Pattern.compile("(.*\\d{4}-\\d{2}-\\d{2})(.*)");
    new LogProcessorWorkVerticle.LogProcessor(logDir, archiveDir, testlogname,
        new JsonObject().putNumber("logfilereadgap", 1000), 100).process();
    Assert.assertTrue(Files.exists(Paths.get(archiveDir), LinkOption.NOFOLLOW_LINKS));
    Assert.assertTrue(Files.exists(Paths.get(archiveDir, testlogname), LinkOption.NOFOLLOW_LINKS));
    TestUtils.assertDailyDbItemEqual(testlogname);
  }
}
