package com.cfp.query.service.fetchers;

import java.util.Map;

import graphql.schema.DataFetcher;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;

import static java.util.Objects.isNull;
import static org.slf4j.LoggerFactory.getLogger;

public class HotelsData {

  private static final Logger log = getLogger(HotelsData.class);

  private static OkHttpClient hotelsClient;
  private static int hotelsPort;
  private static String hotelsHost;

  public static DataFetcher getHotelsByIdDataFetcher = environment -> {
    Long id = environment.getArgument("id");
    if (isNull(id)) {
      id = Long.valueOf((Integer)((Map<String, Object>) environment.getSource()).get("hotel"));
    }
    Call call = hotelsHttpClient("/hotels/" + id);
    JsonObject json;
    try {
      json = new JsonObject(call.execute().body().string());
    } catch (Exception e) {
      json = new JsonObject();
    }
    return json.getMap();
  };

  public static DataFetcher getHotelsDataFetcher = environment -> getHotels().getList();

  public static JsonArray getHotels() {
    Call call = hotelsHttpClient("/hotels");
    JsonArray json;
    try {
      json = new JsonArray(call.execute().body().string());
    } catch (Exception e) {
      json = new JsonArray();
    }
    return json;
  }

  private static Call hotelsHttpClient(final String path) {
    if (isNull(hotelsClient)) {
      hotelsClient = new OkHttpClient();
      final JsonObject conf = new JsonObject(); //DigitalExperienceSchema.vertx.getOrCreateContext().config();
      hotelsPort = conf.getInteger("hotels.http.port", 8081);
      hotelsHost = conf.getString("hotels.http.host", "localhost");
    }
    Request request = new Request.Builder()
      .url("http://" + hotelsHost + ":" + hotelsPort + path)
      .get()
      .build();
    return hotelsClient.newCall(request);
  }
}