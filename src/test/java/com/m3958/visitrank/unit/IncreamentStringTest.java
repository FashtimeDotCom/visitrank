package com.m3958.visitrank.unit;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.Utils.IncreamentString;

public class IncreamentStringTest {

  @Test
  public void t1(){
    String s = "az";
    Assert.assertEquals("ba", IncreamentString.incrementedAlpha(s));
  }
  
  @Test
  public void t2(){
    String s = "aa";
    Assert.assertEquals("ab", IncreamentString.incrementedAlpha(s));
  }
  
  @Test
  public void t3(){
    String s = "a";
    Assert.assertEquals("b", IncreamentString.incrementedAlpha(s));
  }
  
  @Test
  public void t4(){
    String s = "zzzz";
    Assert.assertEquals("aaaaa", IncreamentString.incrementedAlpha(s));
  }



}
