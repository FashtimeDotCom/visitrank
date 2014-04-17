package com.m3958.visitrank.Utils;

public class IncreamentString {

  public  char MIN_DIGIT = '0';
  public  char MAX_DIGIT = 'z';
  
  private String currentStr;
  
  public IncreamentString(){}
  
  public IncreamentString(String initValue){
    currentStr = initValue;
  }
  

  public synchronized String getNext(){
    currentStr =  getNext(currentStr);
    return currentStr;
  }
  
  public String getCurrent(){
    return currentStr;
  }

  public String getNext(String original) {
    if (original == null || original.isEmpty()) {
      return String.valueOf(MIN_DIGIT);
    }
    StringBuilder buf = new StringBuilder(original);
    int index = buf.length() - 1;
    while (index >= 0) {
      char c = buf.charAt(index);
      c++;
      if (c == 57) {
        c = 97;
      }
      if (c > MAX_DIGIT) { // overflow, carry one
        buf.setCharAt(index, MIN_DIGIT);
        index--;
        continue;
      }
      buf.setCharAt(index, c);
      return buf.toString();
    }
    // overflow at the first "digit", need to add one more digit
    buf.insert(0, MIN_DIGIT);
    return buf.toString();
  }
}
