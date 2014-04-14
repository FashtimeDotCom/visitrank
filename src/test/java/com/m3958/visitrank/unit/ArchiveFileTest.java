package com.m3958.visitrank.unit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.Utils.AppUtils;
import com.m3958.visitrank.testutils.TestUtils;

public class ArchiveFileTest {

  private String sampleName = "archive.log";

  private String archiveDir = "t-archives";

  private String logDir = "t-logs";

  @Before
  public void setup() throws IOException {
    TestUtils.deleteDirs(archiveDir, logDir);
    TestUtils.createDirs(archiveDir, logDir);
    Files.createFile(Paths.get(archiveDir, sampleName));
    Files.createFile(Paths.get(archiveDir, sampleName + ".duplicated"));
    Files.createFile(Paths.get(archiveDir, sampleName + ".duplicated.duplicated"));
    Files.createFile(Paths.get(logDir, sampleName));
    List<String> ss = Arrays.asList("a", "b");
    Files.write(Paths.get(logDir, sampleName), ss, Charset.forName("UTF-8"));
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.deleteDirs(archiveDir, logDir);
  }

  @Test
  public void t1() throws IOException {
    AppUtils.moveLogFiles(logDir, archiveDir, sampleName, Paths.get(logDir, sampleName));
    Assert.assertTrue(Files.exists(Paths.get(archiveDir, sampleName + ".duplicated.duplicated.duplicated")));
  }
}
