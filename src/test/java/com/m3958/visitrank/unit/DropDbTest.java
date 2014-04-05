package com.m3958.visitrank.unit;

import java.io.IOException;
import java.net.UnknownHostException;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.testutils.TestUtils;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class DropDbTest {

  private String testdb = "tttdb";
  
  @Before
  public void setup() throws IOException {
    TestUtils.dropDb(testdb);
    TestUtils.createSampleDb(testdb, 10);
  }

  @After
  public void cleanup() throws IOException {
    TestUtils.dropDb(testdb);
  }
  
  @Test
  public void t1() throws UnknownHostException{
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(testdb);
    DBCollection hourlyCol = db.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
    DBCursor cursor = hourlyCol.find();
    boolean hexist = cursor.hasNext();
    cursor.close();
    mongoClient.close();
    Assert.assertFalse(hexist);
  }
  
  @Test
  public void t2() throws UnknownHostException{
    TestUtils.dropDb(testdb);
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(testdb);
    DBCollection hourlyCol = db.getCollection(AppConstants.MongoNames.HOURLY_JOB_COL_NAME);
    DBCursor cursor = hourlyCol.find();
    boolean hexist = cursor.hasNext();
    cursor.close();
    mongoClient.close();
    Assert.assertFalse(hexist);
  }
  
}
