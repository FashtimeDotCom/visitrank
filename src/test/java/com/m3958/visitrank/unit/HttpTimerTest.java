package com.m3958.visitrank.unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;


public class HttpTimerTest {

  @Test
  public void test(){
    Assert.assertTrue(true);
  }
  
  @Test
  public void t1() throws IOException {
    List<Metrics> metrics = new ArrayList<>();
    metrics.add(oneUrl("宁波政府站", "http://gtog.ningbo.gov.cn"));
    metrics.add(oneUrl("奉化", "http://www.fh.gov.cn"));
    metrics.add(oneUrl("大榭", "http://dx.ningbo.gov.cn"));
    metrics.add(oneUrl("大榭真实", "http://dx.ningbo.gov.cn/cn/default.html"));
    metrics.add(oneUrl("海曙", "http://www.haishu.gov.cn"));
    metrics.add(oneUrl("宁海", "http://nh.gov.cn/col/col2/index.html"));
//    metrics.add(oneUrl("镇海", "http://www.zh.gov.cn/index.htm"));
    metrics.add(oneUrl("海曙真實", "http://www.haishu.gov.cn/gov/"));
    metrics.add(oneUrl("寧海真實", "http://nh.gov.cn/col/col4/"));
    


    Comparator<Metrics> timecmp = new Comparator<Metrics>() {
      @Override
      public int compare(Metrics o1, Metrics o2) {
        return new Long(o1.time - o2.time).intValue();
      }
    };

    Comparator<Metrics> downspeedcmp = new Comparator<Metrics>() {
      @Override
      public int compare(Metrics o1, Metrics o2) {
        return new Long(o2.bytes / o2.time - o1.bytes / o1.time).intValue();
      }
    };


    printOut("按时间排序：", metrics, timecmp);
    printOut("按传输速度排序：", metrics, downspeedcmp);
  }

  private void printOut(String desc, List<Metrics> list, Comparator<Metrics> cmp) {
    Collections.sort(list, cmp);
    System.out.println(desc);
    for (Metrics m : list) {
      System.out.println(m.toString());
    }
  }

  private Metrics oneUrl(String name, String url) throws IOException {
    long start = System.currentTimeMillis();
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(url);
    CloseableHttpResponse response1 = httpclient.execute(httpGet);
    // The underlying HTTP connection is still held by the response object
    // to allow the response content to be streamed directly from the network socket.
    // In order to ensure correct deallocation of system resources
    // the user MUST either fully consume the response content or abort request
    // execution by calling CloseableHttpResponse#close().

    try {
      HttpEntity entity1 = response1.getEntity();
      // do something useful with the response body
      // and ensure it is fully consumed
      byte[] bytes = EntityUtils.toByteArray(entity1);
      EntityUtils.consume(entity1);
      return new Metrics(name, bytes.length, url, System.currentTimeMillis() - start);
    } finally {
      response1.close();
    }
  }

  public static class Metrics {
    private String name;
    private long bytes;
    private String url;
    private long time;

    public Metrics(String name, long bytes, String url, long time) {
      super();
      this.name = name;
      this.bytes = bytes;
      this.url = url;
      this.time = time;
    }

    @Override
    public String toString() {
      return name + "," + url + " use: " + time + "ms, bytes: " + bytes + ",每毫秒传输："
          + (bytes / time);
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public long getBytes() {
      return bytes;
    }

    public void setBytes(long bytes) {
      this.bytes = bytes;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public long getTime() {
      return time;
    }

    public void setTime(long time) {
      this.time = time;
    }
  }
}
