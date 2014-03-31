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
  
  private String callbackPtnStr = "^abc\\(\\d+\\);$";
  
  private String dwPtnStr = "document.write\\(\\d+\\);$";

  @Test
  public void t() {
    AppConstants.HTTP_PORT = 8334;
    HttpClient client =
        vertx.createHttpClient().setHost("localhost").setPort(AppConstants.HTTP_PORT);

    HttpClientRequest request =
        client.get("/?record=true&silent=true", new Handler<HttpClientResponse>() {
          @Override
          public void handle(HttpClientResponse resp) {
            assertEquals(200, resp.statusCode());
            resp.bodyHandler(new Handler<Buffer>() {
              @Override
              public void handle(Buffer respStr) {
                assertEquals("", respStr.toString());
                VertxAssert.testComplete();
              }
            });
          }
        });
    request.putHeader("referer", "http://www.example.com/");
    request.end();
  }
  
  @Test
  public void t1() {
    AppConstants.HTTP_PORT = 8334;
    HttpClient client =
        vertx.createHttpClient().setHost("localhost").setPort(AppConstants.HTTP_PORT);

    HttpClientRequest request =
        client.get("/?&record=true&callback=abc", new Handler<HttpClientResponse>() {
          @Override
          public void handle(HttpClientResponse resp) {
            assertEquals(200, resp.statusCode());
            resp.bodyHandler(new Handler<Buffer>() {
              @Override
              public void handle(Buffer respStr) {
                assertTrue(respStr.toString().matches(callbackPtnStr));
                VertxAssert.testComplete();
              }
            });
          }
        });
    request.putHeader("referer", "http://www.example.com");
    request.end();
  }
  
  @Test
  public void t3() {
    AppConstants.HTTP_PORT = 8334;
    HttpClient client =
        vertx.createHttpClient().setHost("localhost").setPort(AppConstants.HTTP_PORT);

    HttpClientRequest request =
        client.get("/?&record=true", new Handler<HttpClientResponse>() {
          @Override
          public void handle(HttpClientResponse resp) {
            assertEquals(200, resp.statusCode());
            resp.bodyHandler(new Handler<Buffer>() {
              @Override
              public void handle(Buffer respStr) {
                assertTrue(respStr.toString().matches(dwPtnStr));
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
