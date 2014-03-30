package com.m3958.visitrank.Utils;

public  class RemainsCounter {
  
  private int remains;
  
  public RemainsCounter(int initRemains){
    this.remains = initRemains;
  }
  
  public synchronized int remainsGetSet(int i) {
    if (i == -1) {
      remains += 1;
    } else if (i == 1) {
      remains -= 1;
    }
    return remains;
  }
}
