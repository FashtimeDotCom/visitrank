package com.m3958.visitrank.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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

  public RemainLogFileFinder(String logDirStr) {
    this.logDirStr = logDirStr;
  }

  public String findOne() {
    File logDir = new File(logDirStr);
    String[] files = logDir.list();

    String pf = findLogHasPartial(files);

    if (pf != null) {
      if (locker.canLockLog(pf)) {
        return pf;
      } else {
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

  public String nextLogName() {
    File logDir = new File(logDirStr);
    String[] files = logDir.list();
    String lf = null;
    for (String f : files) {
      Matcher m = fptn.matcher(f);
      if (f.endsWith("log") && m.matches()) { // find log file.
        lf = f;
      }
    }
    if (lf == null) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
      return sdf.format(new Date()) + ".log";
    } else {
      char c = lf.charAt(lf.length() - 5);
      if (c == '9') {
        c = 'a';
      } else if (c == 'z') {
        c = '0';
      } else {
        c++;
      }
      String s = lf.substring(0, lf.length() - 5);
      return new StringBuffer().append(s).append(c).append(".log").toString();

    }
  }
}
