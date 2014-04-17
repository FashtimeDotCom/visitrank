package com.m3958.visitrank.unit;

import java.net.UnknownHostException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.Utils.FieldNameAbbreviation;
import com.m3958.visitrank.Utils.IndexBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class IndexerTest {

  public String indexDbname = "index-test";
  
  @After
  public void cleanup() throws UnknownHostException{
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    mongoClient.dropDatabase(indexDbname);
    mongoClient.close();
  }

  @Test
  public void t1() throws UnknownHostException {
    IndexBuilder.hostNameIndex(indexDbname);
    MongoClient mongoClient;
    mongoClient = new MongoClient(AppConstants.MONGODB_HOST, AppConstants.MONGODB_PORT);
    DB db = mongoClient.getDB(indexDbname);
    DBCollection col = db.getCollection(AppConstants.MongoNames.HOST_NAME_COLLECTION_NAME);
    List<DBObject> idxes = col.getIndexInfo();
    Assert.assertEquals(3, idxes.size());
    for(DBObject dbo : idxes){
      String name = (String) dbo.get("name");
      if("h_1".equals(name) || "s_1".equals(name)){
        Assert.assertTrue((boolean) dbo.get("unique"));
      }
    }
    col.insert(new BasicDBObject(FieldNameAbbreviation.HostName.HOST,"www.m3958.com").append(FieldNameAbbreviation.HostName.HOST_SHORT, "0"));
    
    try {
      col.insert(new BasicDBObject(FieldNameAbbreviation.HostName.HOST,"www.m3958.com").append(FieldNameAbbreviation.HostName.HOST_SHORT, "0"));
    } catch (Exception e) {
    }
    mongoClient.close();
  }
}
