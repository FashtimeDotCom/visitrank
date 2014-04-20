package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.LogProcessorWorkVerticle;
import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.Utils.LogItemTransformer;
import com.m3958.visitrank.testutils.TestUtils;

public class LogProcessorTest {

  private String testlogname = "t-2014-03-02-01.log";

  private static AppConfig appConfig;

  @BeforeClass
  public static void sss() throws IOException {
    appConfig =
        new AppConfig(AppUtils.loadJsonResourceContent(BatchCopyTestNo.class, "testconf.json"));
  }

  @AfterClass
  public static void ccc() throws UnknownHostException {
    appConfig.closeMongoClient();
  }

  @Before
  public void setup() throws IOException {
    if (!Files.exists(Paths.get(appConfig.getLogDir(), testlogname + AppConstants.PARTIAL_POSTFIX))) {
      TestUtils.deleteDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
      TestUtils.dropDb(appConfig, appConfig.getRepoDbName());
      TestUtils.createDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
      TestUtils.createSampleLogs(appConfig.getLogDir(), testlogname, 1000);
    }
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.deleteDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
    TestUtils.dropDb(appConfig, appConfig.getRepoDbName());
  }

  @Test
  public void t() throws UnknownHostException {
    LogItemTransformer logItemTransformer = new LogItemTransformer(appConfig);
    new LogProcessorWorkVerticle.LogProcessor(appConfig, logItemTransformer, testlogname).process();
    Assert.assertTrue(Files.exists(appConfig.getLogPath()));
    Assert.assertTrue(Files.exists(Paths.get(appConfig.getArchiveDir(), testlogname),
        LinkOption.NOFOLLOW_LINKS));
    TestUtils.assertDbItemEqual(appConfig, appConfig.getRepoDbName(), 1000);
  }
}
