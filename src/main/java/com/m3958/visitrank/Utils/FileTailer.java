package com.m3958.visitrank.Utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class FileTailer {

  private RandomAccessFile fileHandler;
  private long filePointer;
  
  public FileTailer(String filepath) {
    super();
    try {
      this.fileHandler = new RandomAccessFile(filepath, "r");
      this.filePointer = fileHandler.length();
    } catch (IOException e) {
    }
  }
  
  public String[] getLinesIncludeEmpty(int lines){
    try {
      long fileLength = fileHandler.length();
      if (fileLength == 0) {
        return new String[] {};
      }
      filePointer = fileLength - 1;
      String[] lns = new String[lines];
      int maxIdx = lines - 1;
      for(int idx=maxIdx;idx > -1;idx--){
        lns[idx] = readOneLine();
      }
      return lns;
    } catch (java.io.FileNotFoundException e) {
      return null;
    } catch (java.io.IOException e) {
      return null;
    } finally {
      if (fileHandler != null) try {
        fileHandler.close();
      } catch (IOException e) {}
    }
  }
  
  /**
   * 
   * @param lines
   * @return exclude empty lines.
   */
  public String[] getLines(int lines){
    String[] ss = getLinesIncludeEmpty(lines);
    List<String> truelines = new ArrayList<String>();
    int count = 0;
    for(String s : ss){
      if(!s.trim().isEmpty()){
        count++;
        truelines.add(s.trim());
      }
    }
    return truelines.toArray(new String[count]);
  }
  
  private String readOneLine() throws IOException {
    // delete trailing newlines.
    for (; filePointer > -1; filePointer--) {
      fileHandler.seek(filePointer);
      byte readByte = fileHandler.readByte();
      if (readByte != 0xA && readByte != 0xD) {
        break;
      }
    }

    List<Byte> lb = new ArrayList<>();

    for (; filePointer > -1; filePointer--) {
      fileHandler.seek(filePointer);
      byte readByte = fileHandler.readByte();
      if (readByte == 0xA || readByte == 0xD) {
        break;
      }
      lb.add(readByte);
    }
    byte[] bytes = new byte[lb.size()];
    int maxIdx = lb.size() - 1;
    for (int idx = maxIdx; idx > -1; idx--) {
      bytes[idx] = lb.get(maxIdx - idx);
    }
    String lastLine = new String(bytes, "UTF-8");
    return lastLine;
  }
}
