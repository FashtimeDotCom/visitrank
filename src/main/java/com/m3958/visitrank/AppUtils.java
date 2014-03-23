package com.m3958.visitrank;

public class AppUtils {
  
//  public static String getHourlyDbName(String filename){
//    int idx = filename.lastIndexOf('.');
//    if(idx != -1){
//      return filename.substring(0, idx);
//    }
//    return filename;
//  }
  
  public static String getDailyDbName(String filename){
    int idx = filename.lastIndexOf('-');
    if(idx != -1){
      return filename.substring(0, idx);
    }
    return filename;
  }
  
  public static void main(String[] args) {
//    System.out.println(getHourlyDbName("t-2014-03-02-1.log"));
    System.out.println(getDailyDbName("t-2014-03-02-1.log"));
  }
  
}
