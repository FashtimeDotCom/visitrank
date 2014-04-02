package com.m3958.visitrank;

import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.Utils.FileTailer;

public class AppUtils {

  private static String tfilename = "t-2014-03-02-1.log";

  private static Pattern dailyDbPtn = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2})(.*)");

  public static String getDailyDbName(String filename) {
    Matcher m = dailyDbPtn.matcher(filename);
    if (m.matches()) {
      return m.group(1);
    } else {
      return null;
    }
  }

  public static String getHour(String filename) {
    Matcher m = dailyDbPtn.matcher(filename);
    if (m.matches()) {
      return m.group(2);
    } else {
      return null;
    }
  }

  public static void main(String[] args) {
    System.out.println(getDailyDbName(tfilename));
    System.out.println(getHour(tfilename));
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
