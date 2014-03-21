package com.m3958.visitrank;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.m3958.visitrank.logger.AppLogger;

/**
 * when timer detect an logfile,it will send a message to this verticle, this verticle will deploy
 * an mongodb listener,then deploy a logprocessverticle, then send a message to logprocessverticle.
 * 
 * @author jianglibo@gmail.com
 * 
 */
public class DynamicDeployerVerticle extends Verticle {


  public static String DYNAMIC_DEPLOYER;

  public void start() {
    vertx.eventBus().registerHandler(DYNAMIC_DEPLOYER, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        String filename = message.body().getString("filename");
        int ix = filename.indexOf('.');
        if (ix != -1) {
          filename = filename.substring(0, ix);
        }
        final String fn = filename;
        final String fnNoExt = filename;
        final String mongodbArress = "mongo-" + fnNoExt;
        
        container.deployVerticle(AppConstants.MONGODB_MODULE_NAME, getMongoCfg(mongodbArress), 1,
            new Handler<AsyncResult<String>>() {
              @Override
              public void handle(AsyncResult<String> ar) {
                if (ar.succeeded()) {
                  final String mdeployid = ar.result();
                  final String logprocessorAddress = "lp-" + fnNoExt;
                  JsonObject jo =
                      new JsonObject().putString("mongodeployid", mdeployid).putString("filename",
                          fn).putString("address", logprocessorAddress).putString("mongoAddress", mongodbArress);
                  
                  
                  container.deployVerticle("com.m3958.visitrank.LogProcessorWorkVerticle", jo, 1,
                      new Handler<AsyncResult<String>>() {
                        @Override
                        public void handle(AsyncResult<String> ar) {
                          if(ar.succeeded()){
                            vertx.eventBus().send(logprocessorAddress,ar.result());
                          }else{
                            AppLogger.deployError.info("logprocessor deploy:" + fnNoExt + " failure");
                          }
                        }
                      });

                } else {
                  AppLogger.deployError.info("mongodb deploy:" + fnNoExt + " failure");
                }
              }
            });

      }
    });
    container.logger().info("MongodbDeployerVerticle started");
  }


  public JsonObject getMongoCfg(String mongodbArress) {
    JsonObject mongodbCfg = new JsonObject();
    mongodbCfg.putString("address", mongodbArress).putString("host", AppConstants.MONGODB_HOST)
        .putString("db_name", mongodbArress).putNumber("port", AppConstants.MONGODB_PORT);
    return mongodbCfg;
  }
}
