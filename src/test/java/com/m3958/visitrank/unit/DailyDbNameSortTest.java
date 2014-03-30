package com.m3958.visitrank.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DailyDbNameSortTest {

  private List<String> list;

  @Before
  public void setup() {
    list = Arrays.asList("2014-03-24", "2014-03-02", "2014-03-05", "2014-03-03", "2015-01-01");
  }

  @Test
  public void t() {
    Collections.sort(list);
    Assert.assertEquals("2014-03-02", list.get(0));
    Assert.assertEquals("2015-01-01", list.get(list.size() - 1));
  }

  @Test
  public void t1(){
    Collections.sort(list);
    List<String> list1 = new ArrayList<>(list.size());
    
    Iterator<String> it = list.iterator();
    while(it.hasNext()){
      list1.add(it.next());
    }
    Assert.assertArrayEquals(list.toArray(), list1.toArray());
    
    it = list.iterator();
    String nonext = null;
    
    while(it.hasNext()){
      String s = it.next();
      if(!it.hasNext()){
        nonext = s;
      }
    }
    
    Assert.assertEquals("2015-01-01", nonext);
  }
}
