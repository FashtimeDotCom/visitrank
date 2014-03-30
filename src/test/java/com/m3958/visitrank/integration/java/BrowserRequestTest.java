package com.m3958.visitrank.integration.java;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import com.m3958.visitrank.AppConstants;

public class BrowserRequestTest extends TestVerticle {

  @Test
  public void testNoSiteid() {
    /**
     * tes no siteid parameter,should return 0,pagevisit should not increase.
     */
    AppConstants.HTTP_PORT = 8334;
    vertx.createHttpClient().setPort(AppConstants.HTTP_PORT)
        .getNow("/", new Handler<HttpClientResponse>() {
          @Override
          public void handle(HttpClientResponse resp) {
            assertEquals(200, resp.statusCode());
            resp.bodyHandler(new Handler<Buffer>() {
              @Override
              public void handle(Buffer respStr) {
                assertEquals("0", respStr.toString());
                VertxAssert.testComplete();
              }
            });
          }
        });
  }

  @Test
  public void testWithSiteidNoReferer() {
    AppConstants.HTTP_PORT = 8334;
    vertx.createHttpClient().setPort(AppConstants.HTTP_PORT)
        .getNow("/?siteid=" + TestConstants.DEMO_SITEID, new Handler<HttpClientResponse>() {
          @Override
          public void handle(HttpClientResponse resp) {
            assertEquals(200, resp.statusCode());
            resp.bodyHandler(new Handler<Buffer>() {
              @Override
              public void handle(Buffer respStr) {
                assertEquals("0", respStr.toString());
                VertxAssert.testComplete();
              }
            });
          }
        });
  }

  @Test
  public void testWithSiteid() {
    AppConstants.HTTP_PORT = 8334;
    HttpClient client =
        vertx.createHttpClient().setHost("localhost").setPort(AppConstants.HTTP_PORT);

    HttpClientRequest request =
        client.get("/?siteid=" + TestConstants.DEMO_SITEID + "&record=true", new Handler<HttpClientResponse>() {
          @Override
          public void handle(HttpClientResponse resp) {
            assertEquals(200, resp.statusCode());
            resp.bodyHandler(new Handler<Buffer>() {
              @Override
              public void handle(Buffer respStr) {
                assertTrue(Long.valueOf(respStr.toString()) > 0);
                VertxAssert.testComplete();
              }
            });
          }
        });
    request.putHeader("referer", "http://www.example.com");
    request.end();
  }

  @Override
  public void start() {
    initialize();
    startTests();
    AppConstants.HTTP_INSTANCE = 1;
    AppConstants.HTTP_PORT = 8334;
    container.deployVerticle(AppConstants.COUNTER_VERTICLE_NAME, new AsyncResultHandler<String>() {
      @Override
      public void handle(AsyncResult<String> asyncResult) {
        assertTrue(asyncResult.succeeded());
        assertNotNull("deploymentID should not be null", asyncResult.result());
        // If deployed correctly then start the tests!
        container.logger().info("startTests()");
        
        JsonObject redisCfg = new JsonObject();
        redisCfg.putString("address", AppConstants.MOD_REDIS_ADDRESS)
            .putString("host", AppConstants.REDIS_HOST).putString("encode", "UTF-8")
            .putNumber("port", AppConstants.REDIS_PORT);

        container.deployModule(AppConstants.REDIS_MODULE_NAME, redisCfg, AppConstants.REDIS_INSTANCE,
            new AsyncResultHandler<String>() {
              @Override
              public void handle(AsyncResult<String> asyncResult) {
                if (asyncResult.succeeded()) {
                  startTests();
                } else {
                }
              }
            });
      }
    });
  }
}


// vertx.eventBus().send(MainVerticle.MOD_MONGO_PERSIST_ADDRESS,
// new VisitMongoCmd().getSiteAllCmd(TestConstants.DEMO_SITEID),
// new Handler<Message<JsonObject>>() {
//
// @Override
// public void handle(Message<JsonObject> saveResultMessage) {
// JsonObject countSiteResultBody = saveResultMessage.body();
// beforePagevisitCount = countSiteResultBody.getLong("count");
// container.logger().info("beforePagevisitCount: " + beforePagevisitCount);
// beforeCalled = true;
// if (afterCalled) {
// VertxAssert.assertEquals(beforePagevisitCount, afterPagevisitCount);
// testComplete();
// }
// }
// });
