package com.m3958.visitrank.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.AppConstants;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class AppUtils {

  public static String getDailyDbName(String filename, Pattern dailyDbPtn) {
    Matcher m = dailyDbPtn.matcher(filename);
    if (m.matches()) {
      return m.group(1);
    } else {
      return null;
    }
  }

  public static String getHour(String filename, Pattern dailyDbPtn) {
    Matcher m = dailyDbPtn.matcher(filename);
    if (m.matches()) {
      return m.group(2);
    } else {
      return null;
    }
  }

  public static long getLastPartialPosition(Path partialLogPath) {
    String[] lines = new FileLineReader(partialLogPath.toString()).getLastLines(1);
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

  /**
   * this method record everything from client.
   * 
   * @param req
   * @return
   */
  public static JsonObject getParamsHeadersOb(HttpServerRequest req) {
    JsonObject jo = new JsonObject();

    JsonObject headerJo = new JsonObject();

    for (Map.Entry<String, String> header : req.headers().entries()) {
      String key = header.getKey();
      String value = header.getValue();
      if ("referer".equalsIgnoreCase(key)) {
        jo.putString(FieldNameAbbreviation.URL, value);
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
    jo.putNumber(FieldNameAbbreviation.TS, new Date().getTime()).putObject("headers", headerJo);

    return jo;
  }

  public static boolean colExist(MongoClient mongoClient, DB db, String colname) {
    Set<String> cols = db.getCollectionNames();
    for (String c : cols) {
      if (colname.equals(c)) {
        return true;
      }
    }
    return false;
  }

  public static String toUtf(String wrongs) throws UnsupportedEncodingException {
    char[] buffer = wrongs.toCharArray();
    byte[] b = new byte[buffer.length];
    for (int i = 0; i < b.length; i++) {
      b[i] = (byte) buffer[i];
    }
    return new String(b, "UTF-8");
  }

  public static void moveLogFiles(String logDir, String archiveDir, String filename,
      Path logfilePath) throws IOException {
    Path archiedPath = Paths.get(archiveDir);
    if (!archiedPath.toFile().exists()) {
      Files.createDirectories(archiedPath);
    }

    Path tp = archiedPath.resolve(filename);
    while (Files.exists(tp)) {
      tp = Paths.get(tp.toString() + ".duplicated");
    }

    Files.move(logfilePath, tp);

    if (Files.exists(Paths.get(logDir, filename + AppConstants.PARTIAL_POSTFIX),
        LinkOption.NOFOLLOW_LINKS)) {
      Files.delete(Paths.get(logDir, filename + AppConstants.PARTIAL_POSTFIX));
    }
  }

}
