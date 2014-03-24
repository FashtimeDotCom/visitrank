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

import java.io.IOException;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import com.m3958.visitrank.LogProcessorWorkVerticle.LogProcessorWorkCfgKey;
import com.m3958.visitrank.testutils.TestUtils;


/**
 * Example Java integration test that deploys the module that this project builds.
 * 
 * Quite often in integration tests you want to deploy the same module for all tests and you don't
 * want tests to start before the module has been deployed.
 * 
 * This test demonstrates how to do that.
 */
public class LogProcessorWorkTest extends TestVerticle {

  private String testlogname = "t-2014-03-02-01.log";
  private String logDir = "testlogs";
  private String archiveDir = "tarchives";


  @Test
  public void t() throws IOException {
    TestUtils.assertDbItemEqual(testlogname);
    
    TestUtils.deleteTestDirs(logDir, archiveDir);
    TestUtils.dropDailyDb(testlogname);
    VertxAssert.testComplete();
  }

  @Override
  public void start() {
    initialize();
    try {
      TestUtils.deleteTestDirs(logDir, archiveDir);
      TestUtils.dropDailyDb(testlogname);
      TestUtils.createDirs(logDir, archiveDir);
      TestUtils.createSampleLogs(logDir, testlogname);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    JsonObject lpCfg =
        new JsonObject().putString(LogProcessorWorkCfgKey.FILE_NAME, testlogname)
            .putString(LogProcessorWorkCfgKey.ADDRESS, testlogname)
            .putString(LogProcessorWorkCfgKey.LOG_DIR, logDir)
            .putString(LogProcessorWorkCfgKey.ARCHIVE_DIR, archiveDir)
            .putBoolean(LogProcessorWorkCfgKey.REPLY, true);
    container.deployWorkerVerticle("com.m3958.visitrank.LogProcessorWorkVerticle", lpCfg, 1, false,
        new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            assertTrue(asyncResult.succeeded());
            String deployId = asyncResult.result();
            vertx.eventBus().send(testlogname, deployId, new Handler<Message<String>>() {

              @Override
              public void handle(Message<String> ar) {
                startTests();
              }
            });
          }
        });
  }
}
