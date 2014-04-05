package com.m3958.visitrank.Utils;

public class IncreamentString {

  public final static char MIN_DIGIT = 'a';
  public final static char MAX_DIGIT = 'z';

  public static String incrementedAlpha(String original) {
    if(original == null || original.isEmpty()){
      return String.valueOf(MIN_DIGIT);
    }
    StringBuilder buf = new StringBuilder(original);
    int index = buf.length() - 1;
    while (index >= 0) {
      char c = buf.charAt(index);
      c++;
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
