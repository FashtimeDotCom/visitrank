package com.m3958.visitrank.unit;

import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.Utils.AppUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class DbCollectionTest {

  @Test
  public void t1() throws UnknownHostException {
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB("not-exist-db");
    Assert.assertFalse(AppUtils.colExist(mongoClient, db, "not-exist-col"));
    mongoClient.close();
  }
}
