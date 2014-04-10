package com.m3958.visitrank.unit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CharsetTest {

  private String zh = "中文";

  private Path fn = Paths.get("cs001.txt");

  @Before
  public void setup() throws IOException {
    if (Files.exists(fn)) {
      Files.delete(fn);
      Files.createFile(fn);
    }

  }

  @After
  public void clean() throws IOException {
    Files.delete(fn);
  }

  @Test
  public void t1() throws IOException, InterruptedException {
    writesome();
    // System.out.println(Charset.defaultCharset());
    // System.out.println(System.getProperty("file.encoding"));
    RandomAccessFile raf = new RandomAccessFile(fn.toFile(), "r");
    String s = raf.readLine();
    raf.close();
    Assert.assertEquals('中', zh.charAt(0));
    String us = new String(tobytes(s),"UTF-8");
    Assert.assertEquals(zh.length(), us.length());
    Assert.assertEquals(zh, us);
    
    long start = System.currentTimeMillis();
    long times = 100000*1000;
    
    for(int i=0;i<times;i++){
      tobytes(s);
    }
    System.out.println("cost: " + (System.currentTimeMillis() - start) + "ms");
  }

  private byte[] tobytes(String s) {
    char[] buffer = s.toCharArray();
    byte[] b = new byte[buffer.length];
    for (int i = 0; i < b.length; i++) {
      b[i] = (byte) buffer[i];
    }
    return b;
  }

  private void writesome() throws IOException {
    OutputStreamWriter partialWriter =
        new OutputStreamWriter(new FileOutputStream(fn.toFile()), "UTF-8");
    PrintWriter pw = new PrintWriter(partialWriter);
    pw.println(zh);
    partialWriter.close();
    pw.close();
  }
}
