package com.m3958.visitrank.unit;

import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;

public class AppUtilsTest {

  private AppConfig appConfig;

  @Before
  public void setup() throws UnknownHostException {
    appConfig =
        new AppConfig(AppUtils.loadJsonResourceContent(this.getClass(), "testconf.json"), true);
  }

  @After
  public void cleanup() {
    appConfig.closeMongoClient();
  }

  @Test
  public void t1() {
    Assert.assertEquals("2014-03-03",
        AppUtils.getDailyDbName("2014-03-03-05.log", appConfig.getDailyDbPtn()));
  }

  @Test
  public void t2() {
    Assert.assertEquals("2014-03-03",
        AppUtils.getDailyDbName("2014-03-03-55-06.log", appConfig.getDailyDbPtn()));
  }

  @Test
  public void t3() {
    Assert
        .assertEquals("-05.log", AppUtils.getHour("2014-03-03-05.log", appConfig.getDailyDbPtn()));
  }

  @Test
  public void t4() {
    Assert.assertEquals("-55-06.log",
        AppUtils.getHour("2014-03-03-55-06.log", appConfig.getDailyDbPtn()));
  }

}
