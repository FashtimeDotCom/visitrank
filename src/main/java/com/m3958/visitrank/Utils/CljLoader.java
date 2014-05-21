package com.m3958.visitrank.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

public class CljLoader {
	
	public static Set<String> loaded = Sets.newHashSet();
	
	public static void load(String cljfilename){
		try {
			clojure.lang.Compiler.load((Resources.asCharSource(Resources.getResource(cljfilename), Charsets.UTF_8).openStream())); 
			loaded.add(cljfilename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
	  CljLoader.load("first-clj-prj/core.clj");
	}
	
}
