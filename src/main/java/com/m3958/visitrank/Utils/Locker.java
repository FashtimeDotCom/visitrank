package com.m3958.visitrank.Utils;

import java.util.HashMap;
import java.util.Map;

public class Locker {
  private Map<String, String> lockMap = new HashMap<>();
  
  public synchronized boolean canLockLog(String filename) {
    if (lockMap.containsKey(filename)) {
      return false;
    } else {
      lockMap.put(filename, "yes");
      return true;
    }
  }

  public void releaseLock(String filename) {
    lockMap.remove(filename);
  }
  
}
