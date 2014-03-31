package com.m3958.visitrank;

import java.nio.file.Path;
import java.util.Date;
import java.util.Map;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.Utils.FileTailer;

public class AppUtils {

//  private static Pattern logdbPat = Pattern.compile(".*\\d{4}-\\d{2}-\\d{2}");

  private static String tfilename = "t-2014-03-02-1.log";

//  private static int logProcessorRemains = 0;
//
//  private static int dailyProcessorRemains = 0;
//
//  private static boolean processorNumberInited = false;

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

//  public static boolean isDailyDb(String dbname) {
//    Matcher m = logdbPat.matcher(dbname);
//    return m.matches();
//  }

  public static void main(String[] args) {
    System.out.println(getDailyDbName(tfilename));
    System.out.println(getHour(tfilename));
  }

//  public static synchronized void initProcessorRemains() {
//    if (!processorNumberInited) {
//      processorNumberInited = true;
//      AppUtils.logProcessorRemains = AppConstants.LOG_PROCESSOR_INSTANCE;
//      AppUtils.dailyProcessorRemains = AppConstants.DAILY_COPY_INSTANCE;
//    }
//  }
//  public static synchronized int logProcessorRemainsGetSet(int i) {
//    if(!processorNumberInited){
//      initProcessorRemains();
//    }
//    if (i == -1) {
//      AppUtils.logProcessorRemains += 1;
//    } else if (i == 1) {
//      AppUtils.logProcessorRemains -= 1;
//    }
//    return AppUtils.logProcessorRemains;
//  }
//
//  public static synchronized int dailyProcessorRemainsGetSet(int i) {
//    if(!processorNumberInited){
//      initProcessorRemains();
//    }
//    if (i == -1) {
//      AppUtils.dailyProcessorRemains += 1;
//    } else if (i == 1) {
//      AppUtils.dailyProcessorRemains -= 1;
//    }
//    return AppUtils.dailyProcessorRemains;
//  }

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
  
  public static JsonObject getParamsHeadersOb(HttpServerRequest req) {
    JsonObject jo = new JsonObject();

    JsonObject headerJo = new JsonObject();

    for (Map.Entry<String, String> header : req.headers().entries()) {
      String key = header.getKey();
      String value = header.getValue();
      if ("referer".equalsIgnoreCase(key)) {
        jo.putString("url", value);
      } else {
        headerJo.putString(key, value);
      }
    }

    for (Map.Entry<String, String> param : req.params().entries()) {
      String key = param.getKey();
      String value = param.getValue();
      jo.putString(key, value);
    }
    headerJo.putString("ip", req.remoteAddress().getAddress().getHostAddress());
    jo.putNumber("ts", new Date().getTime()).putObject("headers", headerJo);

    return jo;
  }
}
