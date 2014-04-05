package com.m3958.visitrank;

import java.util.regex.Pattern;

import org.vertx.java.core.json.JsonObject;

public class AppConstants {
  public static String HOTEST_FIX = "~hotest";
  public static String MOD_REDIS_ADDRESS = "visit_counter.redis";
  public static String MOD_MONGO_PERSIST_ADDRESS = "visit_counter.mongodb";

  public static String DAILY_PARTIAL_DIR = "dailycopypartial";
  
  public static boolean ONLY_LOG = false;
  
  public static String LINE_SEP = System.getProperty("line.separator");

  public static int HTTP_PORT = 8333;
  public static int HTTP_INSTANCE = 1;
  public static int REDIS_PORT = 6379;
  public static int REDIS_INSTANCE = 1;
  public static int MONGODB_PORT = 27017;
  public static int MONGODB_INSTANCE = 1;
  public static int SAVETO_MONGO_INSTANCE = 1;

  public static int DAILY_PROCESSOR_INSTANCE = 1;

  public static int LOG_PROCESSOR_INSTANCE = 1;

  public static String WRITE_CONCERN = "0,0,false,true,true"; // WriteConcern(int w, int wtimeout,
                                                              // boolean fsync, boolean j, boolean
                                                              // continueOnError)


  public static String REDIS_HOST = "localhost";

  public static String MONGODB_HOST = "localhost";

  public static String REDIS_MODULE_NAME = "io.vertx~mod-redis~1.1.3";

  public static String MONGODB_MODULE_NAME = "io.vertx~mod-mongo-persistor~2.1.1";

  public static String MAIN_VERTICLE_NAME = "com.m3958.visitrank.MainVerticle";

  public static String COUNTER_VERTICLE_NAME = "com.m3958.visitrank.CounterVerticle";

  public static String LOGCHECK_VERTICLE_NAME = "com.m3958.visitrank.LogCheckVerticle";

  public static String DEPLOIED_ID_SHARE_MAP = "deploiedidmap";

  public static String PARTIAL_POSTFIX = ".partial";

  public static int LOGFILE_READ_GAP = 100;

  public static int DAILY_DB_READ_GAP = 1000;

  public static void initConfigConstants(JsonObject config) {

    HTTP_PORT = config.getInteger("httpport", HTTP_PORT);
    REDIS_PORT = config.getInteger("redisport", REDIS_PORT);
    MONGODB_PORT = config.getInteger("mongodbport", MONGODB_PORT);

    HTTP_INSTANCE = config.getInteger("httpinstance", HTTP_INSTANCE);
    REDIS_INSTANCE = config.getInteger("redisinstance", REDIS_INSTANCE);
    MONGODB_INSTANCE = config.getInteger("mongodbinstance", MONGODB_INSTANCE);
    SAVETO_MONGO_INSTANCE = config.getInteger("savetomongoverticleinstance", SAVETO_MONGO_INSTANCE);

    REDIS_MODULE_NAME = config.getString("redismodulename", REDIS_MODULE_NAME);
    MONGODB_MODULE_NAME = config.getString("mongodbmodulename", MONGODB_MODULE_NAME);

    REDIS_HOST = config.getString("redishost", REDIS_HOST);

    MONGODB_HOST = config.getString("mongodbhost", MONGODB_HOST);

    DAILY_PROCESSOR_INSTANCE = config.getInteger("dailyprocessinstance", DAILY_PROCESSOR_INSTANCE);

    LOG_PROCESSOR_INSTANCE = config.getInteger("logprocessorinstance", LOG_PROCESSOR_INSTANCE);

    LOGFILE_READ_GAP = config.getInteger("logfilereadgap", LOGFILE_READ_GAP);
    DAILY_DB_READ_GAP = config.getInteger("dailydbreadgap", DAILY_DB_READ_GAP);

    WRITE_CONCERN = config.getString("writeconcern", WRITE_CONCERN);
    
    ONLY_LOG = config.getBoolean("onlylog", ONLY_LOG);

  }

  public static class MongoNames {
    public static String REPOSITORY_DB_NAME = "visitrank";
    public static String PAGE_VISIT_COL_NAME = "pagevisit";
    public static String HOURLY_JOB_COL_NAME = "hourlyjob";
    public static String HOURLY_JOB_NUMBER_KEY = "hournumber";
    public static String HOURLY_JOB_STATUS_KEY = "status";

    public static String DAILY_JOB_COL_NAME = "dailyjob";
    public static String DAILY_JOB_STATUS_KEY = "status";
  }
  
  public static Pattern dailyDbPtn = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2})(.*)");
}
