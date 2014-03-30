package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.LogCheckVerticle;
import com.m3958.visitrank.Utils.Locker;
import com.m3958.visitrank.testutils.TestUtils;

public class CheckRemainDbTest {

  private String dailydbname = "t-2014-03-02";
  
  private String newerdbname = "t-2014-03-03";
  
  private Locker locker;

  @Before
  public void setup() throws IOException {
    locker = new Locker();
    TestUtils.createSampleDailyDb(dailydbname, 10);
    TestUtils.dropSampleDailyDb(newerdbname);
  }

  @After
  public void cleanup() throws IOException {
    locker.releaseLock(dailydbname);
    locker = null;
    TestUtils.dropSampleDailyDb(dailydbname);
    TestUtils.dropSampleDailyDb(newerdbname);
  }


  @Test
  public void t() throws UnknownHostException {
    //has no newerdb.
    String fn = new LogCheckVerticle.RemainDailyDbFinder(locker).findOne("t-\\d{4}-\\d{2}-\\d{2}");
    Assert.assertNull(fn);
    locker.releaseLock(dailydbname);
    //has newerdb.
    TestUtils.createSampleDailyDb(newerdbname, 10);
    fn = new LogCheckVerticle.RemainDailyDbFinder(locker).findOne("t-\\d{4}-\\d{2}-\\d{2}");
    Assert.assertEquals(dailydbname, fn);
    locker.releaseLock(dailydbname);
    TestUtils.dropSampleDailyDb(newerdbname);
    fn = new LogCheckVerticle.RemainDailyDbFinder(locker).findOne("t-\\d{4}-\\d{2}-\\d{2}");
    Assert.assertNull(fn);
  }

}
