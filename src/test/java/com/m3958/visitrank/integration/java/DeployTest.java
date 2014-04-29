package com.m3958.visitrank.integration.java;

/*
 * 
 * @author jianglibo@gmail.com
 */

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

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
import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.LogCheckVerticle;
import com.m3958.visitrank.LogProcessorWorkVerticle;
import com.m3958.visitrank.LogSaverVerticle;
import com.m3958.visitrank.MainVerticle;
import com.m3958.visitrank.SecondStartVerticle;

/**
 */
public class DeployTest extends TestVerticle {
  

  @Test
  public void t1() throws ClientProtocolException, IOException {
    
    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    vertx.eventBus().send(AppConfigVerticle.VERTICLE_ADDRESS, new JsonObject().putString("action", ""),new Handler<Message<JsonObject>>() {
      
      @Override
      public void handle(Message<JsonObject> configJson) {
        ConcurrentMap<String,String> deployedMap = vertx.sharedData().getMap(AppConstants.DEPLOYED_SHARED_MAP);
        Assert.assertNotNull(deployedMap.get(SecondStartVerticle.VERTICLE_NAME));
        Assert.assertNotNull(deployedMap.get(AppConfigVerticle.VERTICLE_NAME));
        Assert.assertNotNull(deployedMap.get("mongo-persistor"));
        Assert.assertNotNull(deployedMap.get("redis"));
        Assert.assertNotNull(deployedMap.get(LogProcessorWorkVerticle.VERTICLE_NAME));
        Assert.assertNotNull(deployedMap.get(LogSaverVerticle.VERTICLE_NAME));
        Assert.assertNotNull(deployedMap.get(LogCheckVerticle.VERTICLE_NAME));
//        Assert.assertNotNull(deployedMap.get(CounterVerticle.VERTICLE_NAME));
        VertxAssert.testComplete();
      }
    });
  }


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
