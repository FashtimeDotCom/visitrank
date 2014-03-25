package com.m3958.visitrank;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {

  private static Pattern logdbPat = Pattern.compile(".*\\d{4}-\\d{2}-\\d{2}");

  private static Map<String, String> pickupLockMap = new HashMap<>();

  private static String tfilename = "t-2014-03-02-1.log";

  public static String getDailyDbName(String filename) {
    int idx = filename.lastIndexOf('-');
    if (idx != -1) {
      return filename.substring(0, idx);
    }
    return filename;
  }

  public static int getHour(String filename) {
    String[] ss = filename.split("-");
    return Integer.parseInt(ss[ss.length - 1].split("\\.")[0]);
  }

  public static boolean isDailyDb(String dbname) {
    Matcher m = logdbPat.matcher(dbname);
    return m.matches();
  }

  public static void main(String[] args) {
    System.out.println(getDailyDbName(tfilename));
    System.out.println(getHour(tfilename));
    System.out.println(isDailyDb(getDailyDbName(tfilename)));
  }

  public static synchronized boolean canLockLog(String filename) {
    if (pickupLockMap.containsKey(filename)) {
      return false;
    } else {
      pickupLockMap.put(filename, "yes");
      return true;
    }
  }

  public static void releaseLock(String logDir, String filename) {
    pickupLockMap.remove(filename);
  }
}
