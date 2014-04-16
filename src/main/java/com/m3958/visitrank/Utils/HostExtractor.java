package com.m3958.visitrank.Utils;

public class HostExtractor {

  public static String getHost(String url) {
    String remain = stripPrefix(url);
    if (remain == null) {
      return null;
    }
    int slash = remain.indexOf('/');
    int qmark = remain.indexOf('?');

    if (slash == -1) {
      if (qmark == -1) { // www.m3958.com
        return remain;
      } else { // www.m3958.com?abc=xxx
        return remain.substring(0, qmark);
      }
    } else {
      if (qmark == -1) { // www.m3958.com/abc
        return remain.substring(0, slash);
      } else { // www.m3958.com/abc?u=x || www.m3958.com?abc=uuu/6677
        int i = slash > qmark ? qmark : slash;
        return remain.substring(0, i);
      }
    }
  }

  public static String getUri(String url) {
    String remain = stripPrefix(url);
    if (remain == null) {
      return "/";
    }
    int slash = remain.indexOf('/');
    int qmark = remain.indexOf('?');

    if (slash == -1) {
      if (qmark == -1) { // www.m3958.com
        return "/";
      } else { // www.m3958.com?abc=xxx
        return "/" + remain.substring(qmark);
      }
    } else {
      if (qmark == -1) { // www.m3958.com/abc
        return remain.substring(slash);
      } else { // www.m3958.com/abc?u=x || www.m3958.com?abc=uuu/6677
        boolean slashFirst = slash > qmark;
        int i = slashFirst ? qmark : slash;
        if (slashFirst) {
          return "/" + remain.substring(i);
        } else {
          return remain.substring(i);
        }
      }
    }
  }

  private static String stripPrefix(String url) {
    int i = url.indexOf("//");
    if (i == -1) {
      return null;
    }

    return url.substring(i + 2);
  }
}
