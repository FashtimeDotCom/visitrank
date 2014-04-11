package com.m3958.visitrank.integration.java;

/*
 * 
 * @author jianglibo@gmail.com
 */

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.http.HttpClient;
import org.vertx.testtools.TestVerticle;

import com.m3958.visitrank.testutils.HttpTestVerticle;
import com.m3958.visitrank.testutils.HttpTestVerticleResponseHandler;

/**
 */
public class HttpVerticleTest extends TestVerticle {

 
  @Test
  public void t1() {
    HttpClient client =
        vertx.createHttpClient().setPort(TestConstants.HTTP_PORT).setHost("localhost")
            .setMaxPoolSize(10);

    String url = "/?dowhat=url&xx=uu";
    client.getNow(url, new HttpTestVerticleResponseHandler(container));
  }


  @Override
  public void start() {
    // Make sure we call initialize() - this sets up the assert stuff so
    // assert functionality works correctly
    initialize();
    // Deploy the module - the System property `vertx.modulename` will
    // contain the name of the module so you
    // don't have to hardecode it in your tests
    container.logger().info(System.getProperty("vertx.modulename"));
    container.deployVerticle(HttpTestVerticle.VERTICLE_NAME,
        new AsyncResultHandler<String>() {
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
