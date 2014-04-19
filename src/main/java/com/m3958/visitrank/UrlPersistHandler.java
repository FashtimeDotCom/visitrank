package com.m3958.visitrank;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

import com.m3958.visitrank.mongocmd.UrlMongoCmd;
import com.m3958.visitrank.mongocmd.VisitMongoCmd;
import com.m3958.visitrank.rediscmd.INCR;


public class UrlPersistHandler implements Handler<Message<JsonObject>> {

  private Logger log;
  private EventBus eb;
  private JsonObject rqJso;

  public UrlPersistHandler(EventBus eb, Logger log, JsonObject rqJso) {
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
        eb.send(AppConstants.MONGO_ADDRESS, findUrlCmd,
            new Handler<Message<JsonObject>>() {
              @Override
              public void handle(Message<JsonObject> urlresult) {
                JsonObject urlFindResultBody = urlresult.body();
                if ("ok".equals(urlFindResultBody.getString("status"))) {
                  JsonObject murl = urlFindResultBody.getObject("result");
                  if (murl != null) {
                    eb.send(AppConstants.MONGO_ADDRESS, new VisitMongoCmd(
                        UrlPersistHandler.this.rqJso).saveCmd());
                  } else {
                    eb.send(AppConstants.MONGO_ADDRESS, new UrlMongoCmd(
                        UrlPersistHandler.this.rqJso).saveCmd(),
                        new Handler<Message<JsonObject>>() {

                          @Override
                          public void handle(Message<JsonObject> saveResultMessage) {
                            JsonObject saveUrlResultBody = saveResultMessage.body();
                            if ("ok".equals(saveUrlResultBody.getString("status"))) {
                              eb.send(AppConstants.MONGO_ADDRESS, new VisitMongoCmd(
                                  UrlPersistHandler.this.rqJso)
                                  .saveCmd());
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
    eb.send(AppConstants.MOD_REDIS_ADDRESS, new INCR(siteid).getCmd());
  }

}
