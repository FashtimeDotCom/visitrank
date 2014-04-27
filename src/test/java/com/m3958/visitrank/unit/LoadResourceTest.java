package com.m3958.visitrank.unit;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.Utils.AppUtils;

public class LoadResourceTest {

  @Test
  public void t1() throws IOException {
    List<String> lines = AppUtils.loadResourceLines(this.getClass(), "/mrfuncs/count10m.js");
    Assert.assertEquals("//mapfunc:", lines.get(0));
  }
}
