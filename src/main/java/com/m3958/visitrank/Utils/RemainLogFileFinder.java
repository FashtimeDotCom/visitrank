package com.m3958.visitrank.Utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.m3958.visitrank.AppConstants;

public class RemainLogFileFinder {
  private String logDirStr;

  private static Pattern fptn = Pattern.compile(".*\\d{4}-\\d{2}-\\d{2}.*\\.log");

  private Locker locker;

  public RemainLogFileFinder(String logDirStr, Locker locker) {
    this.logDirStr = logDirStr;
    this.locker = locker;
  }

  public String findOne() {
    File logDir = new File(logDirStr);
    String[] files = logDir.list();

    String pf = findLogHasPartial(files);

    if (pf != null) {
      if(locker.canLockLog(pf)){
        return pf;
      }else{
        return null;
      }
    }
    for (String f : files) {
      Matcher m = fptn.matcher(f);
      if (f.endsWith("log") && m.matches()) { // find log file.
        if (locker.canLockLog(f)) {
          return f;
        }
      }
    }
    return null;
  }

  private String findLogHasPartial(String[] list) {
    for (String s : list) {
      if (s.endsWith(AppConstants.PARTIAL_POSTFIX)) {
        return s.substring(0, s.length() - AppConstants.PARTIAL_POSTFIX.length());
      }
    }
    return null;
  }
}
