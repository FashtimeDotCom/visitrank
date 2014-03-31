package com.m3958.visitrank.unit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.m3958.visitrank.Utils.FileTailer;

public class PartialFileTailTest {

  private Path tp = Paths.get("partial.partial");

  @Before
  public void setup() throws IOException {
    if (Files.exists(tp)) {
      Files.delete(tp);
    }
  }

  @After
  public void cleanup() throws IOException {
    if (Files.exists(tp)) {
      Files.delete(tp);
    }
  }

  @Test
  public void t1() throws IOException {
    createFileEmpty();
    String[] ss = new FileTailer(tp.toString()).getLines(1);
    Assert.assertEquals(0, ss.length);
  }

  @Test
  public void t2() throws IOException {
    createFileEndWithNl();
    String[] ss = new FileTailer(tp.toString()).getLines(1);
    Assert.assertEquals("world", ss[0]);
  }

  @Test
  public void t4() throws IOException {
    createFileEndWithNl1();
    String[] ss = new FileTailer(tp.toString()).getLines(1);
    Assert.assertEquals("world", ss[0]);
  }

  @Test
  public void t5() throws IOException {
    createFileEndWithNlChinese();
    String[] ss = new FileTailer(tp.toString()).getLines(1);
    Assert.assertEquals("哈巴", ss[0]);
  }

  @Test
  public void t6() throws IOException {
    createFileEndWithNlChinese();
    String[] ss = new FileTailer(tp.toString()).getLines(2);
    Assert.assertEquals("狗", ss[0]);
    Assert.assertEquals("哈巴", ss[1]);
  }

  @Test
  public void t7() throws IOException {
    createFileOneline();
    String[] ss = new FileTailer(tp.toString()).getLines(2);
    Assert.assertEquals(1, ss.length);
    Assert.assertEquals("hello", ss[0]);
  }

  private void createFileEndWithNlChinese() throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("狗\n");
    lines.add("哈巴\n");
    lines.add("\n");
    lines.add("\n");
    lines.add("\n");
    lines.add("\n");
    lines.add("\n");
    lines.add("\n");
    Files.write(tp, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW);

  }

  @Test
  public void t3() throws IOException {
    createFileNoEndNl();
    String[] ss = new FileTailer(tp.toString()).getLines(1);
    Assert.assertEquals("world", ss[0]);
  }

  private void createFileOneline() throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("hello\n");
    Files.write(tp, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW);
  }

  private void createFileEndWithNl() throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("hello\n");
    lines.add("world\n");
    Files.write(tp, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW);
  }

  private void createFileEndWithNl1() throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("hello\n");
    lines.add("world\n");
    lines.add("\n");
    lines.add("\n");
    lines.add("\n");
    lines.add("\n");
    lines.add("\n");
    lines.add("\n");
    Files.write(tp, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW);
  }

  private void createFileNoEndNl() throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("hello\n");
    lines.add("world");
    Files.write(tp, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW);
  }

  private void createFileEmpty() throws IOException {
    Files.write(tp, new ArrayList<String>(), Charset.forName("UTF-8"),
        StandardOpenOption.CREATE_NEW);
  }

}
