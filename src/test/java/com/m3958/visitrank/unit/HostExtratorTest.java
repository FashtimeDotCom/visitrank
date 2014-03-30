package com.m3958.visitrank.unit;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.Utils.HostExtractor;

public class HostExtratorTest {

  @Test
  public void t(){
    String url1 = "http://www.m3958.com";
    String url11 = "https://www.m3958.com";
    
    String url2 = "http://www.m3958.com?abc=xxx";
    String url22 = "https://www.m3958.com?abc=xxx";
    
    String url3 = "http://www.m3958.com/abc";
    String url33 = "https://www.m3958.com/abc";
    
    String url4 = "http://www.m3958.com/abc?u=x";
    String url44 = "https://www.m3958.com/abc?u=x";
    
    String url5 = "http://www.m3958.com?abc=uuu/6677";
    String url55 = "https://www.m3958.com?abc=uuu/6677";
    
    
    Assert.assertEquals("www.m3958.com", HostExtractor.getHost(url1));
    Assert.assertEquals("www.m3958.com", HostExtractor.getHost(url11));
    Assert.assertEquals("www.m3958.com", HostExtractor.getHost(url2));
    Assert.assertEquals("www.m3958.com", HostExtractor.getHost(url22));
    Assert.assertEquals("www.m3958.com", HostExtractor.getHost(url3));
    Assert.assertEquals("www.m3958.com", HostExtractor.getHost(url33));
    Assert.assertEquals("www.m3958.com", HostExtractor.getHost(url4));
    Assert.assertEquals("www.m3958.com", HostExtractor.getHost(url44));
    Assert.assertEquals("www.m3958.com", HostExtractor.getHost(url5));
    Assert.assertEquals("www.m3958.com", HostExtractor.getHost(url55));
  }
}
