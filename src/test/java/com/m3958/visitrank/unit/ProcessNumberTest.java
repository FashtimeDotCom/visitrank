package com.m3958.visitrank.unit;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.AppUtils;

public class ProcessNumberTest {
  
  @Test
  public void t(){
    AppUtils.initProcessorRemains();
    Assert.assertEquals(5, AppUtils.logProcessorRemainsGetSet(0));
    Assert.assertEquals(3, AppUtils.dailyProcessorRemainsGetSet(0));
    AppUtils.logProcessorRemainsGetSet(1);
    AppUtils.dailyProcessorRemainsGetSet(1);
    Assert.assertEquals(4, AppUtils.logProcessorRemainsGetSet(0));
    Assert.assertEquals(2, AppUtils.dailyProcessorRemainsGetSet(0));
    
    AppUtils.logProcessorRemainsGetSet(-1);
    AppUtils.dailyProcessorRemainsGetSet(-1);
    Assert.assertEquals(5, AppUtils.logProcessorRemainsGetSet(0));
    Assert.assertEquals(3, AppUtils.dailyProcessorRemainsGetSet(0));


  }
}
