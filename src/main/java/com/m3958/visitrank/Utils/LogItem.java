package com.m3958.visitrank.Utils;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class LogItem {

  private String line;
  
  public LogItem(String line){
    this.line = line;
  }
  
  public DBObject toDbObject(){
    return transform((DBObject) JSON.parse(line));
  }
  
  private DBObject transform(DBObject dbo){
    DBObject tdbo = new BasicDBObject();
    DBObject headers = (DBObject) dbo.get("headers");
    tdbo.put(FieldNameAbbreviation.URL_ABBREV, dbo.get(FieldNameAbbreviation.URL));
    tdbo.put(FieldNameAbbreviation.TS_ABBREV, new Date((long) dbo.get(FieldNameAbbreviation.TS)));
    
    tdbo.put(FieldNameAbbreviation.ACCEPT_ABBREV, headers.get(FieldNameAbbreviation.ACCEPT));
    
    tdbo.put(FieldNameAbbreviation.ACCEPT_LANGUAGE_ABBREV, headers.get(FieldNameAbbreviation.ACCEPT_LANGUAGE));
    tdbo.put(FieldNameAbbreviation.USER_AGENT_ABBREV, headers.get(FieldNameAbbreviation.USER_AGENT));
    tdbo.put(FieldNameAbbreviation.IP, getIp(headers));
    
    return tdbo;
  }
  
  private String getIp(DBObject headers){
    if(headers.containsField("X-Forwarded-For")){
      String xff = (String) headers.get("X-Forwarded-For");
      if(xff != null && !xff.isEmpty()){
        String[] ss = xff.split(",");
        if(ss.length > 0){
          return ss[0];
        }
      }
    }
    return (String) headers.get(FieldNameAbbreviation.IP);
  }
  
}
