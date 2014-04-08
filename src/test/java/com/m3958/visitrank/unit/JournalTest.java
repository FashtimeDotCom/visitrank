package com.m3958.visitrank.unit;


import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.testutils.TestUtils;

public class JournalTest {

  private int times = 1000;

  @Test
  public void t1() throws UnknownHostException, InterruptedException {
    System.out.println("journal true,step 5000:");
    TestUtils.createSampleDb("ttt", times, true, 5000);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb("ttt");
  }

  @Test
  public void t2() throws UnknownHostException, InterruptedException {
    System.out.println("journal false,step 5000:");
    TestUtils.createSampleDb("ttt", times, false, 5000);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb("ttt");
  }

  @Test
  public void t3() throws UnknownHostException, InterruptedException {
    System.out.println("journal true,step 5000:");
    TestUtils.createSampleDb("ttt", times, true, 5000);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb("ttt");
  }

  @Test
  public void t4() throws UnknownHostException, InterruptedException {
    System.out.println("journal false,step 5000:");
    TestUtils.createSampleDb("ttt", times, false, 5000);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb("ttt");
  }
  
  @Test
  public void t5() throws UnknownHostException, InterruptedException {
    System.out.println("journal false,step 1:");
    TestUtils.createSampleDb("ttt", times, false, 1);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb("ttt");
  }
  
  @Test
  public void t6() throws UnknownHostException, InterruptedException {
    System.out.println("journal false,step 10:");
    TestUtils.createSampleDb("ttt", times, false, 10);
    Thread.sleep(100);
    Assert.assertTrue(true);
    TestUtils.dropDb("ttt");
  }

}
