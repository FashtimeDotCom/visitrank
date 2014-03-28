package com.m3958.visitrank.unit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DoingLockFileTest {
  
  private String fn = "abc.doing";

  @Before
  public void setup() {
    File f = new File(fn);
    if (f.exists()) {
      f.delete();
    }
  }

  @After
  public void cleanup() {
    File f = new File(fn);
    if (f.exists()) {
      f.delete();
    }
  }
  
  @Test
  public void tpari(){
    Assert.assertEquals(0, Integer.parseInt("00"));
  }
  
  @Test
  public void tmap(){
    Map<String, String> m = new HashMap<>();
    Assert.assertEquals(0, m.size());
    m.put("a", "b");
    Assert.assertEquals(1, m.size());
    m.remove("a");
    Assert.assertEquals(0, m.size());
  }

  private FileLock getLock() {
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(fn);
      FileChannel writeOnlyChannel = fileOutputStream.getChannel();
//      fileOutputStream.close();//when stream closed,lock will release.
      return writeOnlyChannel.tryLock();
    } catch (FileNotFoundException e) {}
    catch (IOException e) {}
    catch (OverlappingFileLockException e){}
    return null;
  }

  @Test
  public void t() {
      Assert.assertNotNull(getLock());
      Assert.assertNull(getLock());
  }
}
