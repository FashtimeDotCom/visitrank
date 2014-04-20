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

import com.m3958.visitrank.AppConstants;
import com.m3958.visitrank.LogProcessorWorkVerticle;
import com.m3958.visitrank.LogProcessorWorkVerticle.LogProcessorWorkMsgKey;
import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.Utils.AppUtils;
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

  private AppConfig appConfig;

  @Test
  public void t() throws IOException {
    TestUtils.assertDbItemEqual(appConfig, appConfig.getRepoDbName(), 1000);

    TestUtils.deleteDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
    TestUtils.dropDb(appConfig, appConfig.getRepoDbName());
    VertxAssert.testComplete();
  }

  @Override
  public void start() {
    initialize();
    appConfig = new AppConfig(AppUtils.loadJsonResourceContent(this.getClass(), "testconf.json"));
    try {
      TestUtils.deleteDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
      TestUtils.dropDb(appConfig, appConfig.getRepoDbName());
      TestUtils.createDirs(appConfig.getLogDir(), appConfig.getArchiveDir());
      TestUtils.createSampleLogs(appConfig.getLogDir(), testlogname, 1000);
    } catch (IOException e) {
      e.printStackTrace();
    }

    final JsonObject body =
        new JsonObject().putString(LogProcessorWorkMsgKey.FILE_NAME, testlogname);

    container.deployWorkerVerticle(LogProcessorWorkVerticle.VERTICLE_NAME,
        new JsonObject().putObject(AppConstants.TEST_CONF_KEY, appConfig.getConfJson()), 1, false,
        new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            assertTrue(asyncResult.succeeded());
            vertx.eventBus().send(LogProcessorWorkVerticle.VERTICLE_ADDRESS, body,
                new Handler<Message<String>>() {
                  @Override
                  public void handle(Message<String> ar) {
                    startTests();
                  }
                });
          }
        });
  }
}
