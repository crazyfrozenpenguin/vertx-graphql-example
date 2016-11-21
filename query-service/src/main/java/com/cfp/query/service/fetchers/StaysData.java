package com.cfp.query.service.fetchers;

import graphql.schema.DataFetcher;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;

import static java.util.Objects.isNull;
import static org.slf4j.LoggerFactory.getLogger;

public class StaysData {

  private static final Logger log = getLogger(StaysData.class);

  private static OkHttpClient staysClient;
  private static int staysPort;
  private static String staysHost;

  public static DataFetcher getStaysByIdDataFetcher = environment -> {
    final Long id = environment.getArgument("id");
    Call call = staysHttpClient("/stays/" + id);
    JsonObject json;
    try {
      json = new JsonObject(call.execute().body().string());
    } catch (Exception e) {
      json = new JsonObject();
    }
    return json.getMap();
  };

  public static DataFetcher getStaysDataFetcher = environment -> {
    Call call = staysHttpClient("/stays");
    JsonArray json;
    try {
      json = new JsonArray(call.execute().body().string());
    } catch (Exception e) {
      json = new JsonArray();
    }
    return json.getList();
  };

  private static Call staysHttpClient(final String path) {
    if (isNull(staysClient)) {
      staysClient = new OkHttpClient();
      final JsonObject conf = new JsonObject(); //DigitalExperienceSchema.vertx.getOrCreateContext().config();
      staysPort = conf.getInteger("stays.http.port", 8082);
      staysHost = conf.getString("stays.http.host", "localhost");
    }
    Request request = new Request.Builder()
      .url("http://" + staysHost + ":" + staysPort + path)
      .get()
      .build();
    return staysClient.newCall(request);
  }
}