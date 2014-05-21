package com.m3958.visitrank.unit;

public class CljTest {
  public static Object evalClj(String a) {
    return clojure.lang.Compiler.load(new java.io.StringReader(a));
  }

  public static void main(String[] args) {
    new clojure.lang.RT(); // needed since 1.5.0
    System.out.println(evalClj("(all-ns)"));
  }
}
