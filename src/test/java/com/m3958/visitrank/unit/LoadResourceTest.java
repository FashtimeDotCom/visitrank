package com.m3958.visitrank.unit;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.Utils.AppUtils;

public class LoadResourceTest {

  @Test
  public void t1() throws IOException {
    List<String> lines = AppUtils.resourceLoader2(this.getClass(), "/mrfuncs/lr.txt");
    Assert.assertEquals(1, lines.size());
    Assert.assertEquals("a", lines.get(0));
  }
}
