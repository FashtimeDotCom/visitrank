package com.m3958.visitrank.Utils;

import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.uaparser.Client;
import com.m3958.visitrank.uaparser.Device;
import com.m3958.visitrank.uaparser.OS;
import com.m3958.visitrank.uaparser.UserAgent;

public class UaParserClientWrapper {
  private Client client;

  public UaParserClientWrapper(Client client) {
    this.client = client;
  }

  public JsonObject toJson() {
    JsonObject jo = new JsonObject();
    jo.putObject("user_agent", getUserAgent());
    jo.putObject("os", getOs());
    jo.putObject("device", getDevice());
    return jo;
  }

  private JsonObject getUserAgent() {
    JsonObject jo = new JsonObject();
    UserAgent ua = client.userAgent;
    jo.putString("family", ua.family);
    jo.putString("major", ua.major);
    jo.putString("minor", ua.minor);
    jo.putString("patch", ua.patch);
    return jo;
  }

  private JsonObject getOs() {
    JsonObject jo = new JsonObject();
    OS os = client.os;
    jo.putString("family", os.family);
    jo.putString("major", os.major);
    jo.putString("minor", os.minor);
    jo.putString("patch", os.patch);
    jo.putString("patch_minor", os.patchMinor);
    return jo;
  }
  
  private JsonObject getDevice(){
    JsonObject jo = new JsonObject();
    Device device = client.device;
    jo.putString("family", device.family);
    return jo;
  }

  public Client getClient() {
    return client;
  }



}
