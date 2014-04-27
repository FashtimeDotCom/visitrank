package com.m3958.visitrank.integration.java;

/*
 * 
 * @author jianglibo@gmail.com
 */

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import com.m3958.visitrank.AppConfigVerticle;
import com.m3958.visitrank.MainVerticle;

/**
 */
public class DeployTest extends TestVerticle {
  
  @Test
  public void t1(){
    Assert.assertTrue(true);
  }

//  @Test
//  public void t1() throws ClientProtocolException, IOException {
//    vertx.eventBus().send(AppConfigVerticle.VERTICLE_ADDRESS, new JsonObject().putString("action", ""),new Handler<Message<JsonObject>>() {
//      
//      @Override
//      public void handle(Message<JsonObject> configJson) {
//        Assert.assertTrue(true);
//        try {
//          Thread.sleep(20000);
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//        VertxAssert.testComplete();
//      }
//    });
//
//  }


  @Override
  public void start() {
    // Make sure we call initialize() - this sets up the assert stuff so
    // assert functionality works correctly
    initialize();
    // Deploy the module - the System property `vertx.modulename` will
    // contain the name of the module so you
    // don't have to hardecode it in your tests
    container.deployVerticle(MainVerticle.VERTICLE_NAME, new AsyncResultHandler<String>() {
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
