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

import static org.vertx.testtools.VertxAssert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
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
  
  public String filename = "2014-03-02-01.log";
  
  public String deployId;
  
  @Before
  public void setup(){
    deployId = null;
  }
  
  @Test
  public void t() {
    VertxAssert.assertNotNull(deployId);
//    vertx.eventBus().send(filename, deployId);
  }

  @Override
  public void start() {
    initialize();
    JsonObject lpCfg =
        new JsonObject().putString("filename", filename).putString("address", filename);
    container.deployWorkerVerticle("com.m3958.visitrank.LogProcessorWorkVerticle",
        lpCfg, 1, false, new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            assertTrue(asyncResult.succeeded());
            deployId = asyncResult.result();
            startTests();
          }
        });
  }
}
