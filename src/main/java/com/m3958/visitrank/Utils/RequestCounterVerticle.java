package com.m3958.visitrank.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;


public class RequestCounterVerticle implements Runnable {

  private long count;

  private String url;

  private int threadNum;

  public RequestCounterVerticle(String url, long count, int threadNum) {
    this.count = count;
    this.url = url;
    this.threadNum = threadNum;
  }

  @Override
  public void run() {
    long start = System.currentTimeMillis();
    for (; count > 0; count--) {
      String s = requestOne();
      if (s == null) {
        System.out.println("failure");
      }
    }
    System.out.println("thread number " + threadNum + " costs: "
        + (System.currentTimeMillis() - start) + "ms");
  }

  private String requestOne() {
    try {
      return Request.Get(url).setHeader("referer", "http://aaa.fh.gov.cn").execute()
          .handleResponse(new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException,
                IOException {
              Reader reader = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
              char[] buffer = new char[1024];
              int num;
              StringBuilder sb = new StringBuilder();
              while ((num = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, num);
              }
              return sb.toString();
            }
          });
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    long start = System.currentTimeMillis();

    String url = "http://localhost:8333?out=wholesite";
    long perThread = 10000;
    int totalThread = 10;

    int concurrency = 10;

    int loopNum = totalThread / concurrency;
    int tcount = 0;
    for (int j = 0; j < loopNum; j++) {
      ExecutorService executor = Executors.newFixedThreadPool(concurrency);
      for (int i = 0; i < concurrency; i++) {
        Runnable worker = new RequestCounterVerticle(url, perThread, tcount++);
        executor.execute(worker);
      }
      executor.shutdown();
      while (!executor.isTerminated()) {}
    }

    System.out.println("total costs: " + (System.currentTimeMillis() - start) + "ms");
  }

}
