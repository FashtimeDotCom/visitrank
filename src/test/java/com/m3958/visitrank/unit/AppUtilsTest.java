package com.m3958.visitrank.unit;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.Utils.AppUtils;

public class AppUtilsTest {

  @Test
  public void t1() {
    Assert.assertEquals("2014-03-03",
        AppUtils.getDailyDbName("2014-03-03-05.log", AppConstants.dailyDbPtn));
  }

  @Test
  public void t2() {
    Assert.assertEquals("2014-03-03",
        AppUtils.getDailyDbName("2014-03-03-55-06.log", AppConstants.dailyDbPtn));
  }

  @Test
  public void t3() {
    Assert.assertEquals("-05.log", AppUtils.getHour("2014-03-03-05.log", AppConstants.dailyDbPtn));
  }

  @Test
  public void t4() {
    Assert.assertEquals("-55-06.log",
        AppUtils.getHour("2014-03-03-55-06.log", AppConstants.dailyDbPtn));
  }

}
