package com.m3958.visitrank.unit;

import java.io.IOException;

import org.junit.Test;

import com.m3958.visitrank.uaparser.Client;
import com.m3958.visitrank.uaparser.Parser;

public class UaParserTest {

  @Test
  public void t1() throws IOException {
    String uaString = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36";
//        "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3";

    Parser uaParser = new Parser();
    Client c = uaParser.parse(uaString);
    System.out.println(c);
    System.out.println();
    System.out.println(c.userAgent);
    System.out.println(c.userAgent.family); // => "Mobile Safari"
    System.out.println(c.userAgent.major); // => "5"
    System.out.println(c.userAgent.minor); // => "1"

    System.out.println(c.os); // => "iOS"
    System.out.println(c.os.family); // => "iOS"
    System.out.println(c.os.major); // => "5"
    System.out.println(c.os.minor); // => "1"

    System.out.println(c.device.family); // => "iPhone"
    System.out.println(c.device);
  }
}
