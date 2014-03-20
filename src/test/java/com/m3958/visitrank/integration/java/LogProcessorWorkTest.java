package com.m3958.visitrank.integration.java;

/*
 * Copyright 2013 Red Hat, Inc.
 * 
 * Red Hat licenses this file to you under the Apache License, version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

/**
 * Example Java integration test that deploys the module that this project builds.
 * 
 * Quite often in integration tests you want to deploy the same module for all tests and you don't
 * want tests to start before the module has been deployed.
 * 
 * This test demonstrates how to do that.
 */
public class LogProcessorWorkTest extends TestVerticle {

  @Test
  public void testSaveTestSite() {
    vertx.eventBus().send(
      "log-processor",new JsonObject(),
        new Handler<Message<JsonObject>>() {
          @Override
          public void handle(Message<JsonObject> message) {
            JsonObject body = message.body();
            if ("ok".equals(body.getString("status"))) {
              VertxAssert.testComplete();
            } else {
              container.logger().error(body);
              container.logger().error(body.getString("message"));
              VertxAssert.testComplete();
            }
          }
        });
  }

  @Override
  public void start() {
    initialize();

    container.deployWorkerVerticle("com.m3958.visitrank.LogProcessorWorkVerticle",
        new JsonObject(), 1, false, new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            // Deployment is asynchronous and this this handler will be called when it's complete
            // (or
            // failed)
            assertTrue(asyncResult.succeeded());
            assertNotNull("deploymentID should not be null", asyncResult.result());
            // If deployed correctly then start the tests!
            startTests();
          }
        });

  }
}
