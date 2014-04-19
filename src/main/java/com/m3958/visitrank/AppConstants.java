package com.m3958.visitrank;

import java.util.regex.Pattern;

import org.vertx.java.core.json.JsonObject;

public class AppConstants {
  public static String HOTEST_FIX = "~hotest";
  public static String MOD_REDIS_ADDRESS = "visit_counter.redis";
  public static String MONGO_ADDRESS = "visit_counter.mongodb";

  public static boolean ONLY_LOG = false;

  public static String LINE_SEP = System.lineSeparator();
  
//  public static int LOGITEM_POOL_SIZE = 20;

  public static int HTTP_PORT = 8333;
  public static int HTTP_INSTANCE = 1;
  public static int REDIS_PORT = 6379;
  public static int REDIS_INSTANCE = 1;
  public static int MONGODB_PORT = 27017;
  public static int MONGODB_INSTANCE = 1;
  public static int SAVETO_MONGO_INSTANCE = 1;

  public static int LOG_SAVER_INSTANCE = 10;

  public static int LOG_PROCESSOR_INSTANCE = 1;

  public static String WRITE_CONCERN = "0,0,false,true,true"; // WriteConcern(int w, int wtimeout,
                                                              // boolean fsync, boolean j, boolean
                                                              // continueOnError)
  public static String REDIS_HOST = "localhost";

  public static String MONGODB_HOST = "localhost";

  public static String REDIS_MODULE_NAME = "io.vertx~mod-redis~1.1.3";

  public static String MONGODB_MODULE_NAME = "io.vertx~mod-mongo-persistor~2.1.1";

  public static String DEPLOIED_ID_SHARE_MAP = "deploiedidmap";

  public static String PARTIAL_POSTFIX = ".partial";

  public static int LOGFILE_READ_GAP = 100;

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


    LOGFILE_READ_GAP = config.getInteger("logfilereadgap", LOGFILE_READ_GAP);

    WRITE_CONCERN = config.getString("writeconcern", WRITE_CONCERN);

    ONLY_LOG = config.getBoolean("onlylog", ONLY_LOG);

  }

  public static class MongoNames {
    public static String REPOSITORY_DB_NAME = "visitrank";
    public static String PAGE_VISIT_COL_NAME = "pagevisit";

    public static String META_DB_NAME = "visitrank-meta";
    public static String HOST_NAME_COLLECTION_NAME = "hostname";

  }

  public static class MapReduceFunctionName {
    public static String MAP = "mapfunc";
    public static String REDUCE = "reducefunc";
    public static String FINALIZE = "finalizefunc";
  }

  public static Pattern dailyDbPtn = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2})(.*)");
}
