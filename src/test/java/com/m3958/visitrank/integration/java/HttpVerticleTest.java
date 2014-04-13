package com.m3958.visitrank.integration.java;

/*
 * 
 * @author jianglibo@gmail.com
 */

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.http.HttpClient;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.testutils.HttpTestVerticle;
import com.m3958.visitrank.testutils.HttpTestVerticleResponseHandler;

/**
 */
public class HttpVerticleTest extends TestVerticle {

  @Test
  public void t1() throws ClientProtocolException, IOException {

    String c =
        Request.Get("http://localhost:" + TestConstants.HTTP_PORT + "?out=wholesite&dowhat=url").execute()
            .returnContent().asString();
    System.out.println(c);
    Assert.assertTrue(c.startsWith("http://"));
    VertxAssert.testComplete();
  }


  @Override
  public void start() {
    // Make sure we call initialize() - this sets up the assert stuff so
    // assert functionality works correctly
    initialize();
    // Deploy the module - the System property `vertx.modulename` will
    // contain the name of the module so you
    // don't have to hardecode it in your tests
    container.deployVerticle(HttpTestVerticle.VERTICLE_NAME, new AsyncResultHandler<String>() {
      @Override
      public void handle(AsyncResult<String> asyncResult) {
        // Deployment is asynchronous and this this handler will
        // be called when it's complete (or failed)
        assertTrue(asyncResult.succeeded());
        assertNotNull("deploymentID should not be null", asyncResult.result());
        // If deployed correctly then start the tests!
        startTests();
      }
    });
  }

}
