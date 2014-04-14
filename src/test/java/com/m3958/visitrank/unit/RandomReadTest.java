package com.m3958.visitrank.unit;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.junit.Assert;
import org.junit.Test;

import com.mongodb.util.JSON;

public class RandomReadTest {
  
  Path p = Paths.get("2014-04-14-08-29.log");

//  @Test
//  public void t1() throws IOException {
//    if (Files.exists(p)) {
//      long start = System.currentTimeMillis();
//      String line;
//      RandomAccessFile raf = new RandomAccessFile(p.toFile(), "r");
//      while ((line = raf.readLine()) != null) {
//        JSON.parse(line);
//      }
//      raf.close();
//      System.out.println("random read costs:" + (System.currentTimeMillis() - start) + "ms");
//    }
//    Assert.assertTrue(true);
//  }
  
  @Test
  public void t2() throws IOException{
    if(Files.exists(p)){
      long start = System.currentTimeMillis();
      String line;
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(p.toFile()),
              "UTF-8"));
      
      while ((line = reader.readLine()) != null) {
        JSON.parse(line);
      }
      reader.close();
      System.out.println("BufferedReader read costs:" + (System.currentTimeMillis()-start) + "ms");
    }
    Assert.assertTrue(true);
  }
}
