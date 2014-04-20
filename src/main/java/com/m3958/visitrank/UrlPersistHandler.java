package com.m3958.visitrank;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

import com.m3958.visitrank.Utils.AppConfig;
import com.m3958.visitrank.mongocmd.UrlMongoCmd;
import com.m3958.visitrank.mongocmd.VisitMongoCmd;
import com.m3958.visitrank.rediscmd.INCR;


public class UrlPersistHandler implements Handler<Message<JsonObject>> {

  private Logger log;
  private EventBus eb;
  private JsonObject rqJso;
  private AppConfig appConfig;

  public UrlPersistHandler(AppConfig appConfig, EventBus eb, Logger log, JsonObject rqJso) {
    this.appConfig = appConfig;
    this.eb = eb;
    this.log = log;
    this.rqJso = rqJso;
  }

  @Override
  public void handle(Message<JsonObject> message) {
    JsonObject siteFindResultBody = message.body();
    if ("ok".equals(siteFindResultBody.getString("status"))) {
      final JsonObject msite = siteFindResultBody.getObject("result");
      if (msite != null) {
        JsonObject findUrlCmd = new UrlMongoCmd(UrlPersistHandler.this.rqJso).findOneCmd();
        increaseSiteCounter(msite);
        eb.send(appConfig.getMongoAddress(), findUrlCmd, new Handler<Message<JsonObject>>() {
          @Override
          public void handle(Message<JsonObject> urlresult) {
            JsonObject urlFindResultBody = urlresult.body();
            if ("ok".equals(urlFindResultBody.getString("status"))) {
              JsonObject murl = urlFindResultBody.getObject("result");
              if (murl != null) {
                eb.send(appConfig.getMongoAddress(),
                    new VisitMongoCmd(UrlPersistHandler.this.rqJso).saveCmd());
              } else {
                eb.send(appConfig.getMongoAddress(),
                    new UrlMongoCmd(UrlPersistHandler.this.rqJso).saveCmd(),
                    new Handler<Message<JsonObject>>() {

                      @Override
                      public void handle(Message<JsonObject> saveResultMessage) {
                        JsonObject saveUrlResultBody = saveResultMessage.body();
                        if ("ok".equals(saveUrlResultBody.getString("status"))) {
                          eb.send(appConfig.getMongoAddress(), new VisitMongoCmd(
                              UrlPersistHandler.this.rqJso).saveCmd());
                        } else {
                          log.error(saveUrlResultBody.getString("message"));
                        }
                      }
                    });
              }
            } else {
              log.error(urlFindResultBody.getString("message"));
            }
          }
        });
      }
    } else {
      log.error(siteFindResultBody.getString("message"));
    }
  }

  private void increaseSiteCounter(JsonObject msite) {
    String siteid = msite.getString("_id");
    eb.send(appConfig.getMongoAddress(), new INCR(siteid).getCmd());
  }

}
