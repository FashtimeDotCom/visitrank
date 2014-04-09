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
import com.m3958.visitrank.LogProcessorWorkVerticle2;
import com.m3958.visitrank.testutils.TestUtils;

public class LogProcessor2Test {

  private String testlogname = "t-2014-03-02-01.log";
  private String logDir = "testlogs";
  private String archiveDir = "tarchives";
  private String testRepoDb = "t-visitrank";

  @Before
  public void setup() throws IOException {
    if(!Files.exists(Paths.get(logDir,testlogname + AppConstants.PARTIAL_POSTFIX))){
      TestUtils.deleteDirs(logDir, archiveDir);
      TestUtils.dropDb(testRepoDb);
      TestUtils.createDirs(logDir, archiveDir);
      TestUtils.createSampleLogs(logDir, testlogname, 1000);
    }
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.deleteDirs(logDir, archiveDir);
    TestUtils.dropDb(testRepoDb);
  }

  @Test
  public void t() throws UnknownHostException {
    AppConstants.MongoNames.REPOSITORY_DB_NAME = "t-visitrank";
    new LogProcessorWorkVerticle2.LogProcessor(logDir, archiveDir, testlogname,
        new JsonObject().putNumber("logfilereadgap", 100), 100).process();
    Assert.assertTrue(Files.exists(Paths.get(archiveDir), LinkOption.NOFOLLOW_LINKS));
    Assert.assertTrue(Files.exists(Paths.get(archiveDir, testlogname), LinkOption.NOFOLLOW_LINKS));
    TestUtils.assertDbItemEqual(testRepoDb,1000);
  }
}
