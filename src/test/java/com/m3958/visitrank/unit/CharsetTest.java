package com.m3958.visitrank.unit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
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
  public void setup() throws IOException{
    if(Files.exists(fn)){
      Files.delete(fn);
      Files.createFile(fn);
    }

  }
  
  @After
  public void clean() throws IOException{
    Files.delete(fn);
  }
  
  @Test
  public void t1() throws IOException, InterruptedException{
    writesome();
    System.out.println(Charset.defaultCharset());
    System.out.println(System.getProperty("file.encoding"));
    
    RandomAccessFile raf = new RandomAccessFile(fn.toFile(), "r");
    String s = raf.readLine();
    s = new String(s.getBytes(),Charset.forName("UTF-8"));
    raf.close();
    Assert.assertEquals(zh, s);
  }
  
  private void writesome() throws IOException{
    OutputStreamWriter partialWriter =
        new OutputStreamWriter(new FileOutputStream(fn.toFile()), "UTF-8");
    PrintWriter pw = new PrintWriter(partialWriter);
    pw.println(zh);
    partialWriter.close();
    pw.close();
  }
}
