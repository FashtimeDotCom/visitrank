package com.m3958.visitrank.unit;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class CljLoaderTest {

  private String cljname = "first-clj-prj/core.clj";

  @Test
  public void t3() throws IOException {
    String src = Resources.toString(Resources.getResource("test.clj"), Charsets.UTF_8);
    Assert.assertEquals("Hello Clj", src);
    clojure.lang.Compiler.load(new StringReader("(all-ns)"));
  }

  @Test
  public void t1() throws IOException {
    long start = System.currentTimeMillis();
    RT.loadResourceScript(cljname);
    System.out.println(clojure.lang.Compiler.load(new StringReader("(all-ns)")));
    Var main = findVar("first-clj-prj/core", "main");
    Object result = main.invoke();
    System.out.println(result);
    System.out.println("costs:" + (System.currentTimeMillis() - start) + "ms");
  }

  @Test
  public void t2() throws IOException {
    long start = System.currentTimeMillis();
    Var main = findVar("first-clj-prj/core", "main");
    Object result = main.invoke();
    System.out.println(result);
    System.out.println("costs:" + (System.currentTimeMillis() - start) + "ms");
  }
  
  private Var findVar(String nsName,String varName){
    Namespace ns = Namespace.find(Symbol.create(nsName));//another is findOrCreate.
    return ns.findInternedVar(Symbol.create(varName)); //maybe return null,ns.intern(Symbol sym) will create one,if not exist.
  }
}
