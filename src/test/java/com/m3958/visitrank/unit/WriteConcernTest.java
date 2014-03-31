package com.m3958.visitrank.unit;


import org.junit.Assert;
import org.junit.Test;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.LogCheckVerticle;
import com.mongodb.WriteConcern;

public class WriteConcernTest {

  @Test
  public void t(){
    String s = "0,0,false,true,true";
    JsonObject jo = new LogCheckVerticle.WriteConcernParser(s).parse();
    WriteConcern wc = LogCheckVerticle.WriteConcernParser.getWriteConcern(jo);
    Assert.assertEquals(0, wc.getW());
    Assert.assertEquals(0, wc.getWtimeout());
    Assert.assertFalse(wc.getFsync());
    Assert.assertFalse(!wc.getJ());
    Assert.assertFalse(!wc.getContinueOnErrorForInsert());
  }
}
