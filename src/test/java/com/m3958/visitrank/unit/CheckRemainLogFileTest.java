package com.m3958.visitrank.unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.Utils.Locker;
import com.m3958.visitrank.Utils.RemainLogFileFinder;
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
  public void t1() {
    String fn = new RemainLogFileFinder(logDir, locker).findOne();
    Assert.assertEquals("t-2014-03-02-01.log", fn);

    fn = new RemainLogFileFinder(logDir, locker).findOne();
    Assert.assertNull(fn);
  }

  @Test
  public void t2() throws IOException {
    samples(1);
    Path d = Paths.get(logDir);

    String[] ss = d.toFile().list();
    Arrays.sort(ss);
    Assert.assertEquals("2014-03-02-05.log", ss[0]);
    Assert.assertEquals("2014-03-03-01.log", ss[3]);
    samples(-1);
  }



  @Test
  public void t3() throws IOException {
    samples(1);
    Locker locker = new Locker();
    RemainLogFileFinder rlf = new RemainLogFileFinder(logDir, locker);
    Assert.assertEquals("2014-03-02-05.log", rlf.findOne());
    Assert.assertEquals("2014-03-02-10.log", rlf.findOne());
    samples(-1);
  }

  @Test
  public void t4() throws IOException {
    samples(1);
    Files.createFile(Paths.get(logDir, "2014-03-02-17.log" + AppConstants.PARTIAL_POSTFIX));
    Locker locker = new Locker();
    RemainLogFileFinder rlf = new RemainLogFileFinder(logDir, locker);
    Assert.assertEquals("2014-03-02-17.log", rlf.findOne());
    Assert.assertNull(rlf.findOne());
    Files.delete(Paths.get(logDir, "2014-03-02-17.log" + AppConstants.PARTIAL_POSTFIX));
    Assert.assertEquals("2014-03-02-05.log", rlf.findOne());
    samples(-1);
  }

  @Test
  public void t5() throws IOException {
    samples(1);
    Locker locker = new Locker();
    Path p1 = Paths.get(logDir, "t-2014-03-03-09.log");
    Files.createFile(p1);
    RemainLogFileFinder rlf = new RemainLogFileFinder(logDir, locker);
    Assert.assertEquals("t-2014-03-03-0a.log", rlf.nextLogName());
    Files.delete(p1);
    samples(-1);
  }

  @Test
  public void t6() {
    System.out.println((int) '0');
    System.out.println((int) '9');
    System.out.println((int) 'a');
    System.out.println((int) 'z');
  }

  @Test
  public void t7() {
    String[] ss = new String[] {"2014-03-03-01.log", "2014-03-02-05.log", "t-2014-03-02-01.log"};
    Arrays.sort(ss);
    Assert.assertEquals("2014-03-02-05.log", ss[0]);
    Assert.assertEquals("2014-03-03-01.log", ss[1]);
    Assert.assertEquals("t-2014-03-02-01.log", ss[2]);
  }

  private void samples(int c) throws IOException {
    Path p1 = Paths.get(logDir, "2014-03-03-01.log");
    Path p2 = Paths.get(logDir, "2014-03-02-05.log");
    Path p3 = Paths.get(logDir, "2014-03-02-10.log");
    Path p4 = Paths.get(logDir, "2014-03-02-17.log");
    if (c == 1) {
      Files.createFile(p1);
      Files.createFile(p2);
      Files.createFile(p3);
      Files.createFile(p4);
    } else {
      Files.delete(p1);
      Files.delete(p2);
      Files.delete(p3);
      Files.delete(p4);
    }
  }

}
