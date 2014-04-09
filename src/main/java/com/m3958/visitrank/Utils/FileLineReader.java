package com.m3958.visitrank.Utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.vertx.java.core.json.JsonObject;

public class FileLineReader {

  private RandomAccessFile fileHandler;
  
//  private long filePointer;
  
  private long fileLength;
  private long gap;

  public FileLineReader(String filepath) {
    init(new File(filepath));
  }

  public FileLineReader(File file) {
    init(file);
  }

  public FileLineReader(Path file) {
    init(file.toFile());
  }

  public void init(File file) {
    try {
      this.fileHandler = new RandomAccessFile(file.toString(), "r");
      this.fileLength = file.length();
      this.gap = this.fileLength / 2;

    } catch (IOException e) {}
  }

  public String[] getLastLinesIncludeEmpty(int howmuch) {
    try {
      long fileLength = fileHandler.length();
      if (fileLength == 0) {
        return new String[] {};
      }
      String[] lns = new String[howmuch];
      int maxIdx = howmuch - 1;
      long startPoint = fileLength;
      for (int idx = maxIdx; idx > -1; idx--) {
        FindLineResult flr = readOneLine(startPoint);
        startPoint = flr.getStart();
        if(flr != null){
          lns[idx] = flr.getLine();
        }
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
   * @param howmuch
   * @return exclude empty lines.
   */
  public String[] getLastLines(int howmuch) {
    String[] ss = getLastLinesIncludeEmpty(howmuch);
    List<String> truelines = new ArrayList<String>();
    int count = 0;
    for (String s : ss) {
      if (!s.trim().isEmpty()) {
        count++;
        truelines.add(s.trim());
      }
    }
    return truelines.toArray(new String[count]);
  }

  // public String searchLog(String u,long t) throws IOException{
  // this.fileHandler.readl
  // }

  public FindLineResult getLogItem(String u, long t) throws IOException {
    FindLineResult r = getLogItemPosition(u, t, -1);
    if (fileHandler != null) try {
      fileHandler.close();
    } catch (IOException e) {}
    return r;
  }

  private FindLineResult getLogItemPosition(String u, long t, long position) throws IOException {
    long p;
    if (position == -1) {
      p = this.fileLength / 2;
    } else {
      p = position;
    }
    if (p < 1 || p > this.fileLength) {
      return null;
    }

    gap = gap / 2;
    if (gap == 0) {
      gap = 1;
    }

    FindLineResult lr = getLineByPosition(p, false);
    if (lr == null) {
      return null;
    }
    JsonObject jo = new JsonObject(lr.getLine());
    if (u.equals(jo.getString("u")) && t == jo.getLong("t")) {
      return lr;
    } else {
      if (t > jo.getLong("t")) {
        return getLogItemPosition(u, t, p + gap);
      } else {
        return getLogItemPosition(u, t, p - gap);
      }
    }
  }

  public FindLineResult getLineByPosition(long fpPosition) throws IOException {
    return getLineByPosition(fpPosition, true);
  }

  private FindLineResult getLineByPosition(long fpPosition, boolean closeStream) throws IOException {
    FindLineResult lr;

    if (this.fileLength < 1) {
      lr = null;
    } else {
      long startPoint = fpPosition;
      // seek foward to newline or end of file.
      for (; startPoint < fileLength; startPoint++) {
        fileHandler.seek(startPoint);
        byte readByte = fileHandler.readByte();
        if (readByte == 0xA || readByte == 0xD) {
          break;
        }
      }
      lr = readOneLine(startPoint);
    }
    if (closeStream) {
      if (fileHandler != null) try {
        fileHandler.close();
      } catch (IOException e) {}
    }
    return lr;
  }

  /**
   * 
   * @return
   * @throws IOException
   */
  private FindLineResult readOneLine(long filePointer) throws IOException {
    // delete trailing newlines.
    if (filePointer == fileLength) {
      filePointer--;
    }
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
    return new FindLineResult(lastLine, filePointer + 1);
  }

  public static class FindLineResult {
    private String line;
    private long start;

    public FindLineResult(String line, long start) {
      this.line = line;
      this.setStart(start);
    }

    public String getLine() {
      return line;
    }

    public void setLine(String line) {
      this.line = line;
    }

    public long getStart() {
      return start;
    }

    public void setStart(long start) {
      this.start = start;
    }

    @Override
    public String toString() {
      return line + " - " + start;
    }
  }
}
