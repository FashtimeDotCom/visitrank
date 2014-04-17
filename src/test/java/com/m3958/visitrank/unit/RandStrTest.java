package com.m3958.visitrank.unit;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

public class RandStrTest {

  @Test
  public void t1(){
    String s = RandomStringUtils.random(8, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    Assert.assertEquals(8, s.length());
    System.out.println(s);
  }
}
