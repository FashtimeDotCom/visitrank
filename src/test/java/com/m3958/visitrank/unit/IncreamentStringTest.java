package com.m3958.visitrank.unit;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.Utils.IncreamentString;

public class IncreamentStringTest {
  
  @Test
  public void t1(){
    IncreamentString is = new IncreamentString();
    System.out.println("09AZaz");
    System.out.println((int)'0');
    System.out.println((int)'9');
    System.out.println((int)'A');
    System.out.println((int)'Z');
    System.out.println((int)'a');
    System.out.println((int)'z');
    Assert.assertEquals("b0", is.getNext("az"));
    Assert.assertEquals("00000", is.getNext("zzzz"));
    Assert.assertEquals("ab", is.getNext("aa"));
    Assert.assertEquals("b", is.getNext("a"));
  }
  
  @Test
  public void t2(){
    IncreamentString is = new IncreamentString("auz");
    Assert.assertEquals("av0", is.getNext());
    Assert.assertEquals("av1", is.getNext());
    Assert.assertEquals("av2", is.getNext());
  }
}
