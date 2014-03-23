package com.m3958.visitrank;

import org.vertx.java.core.json.JsonObject;

public class AppConstants {
  public static String HOTEST_FIX = "~hotest";
  public static String MOD_REDIS_ADDRESS = "visit_counter.redis";
  public static String MOD_MONGO_PERSIST_ADDRESS = "visit_counter.mongodb";

  public static int HTTP_PORT = 8333;
  public static int HTTP_INSTANCE = 1;
  public static int REDIS_PORT = 6379;
  public static int REDIS_INSTANCE = 1;
  public static int MONGODB_PORT = 27017;
  public static int MONGODB_INSTANCE = 1;
  public static int SAVETO_MONGO_INSTANCE = 1;

  public static String REDIS_HOST = "localhost";
  
  public static String MONGODB_HOST = "localhost";

  public static String REDIS_MODULE_NAME = "io.vertx~mod-redis~1.1.3";

  public static String MONGODB_MODULE_NAME = "io.vertx~mod-mongo-persistor~2.1.1";
  
  
  public static String DEPLOIED_ID_SHARE_MAP = "deploiedidmap";

  public static void initConfigConstants(JsonObject config) {

      HTTP_PORT = config.getInteger("httpport",HTTP_PORT);
      REDIS_PORT = config.getInteger("redisport",REDIS_PORT);
      MONGODB_PORT = config.getInteger("mongodbport",MONGODB_PORT);


      HTTP_INSTANCE = config.getInteger("httpinstance",HTTP_INSTANCE);
      REDIS_INSTANCE = config.getInteger("redisinstance",REDIS_INSTANCE);
      MONGODB_INSTANCE = config.getInteger("mongodbinstance",MONGODB_INSTANCE);
      SAVETO_MONGO_INSTANCE = config.getInteger("savetomongoverticleinstance",SAVETO_MONGO_INSTANCE);

      REDIS_MODULE_NAME = config.getString("redismodulename",REDIS_MODULE_NAME);
      MONGODB_MODULE_NAME = config.getString("mongodbmodulename",MONGODB_MODULE_NAME);

      REDIS_HOST = config.getString("redishost",REDIS_HOST);
      
      MONGODB_HOST = config.getString("mongodbhost",MONGODB_HOST);

  }
  
  public static class MongoNames{
    public static String TOTAL_DB_NAME = "visitrannk";
    public static String PAGE_VISIT_COL_NAME = "pagevisit";
    public static String STATUS_COL_NAME = "writestatus";
    public static String STATUS_COL_KEY = "complete";
  }

}
