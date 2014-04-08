package com.m3958.visitrank.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;

public class PartialUtil {

  private Path partialLogPath;

  private OutputStreamWriter partialWriter;

  public PartialUtil(Path partialLogPath) {
    this.partialLogPath = partialLogPath;
    try {
      this.partialWriter =
          new OutputStreamWriter(new FileOutputStream(partialLogPath.toFile()), "UTF-8");
    } catch (UnsupportedEncodingException | FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void startPartial() throws UnsupportedEncodingException, FileNotFoundException {

  }

  public void endPartial() throws UnsupportedEncodingException, FileNotFoundException {

  }
}
