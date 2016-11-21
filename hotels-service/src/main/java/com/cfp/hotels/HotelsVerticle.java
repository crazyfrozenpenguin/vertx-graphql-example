package com.cfp.hotels;

import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.ext.web.Router;

import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.nonNull;

/**
 * REST Endpoint to simulate simple hotels.
 */
public class HotelsVerticle extends AbstractVerticle {
  private static final Logger log = getLogger(HotelsVerticle.class);

  @Override
  public void start(final Future<Void> startFuture) throws Exception {

    final Router router = Router.router(vertx);

    router.getWithRegex("/|/hotels").handler(req -> {
      log.info("GET /hotels");
      req.response().putHeader("content-type", "application/json").end(config().getJsonArray("hotels").toString());
    });

    router.get("/hotels/:id").handler(req -> {
      Integer id = getIntegerValue(req.request().getParam("id"));
      log.info("GET /hotels/" + id);

      List hotels = config().getJsonArray("hotels", new JsonArray()).getList();
      if (nonNull(id) && id < hotels.size()) {
        req.response().putHeader("content-type", "application/json").end(hotels.get(id).toString());
      } else {
        req.response().setStatusCode(404).setStatusMessage("Resource not found").end();
      }
    });

    vertx.createHttpServer().requestHandler(router::accept).listen(8081);
  }

  private Integer getIntegerValue(String value) {
    try {
      return Integer.valueOf(value);
    } catch (Exception e) {}
    return null;
  }
}
