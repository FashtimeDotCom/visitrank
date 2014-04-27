package com.m3958.visitrank.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.vertx.java.core.json.JsonObject;

import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

/**
 * Because vertx message type is limited.So use this class.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class AppConfig {

  private String archiveDir;

  private String mongoHost;

  private int mongoPort;

  private int mongoInstance;

  private String logDir;

  private String mongoAddress;

  private String mongoModuleName;

  private int httpInstance;

  private int httpPort;

  private int redisInstance;

  private String redisAddress;

  private String redisModuleName;

  private int redisPort;

  private String charset;

  private int logFileReadGap;

  private boolean onlyLog;

  private String redisHost;

  private int logSaverInstance;

  private WriteConcern writeConcern;

  private String repoDbName;

  private Pattern dailyDbPtn;

  private JsonObject confJson;

  private MongoClient mongoClient;

  private String metaDbName;

  private String mrDbName;

  private String mrFuncFolder;
  
  private String mrSrcDbName;

  public AppConfig(JsonObject jo, boolean createMongoClient) {
    setConfJson(jo);
    setArchiveDir(jo.getString("archiveDir"));
    setCharset(jo.getString("charset"));
    setHttpInstance(jo.getInteger("httpInstance"));
    setHttpPort(jo.getInteger("httpPort"));
    setLogDir(jo.getString("logDir"));
    setLogFileReadGap(jo.getInteger("logFileReadGap"));
    setLogSaverInstance(jo.getInteger("logSaverInstance"));
    setMongoAddress(jo.getString("mongoAddress"));
    setMongoHost(jo.getString("mongoHost"));
    setMongoInstance(jo.getInteger("mongoInstance"));
    setMongoModuleName(jo.getString("mongoModuleName"));
    setMongoPort(jo.getInteger("mongoPort"));
    setOnlyLog(jo.getBoolean("onlyLog"));
    setRedisAddress(jo.getString("redisAddress"));
    setRedisHost(jo.getString("redisHost"));
    setRedisInstance(jo.getInteger("redisInstance"));
    setRedisModuleName(jo.getString("redisModuleName"));
    setRedisPort(jo.getInteger("redisPort"));
    setWriteConcern(WriteConcernParser.getWriteConcern(jo.getObject("writeConcern")));
    setRepoDbName(jo.getString("repoDbName"));
    setDailyDbPtn(Pattern.compile(jo.getString("dailyDbPtn")));
    if (createMongoClient) {
      try {
        setMongoClient(new MongoClient(getMongoHost(), getMongoPort()));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    setMetaDbName(jo.getString("metaDbName"));
    setMrDbName(jo.getString("mrDbName"));
    setMrFuncFolder(jo.getString("mrFuncFolder"));
    setMrSrcDbName(jo.getString("mrSrcDbName"));
  }

  public String getMongoHost() {
    return mongoHost;
  }

  public int getMongoPort() {
    return mongoPort;
  }

  public int getMongoInstance() {
    return mongoInstance;
  }

  public void setMongoInstance(int mongoInstance) {
    this.mongoInstance = mongoInstance;
  }

  public String getMongoAddress() {
    return mongoAddress;
  }

  public void setMongoAddress(String mongoAddress) {
    this.mongoAddress = mongoAddress;
  }

  public String getMongoModuleName() {
    return mongoModuleName;
  }

  public void setMongoModuleName(String mongoModuleName) {
    this.mongoModuleName = mongoModuleName;
  }

  public int getHttpInstance() {
    return httpInstance;
  }

  public void setHttpInstance(int httpInstance) {
    this.httpInstance = httpInstance;
  }

  public int getRedisInstance() {
    return redisInstance;
  }

  public void setRedisInstance(int redisInstance) {
    this.redisInstance = redisInstance;
  }

  public String getRedisAddress() {
    return redisAddress;
  }

  public void setRedisAddress(String redisAddress) {
    this.redisAddress = redisAddress;
  }

  public String getRedisModuleName() {
    return redisModuleName;
  }

  public void setRedisModuleName(String redisModuleName) {
    this.redisModuleName = redisModuleName;
  }

  public int getRedisPort() {
    return redisPort;
  }

  public void setRedisPort(int redisPort) {
    this.redisPort = redisPort;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public int getLogFileReadGap() {
    return logFileReadGap;
  }

  public void setLogFileReadGap(int logFileReadGap) {
    this.logFileReadGap = logFileReadGap;
  }

  public boolean isOnlyLog() {
    return onlyLog;
  }

  public void setOnlyLog(boolean onlyLog) {
    this.onlyLog = onlyLog;
  }

  public String getRedisHost() {
    return redisHost;
  }

  public void setRedisHost(String redisHost) {
    this.redisHost = redisHost;
  }

  public int getLogSaverInstance() {
    return logSaverInstance;
  }

  public void setLogSaverInstance(int logSaverInstance) {
    this.logSaverInstance = logSaverInstance;
  }

  public void setMongoHost(String mongoHost) {
    this.mongoHost = mongoHost;
  }

  public void setMongoPort(int mongoPort) {
    this.mongoPort = mongoPort;
  }

  public int getHttpPort() {
    return httpPort;
  }

  public void setHttpPort(int httpPort) {
    this.httpPort = httpPort;
  }

  public String getLogDir() {
    return logDir;
  }

  public Path getLogPath() {
    return Paths.get(logDir);
  }


  public void setLogDir(String logDir) {
    this.logDir = logDir;
  }

  public String getArchiveDir() {
    return archiveDir;
  }

  public Path getArchivePath() {
    return Paths.get(archiveDir);
  }

  public void setArchiveDir(String archiveDir) {
    this.archiveDir = archiveDir;
  }

  public WriteConcern getWriteConcern() {
    return writeConcern;
  }

  public void setWriteConcern(WriteConcern writeConcern) {
    this.writeConcern = writeConcern;
  }

  public String getRepoDbName() {
    return repoDbName;
  }

  public void setRepoDbName(String repoDbName) {
    this.repoDbName = repoDbName;
  }

  public Pattern getDailyDbPtn() {
    return dailyDbPtn;
  }

  public void setDailyDbPtn(Pattern dailyDbPtn) {
    this.dailyDbPtn = dailyDbPtn;
  }

  public JsonObject getConfJson() {
    return confJson;
  }

  public void setConfJson(JsonObject confJson) {
    this.confJson = confJson;
  }

  public MongoClient getMongoClient() {
    return mongoClient;
  }

  public void setMongoClient(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  public void closeMongoClient() {
    if (getMongoClient() != null) {
      getMongoClient().close();
    }
  }

  public String getMetaDbName() {
    return metaDbName;
  }

  public void setMetaDbName(String metaDbName) {
    this.metaDbName = metaDbName;
  }

  public String getMrDbName() {
    return mrDbName;
  }

  public void setMrDbName(String mrDbName) {
    this.mrDbName = mrDbName;
  }

  public String getMrFuncFolder() {
    return mrFuncFolder;
  }

  public void setMrFuncFolder(String mrFuncFolder) {
    this.mrFuncFolder = mrFuncFolder;
  }

  public String getMrSrcDbName() {
    return mrSrcDbName;
  }

  public void setMrSrcDbName(String mrSrcDbName) {
    this.mrSrcDbName = mrSrcDbName;
  }
}
