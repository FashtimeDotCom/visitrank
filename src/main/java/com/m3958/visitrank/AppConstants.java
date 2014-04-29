package com.m3958.visitrank;

public class AppConstants {
  public static String HOTEST_FIX = "~hotest";
  
  public static String DEPLOYED_SHARED_MAP = "deployed.map";

  public static String TEST_CONF_KEY = "appConfigJson";
  public static String PARTIAL_POSTFIX = ".partial";

  public static class MongoNames {
    public static String PAGE_VISIT_COL_NAME = "pagevisit";

    public static String HOST_NAME_COLLECTION_NAME = "hostname";

  }

  public static class MapReduceFunctionName {
    public static String MAP = "mapfunc";
    public static String REDUCE = "reducefunc";
    public static String FINALIZE = "finalizefunc";
  }

  public static class MapReduceColName {
    public static String COUNT_TEN_M = "count10m";
  }

}
