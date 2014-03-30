package com.m3958.visitrank.unit;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.Utils.RemainsCounter;

public class ProcessNumberTest {
  
  @Test
  public void t(){
    RemainsCounter mc = new RemainsCounter(5);
    
    Assert.assertEquals(5, mc.remainsGetSet(0));

    Assert.assertEquals(4, mc.remainsGetSet(1));
    Assert.assertEquals(3, mc.remainsGetSet(1));
    Assert.assertEquals(4, mc.remainsGetSet(-1));
  }
}
