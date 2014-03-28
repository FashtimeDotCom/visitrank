package com.m3958.visitrank;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.m3958.visitrank.Utils.FileTailer;

public class AppUtils {

  private static Pattern logdbPat = Pattern.compile(".*\\d{4}-\\d{2}-\\d{2}");

  private static Map<String, String> pickupLockMap = new HashMap<>();
  
  private static String tfilename = "t-2014-03-02-1.log";
  
  private static int logProcessorRemains = 0;
  
  private static int dailyProcessorRemains = 0;

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
  
  public static void initLogProcessorRemains(int i){
    logProcessorRemains = i;
  }
  
  public static void initDailyProcessorRemains(int i){
    dailyProcessorRemains = i;
  }
  
  public static synchronized int logProcessorRemainsGetSet(int i){
    if(i == -1){
      logProcessorRemains += 1;
    }else if(i == 1){
      logProcessorRemains -= 1;
    }
    return logProcessorRemains;
  }
  
  public static synchronized int dailyProcessorRemainsGetSet(int i){
    if(i == -1){
      dailyProcessorRemains += 1;
    }else if(i == 1){
      dailyProcessorRemains -= 1;
    }
    return dailyProcessorRemains;
  }

  public static synchronized boolean canLockLog(String filename) {
    if (pickupLockMap.containsKey(filename)) {
      return false;
    } else {
      pickupLockMap.put(filename, "yes");
      return true;
    }
  }

  public static void releaseLock(String filename) {
    pickupLockMap.remove(filename);
  }
  

  public static long getLastPartialPosition(Path partialLogPath) {
    String[] lines = new FileTailer(partialLogPath.toString()).getLines(1);
    if (lines.length == 1) {
      String[] ss = lines[0].split(",");
      if (ss.length == 2) { // 1000,1000 means 1000 has write to mongodb.
        return Long.parseLong(ss[0], 10);
      } else { // 1000, means 1000 - gap to 1000 has not write to mongodb.
        return Long.parseLong(ss[0], 10) - AppConstants.LOGFILE_READ_GAP;
      }
    }
    return 0;
  }
}
